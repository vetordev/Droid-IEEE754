package ieee754lib;

import java.util.ArrayList;
import java.util.Collections;

public class Conversor {

    static int POSITIVO = 0;
    static int NEGATIVO = 1;

    static int BITS32 = 32;
    static int MANTISSA32BITS = 23;
    static int BIAS32BITS = 127;

    static int BITS64 = 64;
    static int MANTISSA64BITS = 52;
    static int BIAS64BITS = 1023;

    protected Double parteDecimal(double decimal) {
        return decimal - (int) decimal;
    }
    
    protected Bits decimalParaBin(double decimal, int alcance) {
        Bits bits = new Bits();

        int contador = alcance;

        while (contador >= -1) {
            double frac = parteDecimal(decimal) * 2;
            bits.add((int) frac);

            decimal = frac;

            Integer soma = bits.stream().reduce(0, Integer::sum);

            if (soma == 0) {
                contador += 1;
            }

            contador -= 1;
        }

        return bits;
    }

    protected Bits inteiroParaBin(int valor) {
        Bits bits = new Bits();

        while (valor != 1 && valor != 0) {
            bits.add(valor % 2);
            valor >>= 1;
        }

        bits.add(valor);

        Collections.reverse(bits);

        return bits;
    }

    protected int binParaInteiro(Bits bits) {
        int valor = 0;

        Collections.reverse(bits);

        for (int i = 0; i < bits.size(); i++) {
            int bit = bits.get(i);
            if (bit != 0) valor += Math.pow(2, i);
        }

        return valor;
    }

    protected String gabaritoParaHex(Bits gabarito) {
        String[] hexmap = new String[] {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
        };

        ArrayList<String> hex = new ArrayList<String>();

        for (int i = 0; i < gabarito.size() - 3; i += 4) {
            Bits bits = new Bits();
            bits.addAll(gabarito.subList(i, i + 4));

            int valor = binParaInteiro(bits);

            hex.add(hexmap[valor]);
        }

        return String.join("", hex);
    }

    protected Pair normalizar(Bits parteInteira, Bits parteDecimal) {
        Bits mantissa = new Bits();
        int deslocamento = 0;

        //Deslocamento da "vírgula" para direita
        if (parteInteira.size() == 1 && parteInteira.get(0) == 0) {
            for (int i = 0; i < parteDecimal.size(); i++) {
                deslocamento++;
                if (parteDecimal.get(i) == 1) break;
            }

            mantissa.addAll(parteDecimal.subList(deslocamento, parteDecimal.size()));
            deslocamento *= -1;
        } else {
            deslocamento = parteInteira.size() - 1;

            parteInteira.remove(0);
            parteInteira.addAll(parteDecimal);

            mantissa.addAll(parteInteira);
        }

        return new Pair<Bits, Integer>(mantissa, deslocamento);
    }

    protected Bits pegarExpoente(int deslocamento, int BIAS) {
        Bits bits = inteiroParaBin(BIAS + deslocamento);

        if (deslocamento <= 0) bits.add(0, 0);

        return bits;
    }

    protected Bits montarGabarito(int sinal, Bits expoente, Bits mantissa) {
        Bits gabarito = new Bits();
        gabarito.add(sinal);

        gabarito.addAll(expoente);
        gabarito.addAll(mantissa);

        return gabarito;
    }

    public String ConverterParaIEEE754(double decimal, int quantidadeBits) {
        if (decimal == 0) return "0";

        int sinal = Conversor.POSITIVO;

        if (decimal < 0) {
            sinal = Conversor.NEGATIVO;
            decimal *= -1;
        }

        if (quantidadeBits != Conversor.BITS32 && quantidadeBits != Conversor.BITS64) {
            throw new Error("Quantidade de bits inválida! Os valores suportados são: 32 e 64.");
        }

        int tamanhoMantissa = Conversor.MANTISSA32BITS;
        int BIAS = Conversor.BIAS32BITS;

        if (quantidadeBits == Conversor.BITS64) {
            tamanhoMantissa = Conversor.MANTISSA64BITS;
            BIAS = Conversor.BIAS64BITS;
        }

        int parteInteira = (int) decimal;
        double parteDecimal = parteDecimal(decimal);

        Bits bitsInteiros = inteiroParaBin(parteInteira);
        Bits bitsDecimais = decimalParaBin(parteDecimal, tamanhoMantissa - bitsInteiros.size());

        // contém mantissa e deslocamento
        Pair<Bits, Integer> pair = normalizar(bitsInteiros, bitsDecimais);
        Bits expoente = pegarExpoente(pair.second, BIAS);

        Bits gabarito = montarGabarito(sinal, expoente, pair.first);

        return gabaritoParaHex(gabarito);

    }
}

class Pair<U, V> {
    U first;
    V second;

    public Pair(U first, V second) {

        this.first = first;
        this.second = second;
    }
}

class Bits extends ArrayList<Integer>{}