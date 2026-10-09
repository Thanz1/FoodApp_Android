package com.example.a2324802010348_vutruongthanh_gk;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class OrderStatusActivity extends AppCompatActivity {

    private TextView tvOrderFoodInfo, tvOrderToppings, tvOrderTotal, tvCurrentStatus;
    private Button btnBackHome;
    private FirebaseFirestore db;
    private ListenerRegistration orderListener;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        tvOrderFoodInfo = findViewById(R.id.tvOrderFoodInfo);
        tvOrderToppings = findViewById(R.id.tvOrderToppings);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);
        btnBackHome = findViewById(R.id.btnBackHome);

        db = FirebaseFirestore.getInstance();
        orderId = getIntent().getStringExtra("orderId");

        btnBackHome.setOnClickListener(v -> finish());

        if (orderId != null) {
            orderListener = db.collection("Orders").document(orderId)
                    .addSnapshotListener((snapshot, error) -> {
                        if (error != null) {
                            Toast.makeText(this, "Lỗi lắng nghe: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            Order order = snapshot.toObject(Order.class);
                            if (order != null) {
                                tvOrderFoodInfo.setText("Món: " + order.getFoodName());
                                tvOrderToppings.setText("Topping: " + (order.getToppings().isEmpty() ? "Không có" : String.join(", ", order.getToppings())));
                                tvOrderTotal.setText("Tổng tiền: " + String.format("%,.0f đ", order.getTotalPrice()));
                                tvCurrentStatus.setText(order.getStatus());

                                // Đổi màu sắc theo tiến độ
                                if ("Đang chuẩn bị món".equals(order.getStatus())) {
                                    tvCurrentStatus.setTextColor(0xFF2196F3); // Xanh dương
                                } else if ("Đã làm xong món".equals(order.getStatus())) {
                                    tvCurrentStatus.setTextColor(0xFF4CAF50); // Xanh lá
                                } else {
                                    tvCurrentStatus.setTextColor(0xFFFF9800); // Cam
                                }
                            }
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderListener != null) orderListener.remove();
    }
}