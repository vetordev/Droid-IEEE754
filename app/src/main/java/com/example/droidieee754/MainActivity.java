package com.example.droidieee754;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import ieee754lib.Conversor;

public class MainActivity extends AppCompatActivity {

    private final Conversor conversor;

    public MainActivity() {
        this.conversor = new Conversor();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClick(View view) {
        TextInputEditText editText = findViewById(R.id.decimalInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        try {
            double valor = Double.parseDouble(editText.getText().toString());
            builder.setTitle("Resultado");

            int BITS32 = 32;
            int BITS64 = 64;

            String mensagem = String.format(
                    "32 Bits: %s (hex)\n64 Bits: %s (hex)",
                    this.conversor.ConverterParaIEEE754(valor, BITS32),
                    this.conversor.ConverterParaIEEE754(valor, BITS64)
            );

            builder.setMessage(mensagem);
        } catch (Exception e) {
            builder.setTitle("Erro");
            builder.setMessage("Entrada inválida!");
        }


        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }
}