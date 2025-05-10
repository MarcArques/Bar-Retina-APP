package com.example.bar_retina_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String urlServidor;
    private String nombreCamarero;
    private RecyclerView tagsRecycler;
    private Button orderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        tagsRecycler = findViewById(R.id.tagsRecycler);
        orderButton = findViewById(R.id.orderButton);

        handleConfig();

    }

    private void handleConfig() {
        if (UtilsConfigXML.configExists(this)) {
            String[] config = UtilsConfigXML.readConfig(this);
            if (config != null) {
                urlServidor = config[0];
                nombreCamarero = config[1];

                conectarAlServidor();
            } else {
                abrirPantallaConfig();
            }
        } else {
            abrirPantallaConfig();
        }
    }

    private void conectarAlServidor() {
        UtilsWS wsClient = UtilsWS.getSharedInstance(urlServidor, nombreCamarero);

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
                    AppData appData = AppData.getInstance();
                    switch (type) {
                        case "allProductes":
                            JSONArray prodcutes = msg.getJSONArray("productes");
                            appData.collectProducts(prodcutes);
                            loadData();
                            break;
                        default:
                            Log.d("ONMESSAGE", "Unknown server type: "+type);
                            break;
                    }
                }else {
                    Log.e("ONMESSAGE", "Response type not found");
                }
            } catch (JSONException e) {
                Log.e("ONMESSAGE", "Incorrect response format");
            }

        }));
    }

    private void abrirPantallaConfig() {
        Intent intent = new Intent(this, CtrlConfig.class);
        startActivity(intent);
        finish();
    }

    private void loadData() {
        tagsRecycler.setLayoutManager(new LinearLayoutManager(this));
        TagAdapter tagsAdapter = new TagAdapter(this, () -> {
            float total = AppData.getInstance().getTotalBill();
            orderButton.setText(String.format("Total: %.2f€", total));
        });
        tagsRecycler.setAdapter(tagsAdapter);
    }
}

