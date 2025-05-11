package com.example.bar_retina_app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppData {
    private static AppData instance;
    public List<Product> products;
    public List<OrderItem> order;
    public Table table;
    public ArrayList<Table> tables;

    private AppData() {
        this.products = new ArrayList<>();
        this.order = new ArrayList<>();
        this.tables = new ArrayList<>();
    }

    public static AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    public void collectProducts(JSONArray productsArray) {
        products.clear();

        try {
            for (int i = 0; i < productsArray.length(); i++) {
                JSONObject productJson = productsArray.getJSONObject(i);

                String name = productJson.getString("nom");
                String description = productJson.getString("descripcio");
                float price = Float.parseFloat(productJson.getString("preu"));
                String categoria = productJson.getString("categoria");

                Tag tag = new Tag(categoria);
                Product product = new Product(name, description, price, tag);

                products.add(product);
            }

            Log.d("COLLECT PRODUCTS", "products collected successfully. Count: " + products.size());
        } catch (JSONException | NumberFormatException e) {
            Log.e("COLLECT PRODUCTS", "Error while collecting products: " + e.getMessage());
        }
    }

    public OrderItem getOrderItemByName(String productName) {
        for (OrderItem item : order) {
            if (item.getProduct().getName().equals(productName)) {
                return item;
            }
        }
        return null;
    }

    public int getProductQuantity (String productName) {
        for(OrderItem item : order) {
            if(item.getProduct().getName().equals(productName)) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public float getTotalBill () {
        float total = 0f;
        for (OrderItem item : order) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    public boolean containsProduct(String productName) {
        for (OrderItem item : order) {
            if (item.getProduct().getName().equals(productName)) {
                return true;
            }
        }
        return false;
    }

    public void loadTables(JSONArray obj) {
        tables.clear();
        for(int i = 0; i < obj.length(); i++) {
            try {
                JSONObject table = obj.getJSONObject(i);
                int number = table.getInt("number");
                int clients = table.getInt("clients");
                Table newTable = new Table(number, clients);
                tables.add(newTable);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        Log.d("TABLES", tables.toString());
    }

}
