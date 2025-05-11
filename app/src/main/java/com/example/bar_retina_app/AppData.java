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
    public Table table;
    public ArrayList<Table> tables;

    private AppData() {
        this.products = new ArrayList<>();
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

    public void loadTables(JSONArray obj) {
        tables.clear();
        Log.d("TABLES", "Loading tables: "+obj.length());
        for(int i = 0; i < obj.length(); i++) {
            try {
                JSONObject table = obj.getJSONObject(i);
                int number = table.getInt("number");
                int clients = table.getInt("clients");
                Table newTable = new Table(number, clients);
                tables.add(newTable);
                JSONArray products = table.getJSONArray("items");
                for(int j = 0; j < products.length(); j++) {
                    JSONObject product = products.getJSONObject(j);
                    Log.i("TABLES", product.toString());
                    String name = product.getString("product");
                    int amount = product.getInt("amount");
                    Product newProduct = null;
                    for(Product p : this.products) {
                        Log.i("TABLES", p.getName());
                        if(p.getName().equals(name)) {
                            newProduct = p;
                        }
                    }
                    if(newProduct == null) {
                        return;
                    }
                    newTable.order.add(new OrderItem(newProduct, amount));
                    Log.d("TABLES", newTable.toString());
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        Log.d("TABLES", tables.toString());
    }

}
