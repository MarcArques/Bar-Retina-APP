package com.example.bar_retina_app;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class UtilsWS {

    private static UtilsWS sharedInstance = null;
    private WebSocketClient client;
    private Consumer<String> onOpenCallBack = null;
    private Consumer<String> onErrorCallBack = null;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String location;
    private String name;

    private ScheduledExecutorService pingScheduler;

    private UtilsWS(String location, String name) {
        this.location = location;
        this.name = name;
        createWebSocketClient();
    }

    private void createWebSocketClient() {
        try {
            client = new WebSocketClient(new URI(location), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    if (onOpenCallBack != null) {
                        mainHandler.post(() -> onOpenCallBack.accept("Conectado correctamente"));
                    }
                    JSONObject rst = new JSONObject();
                    try {
                        rst.put("type", "registration");
                        rst.put("name", name);
                        client.send(rst.toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onMessage(String message) {
                    mainHandler.post(() -> handleServerMessage(message));
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    stopPingRoutine();
                    if (onErrorCallBack != null) {
                        mainHandler.post(() -> onErrorCallBack.accept("Conexión cerrada: " + reason));
                    }
                }

                @Override
                public void onError(Exception ex) {
                    stopPingRoutine();
                    ex.printStackTrace();
                    if (onErrorCallBack != null) {
                        String msg = (ex.getMessage() != null) ? ex.getMessage() : "Error desconocido";
                        mainHandler.post(() -> onErrorCallBack.accept("Error de conexión: " + msg));
                    }
                }
            };

            client.connect();
            startPingRoutine();

        } catch (URISyntaxException e) {
            if (onErrorCallBack != null) {
                mainHandler.post(() -> onErrorCallBack.accept("URL inválida: " + location));
            }
            e.printStackTrace();
        }
    }

    private void handleServerMessage(String message) {
        Log.d("WS_MESSAGE", message);
        try {
            JSONObject msg = new JSONObject(message);
            if (msg.has("key")) {
                String key = msg.getString("key");
                AppData appData = AppData.getInstance();

                switch (key) {
                    case "tables":
                        JSONArray tables = msg.getJSONArray("tables");
                        appData.loadTables(tables);
                        break;

                    case "allProductes":
                        JSONArray products = msg.getJSONArray("productes");
                        appData.collectProducts(products);
                        break;

                    default:
                        Log.w("WS_MESSAGE", "Unknown key received: " + key);
                        break;
                }
            }
        } catch (JSONException e) {
            Log.e("WS_MESSAGE", "JSON error: " + e.getMessage());
        }
    }

    public static UtilsWS getSharedInstance(String location, String name) {
        if (sharedInstance == null) {
            sharedInstance = new UtilsWS(location, name);
        }
        return sharedInstance;
    }

    public static UtilsWS getSharedInstance() {
        return sharedInstance;
    }

    public void onOpen(Consumer<String> callBack) {
        this.onOpenCallBack = callBack;
    }

    public void onError(Consumer<String> callBack) {
        this.onErrorCallBack = callBack;
    }

    public void forceExit() {
        if (client != null && client.isOpen()) {
            client.close();
        }
    }

    public boolean isOpen() {
        return client != null && client.isOpen();
    }

    private void startPingRoutine() {
        pingScheduler = Executors.newSingleThreadScheduledExecutor();
        pingScheduler.scheduleWithFixedDelay(() -> {
            if (client != null && client.isOpen()) {
                client.send("{\"type\":\"ping\"}");
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private void stopPingRoutine() {
        if (pingScheduler != null && !pingScheduler.isShutdown()) {
            pingScheduler.shutdownNow();
        }
    }

    public void send(String msg) {
        client.send(msg);
    }

    public void enviarComanda(String camarero, int taula, List<OrderItem> items) {
        try {
            JSONObject orderJson = new JSONObject();
            orderJson.put("type", "newOrder");
            orderJson.put("waiter", camarero);
            orderJson.put("tableNum", taula);

            JSONArray productsArray = new JSONArray();
            for (OrderItem item : items) {
                JSONObject productJson = new JSONObject();
                productJson.put("name", item.getProduct().getName());
                productJson.put("description", item.getProduct().getDescription());
                productJson.put("price", item.getProduct().getPrice());
                productJson.put("tag", item.getProduct().getTag().getName());
                productJson.put("amount", item.getQuantity());
                productsArray.put(productJson);
            }

            orderJson.put("products", productsArray);

            if (client != null && client.isOpen()) {
                client.send(orderJson.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
