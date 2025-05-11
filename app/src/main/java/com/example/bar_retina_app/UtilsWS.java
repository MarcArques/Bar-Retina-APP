package com.example.bar_retina_app;

import android.os.Handler;
import android.os.Looper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class UtilsWS {

    private static UtilsWS sharedInstance = null;
    private WebSocketClient client;
    private Consumer<String> onOpenCallBack = null;
    private Consumer<String> onErrorCallBack = null;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String location;
    private String name;

    private ScheduledExecutorService pingScheduler;
    private Consumer<String> onMessageCallBack = null;

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
                    if (onMessageCallBack != null) {
                        mainHandler.post(() -> onMessageCallBack.accept(message));
                    }
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

    public void onMessage(Consumer<String> callBack) {
        this.onMessageCallBack = callBack;
    }

    private void startPingRoutine() {
        pingScheduler = Executors.newSingleThreadScheduledExecutor();
        pingScheduler.scheduleWithFixedDelay(() -> {
            if (client != null && client.isOpen()) {
                client.send("{\"type\":\"ping\"}");
            }
        }, 30, 30, TimeUnit.SECONDS); // primer ping a los 30s, luego cada 30s de espera después del anterior
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
