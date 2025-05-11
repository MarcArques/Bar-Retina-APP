package com.example.bar_retina_app;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private int number;
    private int clients;
    public List<OrderItem> order;

    public Table(int number, int clients) {
        this.number = number;
        this.clients = clients;
        this.order = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public int getClients() {
        return clients;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setClients(int clients) {
        this.clients = clients;
    }

    public float getTotalBill() {
        float total = 0f;
        for(OrderItem orderItem : order) {
            total += orderItem.getProduct().getPrice() * orderItem.getQuantity();
        }
        return total;
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

    public boolean containsProduct(String productName) {
        for (OrderItem item : order) {
            if (item.getProduct().getName().equals(productName)) {
                return true;
            }
        }
        return false;
    }
}