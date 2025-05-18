package com.example.bar_retina_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class TablesActivity extends AppCompatActivity {

    private String urlServidor;
    private String nombreCamarero;
    private RecyclerView tableRecycler;
    private TableAdapter adapter;
    private ImageButton settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tables);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        tableRecycler = findViewById(R.id.tablesRecycler);
        tableRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new TableAdapter(this, this::finish);
        tableRecycler.setAdapter(adapter);

        settingsButton = findViewById(R.id.settingsButtonTables);

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CtrlConfig.class);
            startActivity(intent);
        });

        handleConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppData.getInstance().setOnDataChangedListener(new AppData.OnDataChangedListener() {
            @Override
            public void onTablesChanged() {
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }

            @Override
            public void onProductsChanged() {

            }

            @Override
            public void onCurrentTableChanged() {

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
            Toast.makeText(this, "Connectat", Toast.LENGTH_SHORT).show();
            askTables(wsClient);
        }));

        wsClient.onError((error) -> runOnUiThread(() -> {
            Toast.makeText(this, "Error de connexió: " + error, Toast.LENGTH_LONG).show();
            AppData appData = AppData.getInstance();
            appData.tables.clear();
            abrirPantallaConfig();
        }));

        if (wsClient.isOpen()) {
            askTables(wsClient);
        }
    }

    private void askTables(UtilsWS wsClient) {
        try {
            JSONObject rst = new JSONObject();
            rst.put("type", "getTables");
            wsClient.send(rst.toString());
            Log.d("TABLES", "Sent getTables");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void abrirPantallaConfig() {
        Intent intent = new Intent(this, CtrlConfig.class);
        startActivity(intent);
        finish();
    }
}
