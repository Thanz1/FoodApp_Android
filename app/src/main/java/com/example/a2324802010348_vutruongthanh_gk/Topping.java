package com.example.a2324802010348_vutruongthanh_gk;

import java.io.Serializable;

public class Topping implements Serializable {
    private String name;
    private double price;

    public Topping() {} // Bắt buộc cho Firestore

    public Topping(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}