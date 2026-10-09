package com.example.a2324802010348_vutruongthanh_gk;

public class Review {
    private String userEmail;
    private float rating;
    private String comment;

    public Review() {} // Bắt buộc cho Firestore

    public Review(String userEmail, float rating, String comment) {
        this.userEmail = userEmail;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}