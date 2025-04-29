package com.example.bar_retina_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String urlServidor;
    private String nombreCamarero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (UtilsConfigXML.configExists(this)) {
            // Si CONFIG.XML existe, leemos los datos
            String[] config = UtilsConfigXML.readConfig(this);
            if (config != null) {
                urlServidor = config[0];
                nombreCamarero = config[1];

                conectarAlServidor();
            } else {
                // Si hay error leyendo, vamos a CtrlConfig
                abrirPantallaConfig();
            }
        } else {
            // No existe CONFIG.XML, vamos a pedir datos
            abrirPantallaConfig();
        }
    }

    private void conectarAlServidor() {
        UtilsWS wsClient = UtilsWS.getSharedInstance(urlServidor);

        wsClient.onOpen((message) -> runOnUiThread(() -> {
            Toast.makeText(this, "Conexión exitosa", Toast.LENGTH_SHORT).show();
            // Aquí podrías ir a otra pantalla principal si quieres
        }));

        wsClient.onError((error) -> runOnUiThread(() -> {
            Toast.makeText(this, "Error de conexión: " + error, Toast.LENGTH_LONG).show();
            abrirPantallaConfig();
        }));
    }

    private void abrirPantallaConfig() {
        Intent intent = new Intent(this, CtrlConfig.class);
        startActivity(intent);
        finish(); // Cerramos MainActivity para que no se vuelva atrás
    }
}
