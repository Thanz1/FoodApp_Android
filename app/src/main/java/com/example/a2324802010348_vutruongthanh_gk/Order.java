package com.example.a2324802010348_vutruongthanh_gk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String userEmail;
    private String foodName;
    private List<String> toppings = new ArrayList<>();
    private double totalPrice;
    private String status; // "Đang xác nhận yêu cầu", "Đang chuẩn bị món", "Đã làm xong món", "Đã hoàn tất"
    private long timestamp;

    public Order() {} // Bắt buộc cho Firestore

    public Order(String orderId, String userEmail, String foodName, List<String> toppings, double totalPrice, String status, long timestamp) {
        this.orderId = orderId;
        this.userEmail = userEmail;
        this.foodName = foodName;
        this.toppings = toppings;
        this.totalPrice = totalPrice;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public List<String> getToppings() { return toppings; }
    public void setToppings(List<String> toppings) { this.toppings = toppings; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}