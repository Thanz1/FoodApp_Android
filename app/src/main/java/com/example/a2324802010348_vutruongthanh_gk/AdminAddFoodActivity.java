package com.example.a2324802010348_vutruongthanh_gk;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminAddFoodActivity extends AppCompatActivity {

    private EditText edtFoodName, edtFoodPrice, edtFoodCategory, edtFoodImageUrl, edtFoodDesc;
    private ImageView imgPreviewFood;
    private Button btnSaveFood, btnBackFromAdmin;
    private FirebaseFirestore db;

    // Các thành phần xử lý Topping
    private List<Topping> toppingList = new ArrayList<>();
    private LinearLayout layoutToppingContainer;
    private EditText edtToppingName, edtToppingPrice;
    private Button btnAddSingleTopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_food);

        // Ánh xạ View
        edtFoodName = findViewById(R.id.edtFoodName);
        edtFoodPrice = findViewById(R.id.edtFoodPrice);
        edtFoodCategory = findViewById(R.id.edtFoodCategory);
        edtFoodImageUrl = findViewById(R.id.edtFoodImageUrl);
        edtFoodDesc = findViewById(R.id.edtFoodDesc);
        imgPreviewFood = findViewById(R.id.imgPreviewFood);
        btnSaveFood = findViewById(R.id.btnSaveFood);
        btnBackFromAdmin = findViewById(R.id.btnBackFromAdmin);

        layoutToppingContainer = findViewById(R.id.layoutToppingContainer);
        edtToppingName = findViewById(R.id.edtToppingName);
        edtToppingPrice = findViewById(R.id.edtToppingPrice);
        btnAddSingleTopping = findViewById(R.id.btnAddSingleTopping);

        db = FirebaseFirestore.getInstance();

        btnBackFromAdmin.setOnClickListener(v -> finish());

        // Thêm topping vào danh sách tạm
        btnAddSingleTopping.setOnClickListener(v -> {
            String tName = edtToppingName.getText().toString().trim();
            String tPriceStr = edtToppingPrice.getText().toString().trim();

            if (!tName.isEmpty() && !tPriceStr.isEmpty()) {
                double tPrice = Double.parseDouble(tPriceStr);
                Topping topping = new Topping(tName, tPrice);
                toppingList.add(topping);
                addToppingRowView(topping);

                edtToppingName.setText("");
                edtToppingPrice.setText("");
            } else {
                Toast.makeText(this, "Nhập đủ tên và giá topping!", Toast.LENGTH_SHORT).show();
            }
        });

        // Tự động tải ảnh xem trước khi người dùng dán URL
        edtFoodImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    Glide.with(AdminAddFoodActivity.this)
                            .load(url)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(imgPreviewFood);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý lưu món ăn vào Firestore
        btnSaveFood.setOnClickListener(v -> {
            String name = edtFoodName.getText().toString().trim();
            String priceStr = edtFoodPrice.getText().toString().trim();
            String category = edtFoodCategory.getText().toString().trim();
            String imageUrl = edtFoodImageUrl.getText().toString().trim();
            String desc = edtFoodDesc.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên và giá món!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            String id = UUID.randomUUID().toString();

            FoodItem newFood = new FoodItem(id, name, desc, price, imageUrl, category);
            newFood.setToppings(toppingList); // Gán topping vào món ăn

            db.collection("Foods").document(id)
                    .set(newFood)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Thêm món thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi thêm món: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    // Hiển thị từng dòng Topping kèm nút Xóa
    private void addToppingRowView(Topping topping) {
        View row = getLayoutInflater().inflate(R.layout.item_admin_topping, null);
        TextView tvInfo = row.findViewById(R.id.tvToppingInfo);
        Button btnRemove = row.findViewById(R.id.btnRemoveTopping);

        tvInfo.setText(topping.getName() + " (+" + String.format("%,.0f đ", topping.getPrice()) + ")");
        btnRemove.setOnClickListener(v -> {
            toppingList.remove(topping);
            layoutToppingContainer.removeView(row);
        });
        layoutToppingContainer.addView(row);
    }
}