package com.example.bar_retina_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private String urlServidor;
    private String nombreCamarero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        wsClient.onMessage((message) -> runOnUiThread(() -> {
            try {
                JSONObject msg = new JSONObject(message);
                if(msg.has("type")) {
                    String type = msg.getString("type");
                    switch (type) {
                        case "allProductes":
                            JSONArray array = new JSONArray();
                            break;
                        default:
                            Log.d("ONMESSAGE", "Unknown server type: "+type);
                            break;
                    }
                }else {
                    Log.e("ONMESSAGE", "Response type don't found");
                }
            } catch (JSONException e) {
                Log.e("ONMESSAGE", "Incorrect response format");
            }

        }));
    }

    private void abrirPantallaConfig() {
        Intent intent = new Intent(this, CtrlConfig.class);
        startActivity(intent);
        finish(); // Cerramos MainActivity para que no se vuelva atrás
    }
}
