package com.example.bar_retina_app;

import android.os.Handler;
import android.os.Looper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

public class UtilsWS {

    private static UtilsWS sharedInstance = null;
    private WebSocketClient client;
    private Consumer<String> onOpenCallBack = null;
    private Consumer<String> onErrorCallBack = null;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String location;

    private UtilsWS(String location) {
        this.location = location;
        createWebSocketClient();
    }

    private void createWebSocketClient() {
        try {
            client = new WebSocketClient(new URI(location), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    if (onOpenCallBack != null) {
                        mainHandler.post(() -> onOpenCallBack.accept("✅ Conectado correctamente"));
                    }
                }

                @Override
                public void onMessage(String message) {
                    // No se usa en APP/INICI
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    if (onErrorCallBack != null) {
                        mainHandler.post(() -> onErrorCallBack.accept("Conexión cerrada: " + reason));
                    }
                }

                @Override
                public void onError(Exception ex) {
                    if (onErrorCallBack != null) {
                        String msg = (ex.getMessage() != null) ? ex.getMessage() : "Error desconocido";
                        mainHandler.post(() -> onErrorCallBack.accept("Error de conexión: " + msg));
                    }
                }
            };
            client.connect();
        } catch (URISyntaxException e) {
            if (onErrorCallBack != null) {
                mainHandler.post(() -> onErrorCallBack.accept("❌ URL inválida: " + location));
            }
            e.printStackTrace();
        }
    }

    public static UtilsWS getSharedInstance(String location) {
        if (sharedInstance == null) {
            sharedInstance = new UtilsWS(location);
        }
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
}
