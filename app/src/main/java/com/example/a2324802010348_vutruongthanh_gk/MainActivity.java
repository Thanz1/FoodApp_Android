package com.example.a2324802010348_vutruongthanh_gk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnLogout, btnProfile, btnCart;
    private RecyclerView recyclerViewFood;
    private FoodAdapter foodAdapter;
    private List<FoodItem> foodItemList;
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

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Cấu hình RecyclerView hiển thị dạng lưới 2 cột
        recyclerViewFood.setLayoutManager(new GridLayoutManager(this, 2));

        foodItemList = new ArrayList<>();

        // Khởi tạo Adapter kèm sự kiện click xem chi tiết món
        foodAdapter = new FoodAdapter(this, foodItemList, foodItem -> {
            Intent intent = new Intent(MainActivity.this, FoodDetailActivity.class);
            intent.putExtra("foodItem", foodItem);
            startActivity(intent);
        });
        recyclerViewFood.setAdapter(foodAdapter);

        // Điều hướng sang màn hình Hồ sơ cá nhân
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        // Điều hướng sang Giỏ hàng
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Xử lý Đăng xuất
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
        // Lắng nghe dữ liệu thời gian thực từ Firestore collection "Foods"
        firestoreListener = db.collection("Foods")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, "Lỗi tải món: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        foodItemList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            FoodItem item = doc.toObject(FoodItem.class);
                            foodItemList.add(item);
                        }
                        foodAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Hủy lắng nghe khi màn hình đóng để tránh hao pin / rò rỉ bộ nhớ
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }
}