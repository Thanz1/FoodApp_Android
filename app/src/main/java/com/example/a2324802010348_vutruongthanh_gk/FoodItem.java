package com.example.a2324802010348_vutruongthanh_gk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FoodItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;
    private List<Topping> toppings = new ArrayList<>();

    // Constructor rỗng bắt buộc cho Firebase Firestore
    public FoodItem() {
    }

    public FoodItem(String id, String name, String description, double price, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.toppings = new ArrayList<>();
    }

    public FoodItem(String id, String name, String description, double price, String imageUrl, String category, List<Topping> toppings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.toppings = (toppings != null) ? toppings : new ArrayList<>();
    }

    // Getters và Setters cho toppings
    public List<Topping> getToppings() {
        if (toppings == null) {
            toppings = new ArrayList<>();
        }
        return toppings;
    }

    public void setToppings(List<Topping> toppings) {
        this.toppings = toppings;
    }

    // Các hàm Getters và Setters khác
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}