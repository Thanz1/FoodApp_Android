package com.example.a2324802010348_vutruongthanh_gk;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCart;
    private TextView tvTotalPrice;
    private Button btnCheckout, btnBackHome;
    private FoodAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBackHome = findViewById(R.id.btnBackHome);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        // Lấy danh sách từ CartManager và hiển thị qua FoodAdapter
        cartAdapter = new FoodAdapter(this, CartManager.getInstance().getCartList(), foodItem -> {
            // Có thể xử lý bấm vào món trong giỏ để xóa nếu muốn
        });
        recyclerViewCart.setAdapter(cartAdapter);

        // Hiển thị tổng tiền
        updateTotalPrice();

        // Nút quay về trang chủ
        btnBackHome.setOnClickListener(v -> finish());

        // Xử lý nút thanh toán
        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getCartList().isEmpty()) {
                Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thanh toán thành công! Cảm ơn bạn.", Toast.LENGTH_LONG).show();
                CartManager.getInstance().clearCart(); // Xóa sạch giỏ hàng sau khi thanh toán
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
            }
        });
    }

    private void updateTotalPrice() {
        double total = CartManager.getInstance().getTotalPrice();
        tvTotalPrice.setText(String.format("Tổng tiền: %,.0f đ", total));
    }
}