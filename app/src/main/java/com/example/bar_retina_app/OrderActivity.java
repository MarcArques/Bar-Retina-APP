package com.example.bar_retina_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private Button addMoreButton;
    private RecyclerView orderRecycler;
    private TextView totalAmount;
    private Runnable onDataChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addMoreButton = findViewById(R.id.addMoreButton);
        orderRecycler = findViewById(R.id.orderRecycler);
        totalAmount = findViewById(R.id.totalAmount);

        addMoreButton.setOnClickListener(v -> finish());

        orderRecycler.setLayoutManager(new LinearLayoutManager(this));
        OrderAdapter orderAdapter = new OrderAdapter(() -> dataChanged());
        orderRecycler.setAdapter(orderAdapter);

        totalAmount.setText(String.format("%.2f€", AppData.getInstance().table.getTotalBill()));

        Button enviarButton = findViewById(R.id.sendButton); // CORREGIDO

        enviarButton.setOnClickListener(v -> {
            String camarero = UtilsConfigXML.readConfig(this)[1];
            int taula = AppData.getInstance().table.getNumber();
            List<OrderItem> order = AppData.getInstance().table.order;

            UtilsWS ws = UtilsWS.getSharedInstance();
            if (ws != null && ws.isOpen()) {
                ws.enviarComanda(camarero, taula, order);
                JSONObject rst = new JSONObject();
                try {
                    rst.put("type", "getTables");
                    ws.send(rst.toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                finish();
            } else {
                Log.d("ORDER","WS no conectado");
                Toast.makeText(this, "Error: WebSocket no conectado", Toast.LENGTH_LONG).show();
            }

            order.clear();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppData.getInstance().setOnDataChangedListener(new AppData.OnDataChangedListener() {
            @Override
            public void onTablesChanged() {}

            @Override
            public void onProductsChanged() {}

            @Override
            public void onCurrentTableChanged() {
                runOnUiThread(() -> dataChanged());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppData.getInstance().clearListener();
    }


    private void dataChanged() {
        totalAmount.setText(String.format("%.2f€", AppData.getInstance().table.getTotalBill()));
    }
}
