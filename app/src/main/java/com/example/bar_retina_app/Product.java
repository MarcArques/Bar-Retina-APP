package com.example.bar_retina_app;

public class Product {
    private String name;
    private String description;
    private float price;
    private Tag tag;

    public Product(String name, String description, float price, Tag tag) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.tag = tag;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public Tag getTag() {
        return tag;
    }
}
