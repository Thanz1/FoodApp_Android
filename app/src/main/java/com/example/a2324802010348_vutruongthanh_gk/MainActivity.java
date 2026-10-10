package com.example.a2324802010348_vutruongthanh_gk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button btnLogout, btnProfile, btnCart;
    private RecyclerView recyclerViewFood, rcvCategories;
    private FoodAdapter foodAdapter;
    private CategoryAdapter categoryAdapter;

    private List<FoodItem> foodItemList = new ArrayList<>();
    private List<FoodItem> allFoodItemList = new ArrayList<>();
    private List<String> categoryList = new ArrayList<>();
    private String currentCategory = "Tất cả";

    private FirebaseFirestore db;
    private ListenerRegistration firestoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ View
        btnLogout = findViewById(R.id.btnLogout);
        btnProfile = findViewById(R.id.btnProfile);
        btnCart = findViewById(R.id.btnCart);
        recyclerViewFood = findViewById(R.id.recyclerViewFood);
        rcvCategories = findViewById(R.id.rcvCategories);

        db = FirebaseFirestore.getInstance();

        // Cấu hình RecyclerView danh mục cuộn ngang
        rcvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            currentCategory = category;
            filterFoodsByCategory(category);
        });
        rcvCategories.setAdapter(categoryAdapter);

        // Cấu hình RecyclerView món ăn dạng lưới 2 cột
        recyclerViewFood.setLayoutManager(new GridLayoutManager(this, 2));
        foodAdapter = new FoodAdapter(this, foodItemList, foodItem -> {
            Intent intent = new Intent(MainActivity.this, FoodDetailActivity.class);
            intent.putExtra("foodItem", foodItem);
            startActivity(intent);
        });
        recyclerViewFood.setAdapter(foodAdapter);

        // Điều hướng
        btnProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, UserProfileActivity.class)));
        btnCart.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenToFirestoreFoods();
    }

    private void listenToFirestoreFoods() {
        firestoreListener = db.collection("Foods")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, "Lỗi tải món: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        allFoodItemList.clear();
                        Set<String> uniqueCategories = new LinkedHashSet<>();
                        uniqueCategories.add("Tất cả");

                        for (QueryDocumentSnapshot doc : value) {
                            FoodItem item = doc.toObject(FoodItem.class);
                            allFoodItemList.add(item);
                            if (item.getCategory() != null && !item.getCategory().trim().isEmpty()) {
                                uniqueCategories.add(item.getCategory().trim());
                            }
                        }

                        categoryList.clear();
                        categoryList.addAll(uniqueCategories);
                        categoryAdapter.notifyDataSetChanged();

                        filterFoodsByCategory(currentCategory);
                    }
                });
    }

    private void filterFoodsByCategory(String category) {
        foodItemList.clear();
        if ("Tất cả".equals(category)) {
            foodItemList.addAll(allFoodItemList);
        } else {
            for (FoodItem item : allFoodItemList) {
                if (category.equalsIgnoreCase(item.getCategory())) {
                    foodItemList.add(item);
                }
            }
        }
        foodAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }
}