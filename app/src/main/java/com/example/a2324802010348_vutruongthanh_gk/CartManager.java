package com.example.a2324802010348_vutruongthanh_gk;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<FoodItem> cartList;

    private CartManager() {
        cartList = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addFood(FoodItem foodItem) {
        cartList.add(foodItem);
    }

    public List<FoodItem> getCartList() {
        return cartList;
    }

    public void clearCart() {
        cartList.clear();
    }

    public double getTotalPrice() {
        double total = 0;
        for (FoodItem item : cartList) {
            total += item.getPrice();
        }
        return total;
    }
}