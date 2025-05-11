package com.example.bar_retina_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        addMoreButton.setOnClickListener(v->{
            finish();
        });

        orderRecycler.setLayoutManager(new LinearLayoutManager(this));
        OrderAdapter orderAdapter = new OrderAdapter(() -> dataChanged());
        orderRecycler.setAdapter(orderAdapter);

        totalAmount.setText(String.format("%.2f€",AppData.getInstance().getTotalBill()));
    }

    private void dataChanged() {
        totalAmount.setText(String.format("%.2f€",AppData.getInstance().getTotalBill()));
    }
}