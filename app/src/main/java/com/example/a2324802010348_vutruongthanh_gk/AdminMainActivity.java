package com.example.a2324802010348_vutruongthanh_gk;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminMainActivity extends AppCompatActivity {

    private Button btnAdminLogout, btnGoToAddFood, btnManageOrders, btnViewAsCustomer;
    private TextView tvAdminHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        btnAdminLogout = findViewById(R.id.btnAdminLogout);
        btnGoToAddFood = findViewById(R.id.btnGoToAddFood);
        btnManageOrders = findViewById(R.id.btnManageOrders);
        btnViewAsCustomer = findViewById(R.id.btnViewAsCustomer);
        tvAdminHello = findViewById(R.id.tvAdminHello);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            tvAdminHello.setText("Tài khoản quản trị: " + user.getEmail());
        }

        // Chuyển sang màn hình Thêm món ăn
        btnGoToAddFood.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMainActivity.this, AdminAddFoodActivity.class);
            startActivity(intent);
        });

        // Tiếp nhận đơn hàng
        btnManageOrders.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMainActivity.this, AdminOrdersActivity.class);
            startActivity(intent);
        });

        // Xem với tư cách khách hàng
        btnViewAsCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMainActivity.this, MainActivity.class);
            startActivity(intent);
        });
        btnGoToAddFood.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMainActivity.this, AdminManageFoodActivity.class);
            startActivity(intent);
        });
        // Đăng xuất
        btnAdminLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}