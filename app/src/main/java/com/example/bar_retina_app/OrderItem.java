package com.example.bar_retina_app;

public class OrderItem {
    private Product product;
    private int quantity;

    public OrderItem(Product product) {
        this.product = product;
        quantity = 1;
    }
    public OrderItem(Product product, int amount) {
        this.product = product;
        this.quantity = amount;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addOne() {
        this.quantity++;
    }

    public void deleteOne() {
        this.quantity--;
    }
}
