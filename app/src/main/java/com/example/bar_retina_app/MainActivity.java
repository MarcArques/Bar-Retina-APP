package com.example.bar_retina_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RecyclerView tagsRecycler;
    private Button orderButton;
    private Button goBackButton;
    private TagAdapter tagsAdapter;
    private ImageButton settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        tagsRecycler = findViewById(R.id.tagsRecycler);
        orderButton = findViewById(R.id.orderButton);
        goBackButton = findViewById(R.id.backButton);
        settingsButton = findViewById(R.id.settingsButton);

        orderButton.setOnClickListener(v -> openOrderScreen());
        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TablesActivity.class);
            startActivity(intent);
        });
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CtrlConfig.class);
            startActivity(intent);
        });

        handleConfig();
        setupRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        float total = AppData.getInstance().table.getTotalBill();
        orderButton.setText(String.format("Total: %.2f€", total));

        AppData.getInstance().setOnDataChangedListener(new AppData.OnDataChangedListener() {
            @Override
            public void onTablesChanged() {
                // No hace falta aquí
            }

            @Override
            public void onProductsChanged() {
                runOnUiThread(() -> {
                    if (tagsAdapter != null) tagsAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onCurrentTableChanged() {
                runOnUiThread(() -> {
                    float total = AppData.getInstance().table.getTotalBill();
                    orderButton.setText(String.format("Total: %.2f€", total));
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppData.getInstance().clearListener();
    }

    private void handleConfig() {
        if (UtilsConfigXML.configExists(this)) {
            String[] config = UtilsConfigXML.readConfig(this);
            if (config != null) {
                conectarAlServidor();
            } else {
                abrirPantallaConfig();
            }
        } else {
            abrirPantallaConfig();
        }
    }

    private void conectarAlServidor() {
        UtilsWS wsClient = UtilsWS.getSharedInstance();

        try {
            JSONObject request = new JSONObject();
            request.put("type", "getAllProductes");
            wsClient.send(request.toString());
            Log.d("WS", "Product request sent");
        } catch (JSONException e) {
            Log.e("WS", "Error creando petición de productos");
        }
    }

    private void abrirPantallaConfig() {
        Intent intent = new Intent(this, CtrlConfig.class);
        startActivity(intent);
        finish();
    }

    private void openOrderScreen() {
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }

    private void setupRecycler() {
        tagsRecycler.setLayoutManager(new LinearLayoutManager(this));
        tagsAdapter = new TagAdapter(this, () -> {
            float total = AppData.getInstance().table.getTotalBill();
            orderButton.setText(String.format("Total: %.2f€", total));
        });
        tagsRecycler.setAdapter(tagsAdapter);
    }
}
