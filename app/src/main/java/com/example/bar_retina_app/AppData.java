package com.example.bar_retina_app;

import org.json.JSONObject;
import java.util.ArrayList;

public class AppData {
    private static AppData instance;
    public ArrayList<JSONObject> products;

    private AppData() {
        // Constructor privado para evitar creación de instancias
    }

    public static AppData getInstance() {
        if(instance == null) {
            instance = new AppData();
        }
        return instance;
    }


}
