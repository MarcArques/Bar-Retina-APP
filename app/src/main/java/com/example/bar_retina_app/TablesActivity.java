package com.example.bar_retina_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TablesActivity extends AppCompatActivity {

    private String urlServidor;
    private String nombreCamarero;
    private RecyclerView tableRecycler;
    private TableAdapter adapter;

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

        handleConfig();

        tableRecycler = findViewById(R.id.tablesRecycler);

        tableRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new TableAdapter(this);
        tableRecycler.setAdapter(adapter);
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
            JSONObject rst = new JSONObject();
            try {
                rst.put("type","getTables");
                Toast.makeText(this, "Sent getTables", Toast.LENGTH_SHORT).show();
                wsClient.send(rst.toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }));

        wsClient.onError((error) -> runOnUiThread(() -> {
            Toast.makeText(this, "Error de connexió: " + error, Toast.LENGTH_LONG).show();
            abrirPantallaConfig();
        }));

        wsClient.onMessage((message) -> {
            try {
                JSONObject msg = new JSONObject(message);
                if(msg.has("key")) {
                    String type = msg.getString("key");
                    AppData appData = AppData.getInstance();
                    switch(type) {
                        case "tables":
                            Log.d("TABLES","Received tables");
                            appData.loadTables(msg.getJSONArray("tables"));
                            runOnUiThread(() -> adapter.notifyDataSetChanged());
                            break;
                        case "allProductes":
                            Log.d("PRODUCTS", "Products received");
                            JSONArray productes = msg.getJSONArray("productes");
                            appData.collectProducts(productes);
                            JSONObject rst = new JSONObject();
                            rst.put("type", "getTables");
                            wsClient.send(rst.toString());
                            break;
                        default:
                            break;
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Error reading tables", Toast.LENGTH_LONG).show();
            }

        });
    }

    private void abrirPantallaConfig() {
        Intent intent = new Intent(this, CtrlConfig.class);
        startActivity(intent);
        finish();
    }
}