package com.example.bar_retina_app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppData {

    private static AppData instance;

    public interface OnDataChangedListener {
        void onTablesChanged();
        void onProductsChanged();
        void onCurrentTableChanged(); // <- NUEVO
    }

    private OnDataChangedListener listener;

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

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.listener = listener;
    }

    public void clearListener() {
        this.listener = null;
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

            if (listener != null) {
                listener.onProductsChanged();
            }

        } catch (JSONException | NumberFormatException e) {
            Log.e("COLLECT PRODUCTS", "Error while collecting products: " + e.getMessage());
        }
    }

    public void loadTables(JSONArray obj) {
        tables.clear();
        Log.d("TABLES", "Loading tables: " + obj.length());

        for (int i = 0; i < obj.length(); i++) {
            try {
                JSONObject tableJson = obj.getJSONObject(i);
                int number = tableJson.getInt("number");
                int clients = tableJson.getInt("clients");
                Table newTable = new Table(number, clients);
                tables.add(newTable);

                JSONArray itemsArray = tableJson.getJSONArray("items");

                for (int j = 0; j < itemsArray.length(); j++) {
                    JSONObject itemJson = itemsArray.getJSONObject(j);
                    Log.i("TABLES", itemJson.toString());

                    String productName = itemJson.getString("product");

                    Product matchedProduct = null;
                    for (Product p : this.products) {
                        if (p.getName().equals(productName)) {
                            matchedProduct = p;
                            break;
                        }
                    }

                    if (matchedProduct == null) {
                        Log.w("TABLES", "Product not found: " + productName);
                        continue;
                    }

                    // Si el producte ja hi és, incrementa la quantitat
                    OrderItem existingItem = newTable.getOrderItemByName(productName);
                    if (existingItem != null) {
                        existingItem.addOne();
                    } else {
                        newTable.order.add(new OrderItem(matchedProduct, 1));
                    }

                    Log.d("TABLES", newTable.toString());
                }

                // Si es la taula seleccionada actualment, actualitza-la també
                if (this.table != null && this.table.getNumber() == number) {
                    this.table = newTable;
                    if (listener != null) listener.onCurrentTableChanged();
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        Log.d("TABLES", "Total tables loaded: " + tables.size());

        if (listener != null) {
            listener.onTablesChanged();
        }
    }

}
