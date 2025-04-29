package com.example.bar_retina_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CtrlConfig extends AppCompatActivity {

    EditText txtHost;
    EditText txtName;
    TextView txtMessage;
    Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl_config);

        txtHost = findViewById(R.id.txtHost);
        txtName = findViewById(R.id.txtName);
        txtMessage = findViewById(R.id.txtMessage);
        btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(v -> {
            String host = txtHost.getText().toString().trim();
            String name = txtName.getText().toString().trim();

            if (host.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardamos los datos
            UtilsConfigXML.saveConfig(this, host, name);

            // Intentamos conectar
            conectarAlServidor(host);
        });
    }

    private void conectarAlServidor(String host) {
        UtilsWS wsClient = UtilsWS.getSharedInstance(host);

        wsClient.onOpen((message) -> runOnUiThread(() -> {
            Toast.makeText(this, "Conexión exitosa", Toast.LENGTH_SHORT).show();
            // Podrías redirigir a otra actividad aquí si quieres
        }));

        wsClient.onError((error) -> runOnUiThread(() -> {
            Toast.makeText(this, "Error de conexión: " + error, Toast.LENGTH_LONG).show();
            txtMessage.setText("Error de conexión. Revisa la URL.");
        }));
    }
}

