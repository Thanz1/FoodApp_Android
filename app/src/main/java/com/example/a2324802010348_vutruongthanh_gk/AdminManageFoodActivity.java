package com.example.a2324802010348_vutruongthanh_gk;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageFoodActivity extends AppCompatActivity {

    private RecyclerView rcvAdminFoods;
    private Button btnAdminCreateFood;
    private FirebaseFirestore db;
    private List<FoodItem> foodList = new ArrayList<>();
    private AdminFoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_food);

        rcvAdminFoods = findViewById(R.id.rcvAdminFoods);
        btnAdminCreateFood = findViewById(R.id.btnAdminCreateFood);
        db = FirebaseFirestore.getInstance();

        rcvAdminFoods.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminFoodAdapter();
        rcvAdminFoods.setAdapter(adapter);

        btnAdminCreateFood.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminAddFoodActivity.class));
        });

        loadFoods();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFoods();
    }

    private void loadFoods() {
        db.collection("Foods").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                foodList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    FoodItem item = doc.toObject(FoodItem.class);
                    foodList.add(item);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Adapter nội bộ phục vụ việc hiển thị danh sách cho Admin
    private class AdminFoodAdapter extends RecyclerView.Adapter<AdminFoodAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_food, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FoodItem food = foodList.get(position);
            holder.tvName.setText(food.getName());
            holder.tvPrice.setText(String.format("%,.0f đ", food.getPrice()));

            if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
                Glide.with(AdminManageFoodActivity.this).load(food.getImageUrl()).into(holder.img);
            }

            // Nút Xóa
            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(AdminManageFoodActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc muốn xóa món " + food.getName() + "?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            db.collection("Foods").document(food.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(AdminManageFoodActivity.this, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                                        loadFoods();
                                    });
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });

            // Nút Sửa
            holder.btnEdit.setOnClickListener(v -> showEditDialog(food));
        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img;
            TextView tvName, tvPrice;
            Button btnEdit, btnDelete;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.imgAdminFood);
                tvName = itemView.findViewById(R.id.tvAdminFoodName);
                tvPrice = itemView.findViewById(R.id.tvAdminFoodPrice);
                btnEdit = itemView.findViewById(R.id.btnEditFood);
                btnDelete = itemView.findViewById(R.id.btnDeleteFood);
            }
        }
    }

    // Hộp thoại chỉnh sửa thông tin món ăn
    private void showEditDialog(FoodItem food) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Món Ăn");

        View view = getLayoutInflater().inflate(R.layout.activity_admin_add_food, null);
        TextView tvTitle = view.findViewById(R.id.tvAdminFormTitle);
        ImageView imgPreview = view.findViewById(R.id.imgPreviewFood);
        EditText edtName = view.findViewById(R.id.edtFoodName);
        EditText edtPrice = view.findViewById(R.id.edtFoodPrice);
        EditText edtCategory = view.findViewById(R.id.edtFoodCategory);
        EditText edtImageUrl = view.findViewById(R.id.edtFoodImageUrl);
        EditText edtDesc = view.findViewById(R.id.edtFoodDesc);
        Button btnSave = view.findViewById(R.id.btnSaveFood);
        Button btnBack = view.findViewById(R.id.btnBackFromAdmin);

        tvTitle.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
        btnSave.setText("Cập nhật món");

        // Điền dữ liệu cũ vào các ô nhập
        edtName.setText(food.getName());
        edtPrice.setText(String.valueOf((long) food.getPrice()));
        edtCategory.setText(food.getCategory());
        edtImageUrl.setText(food.getImageUrl());
        edtDesc.setText(food.getDescription());

        // Nạp ảnh cũ lên preview nếu có
        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            Glide.with(this).load(food.getImageUrl()).into(imgPreview);
        }

        // Xem trước ảnh mới khi sửa link
        edtImageUrl.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    Glide.with(AdminManageFoodActivity.this).load(url).into(imgPreview);
                }
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Tên và giá không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            food.setName(name);
            food.setPrice(Double.parseDouble(priceStr));
            food.setCategory(edtCategory.getText().toString().trim());
            food.setImageUrl(edtImageUrl.getText().toString().trim());
            food.setDescription(edtDesc.getText().toString().trim());

            db.collection("Foods").document(food.getId()).set(food)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadFoods();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        dialog.show();
    }}