package com.example.a2324802010348_vutruongthanh_gk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.util.UUID;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView imgDetailFood;
    private TextView tvDetailName, tvDetailPrice, tvDetailDescription;
    private Button btnAddToCart, btnSubmitReview;
    private RatingBar ratingBarInput;
    private EditText edtCommentInput;
    private RecyclerView rcvReviews;

    // Xử lý Topping & Tổng tiền
    private LinearLayout layoutCustomerToppings;
    private TextView tvTotalPrice;
    private double basePrice = 0;
    private double currentTotal = 0;
    private List<Topping> selectedToppings = new ArrayList<>();

    private FoodItem currentFood;
    private FirebaseFirestore db;
    private List<Review> reviewList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        imgDetailFood = findViewById(R.id.imgDetailFood);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        layoutCustomerToppings = findViewById(R.id.layoutCustomerToppings);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        ratingBarInput = findViewById(R.id.ratingBarInput);
        edtCommentInput = findViewById(R.id.edtCommentInput);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        rcvReviews = findViewById(R.id.rcvReviews);

        db = FirebaseFirestore.getInstance();

        // Nhận dữ liệu món ăn truyền sang
        currentFood = (FoodItem) getIntent().getSerializableExtra("foodItem");

        if (currentFood != null) {
            tvDetailName.setText(currentFood.getName());
            tvDetailPrice.setText(String.format("%,.0f đ", currentFood.getPrice()));
            tvDetailDescription.setText(currentFood.getDescription());

            basePrice = currentFood.getPrice();
            currentTotal = basePrice;
            updateTotalDisplay();

            // 1. Tải ảnh món ăn qua Glide
            if (currentFood.getImageUrl() != null && !currentFood.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(currentFood.getImageUrl())
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(imgDetailFood);
            } else {
                imgDetailFood.setImageResource(R.mipmap.ic_launcher);
            }

            // 2. Tạo CheckBox chọn Topping ăn kèm và tính tổng tiền
            if (currentFood.getToppings() != null && !currentFood.getToppings().isEmpty()) {
                for (Topping topping : currentFood.getToppings()) {
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(topping.getName() + " (+" + String.format("%,.0f đ", topping.getPrice()) + ")");

                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            selectedToppings.add(topping);
                            currentTotal += topping.getPrice();
                        } else {
                            selectedToppings.remove(topping);
                            currentTotal -= topping.getPrice();
                        }
                        updateTotalDisplay();
                    });

                    layoutCustomerToppings.addView(checkBox);
                }
            }

            // 3. Cấu hình danh sách Đánh giá
            rcvReviews.setLayoutManager(new LinearLayoutManager(this));
            reviewAdapter = new ReviewAdapter();
            rcvReviews.setAdapter(reviewAdapter);

            loadReviews();

            // 4. Gửi đánh giá lên Firestore
            btnSubmitReview.setOnClickListener(v -> {
                String comment = edtCommentInput.getText().toString().trim();
                float rating = ratingBarInput.getRating();

                if (comment.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập bình luận!", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = (user != null && user.getEmail() != null) ? user.getEmail() : "Ẩn danh";

                Review newReview = new Review(email, rating, comment);

                db.collection("Foods").document(currentFood.getId())
                        .collection("Reviews")
                        .add(newReview)
                        .addOnSuccessListener(doc -> {
                            Toast.makeText(this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                            edtCommentInput.setText("");
                            loadReviews();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        }

        btnAddToCart.setOnClickListener(v -> {
            if (currentFood == null) return;

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String email = (user != null && user.getEmail() != null) ? user.getEmail() : "customer@gmail.com";
            String orderId = UUID.randomUUID().toString();

            List<String> toppingNames = new ArrayList<>();
            for (Topping t : selectedToppings) {
                toppingNames.add(t.getName() + " (+" + String.format("%,.0f đ", t.getPrice()) + ")");
            }

            Order newOrder = new Order(
                    orderId,
                    email,
                    currentFood.getName(),
                    toppingNames,
                    currentTotal,
                    "Đang xác nhận yêu cầu",
                    System.currentTimeMillis()
            );

            db.collection("Orders").document(orderId)
                    .set(newOrder)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Đặt món thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FoodDetailActivity.this, OrderStatusActivity.class);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi đặt món: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void updateTotalDisplay() {
        tvTotalPrice.setText("Tổng cộng: " + String.format("%,.0f đ", currentTotal));
    }

    private void loadReviews() {
        if (currentFood == null) return;
        db.collection("Foods").document(currentFood.getId())
                .collection("Reviews")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reviewList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Review r = doc.toObject(Review.class);
                        reviewList.add(r);
                    }
                    reviewAdapter.notifyDataSetChanged();
                });
    }

    // Adapter RecyclerView cho đánh giá
    private class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Review r = reviewList.get(position);
            holder.tvUser.setText(r.getUserEmail());
            holder.tvComment.setText(r.getComment());
            holder.rbRating.setRating(r.getRating());
        }

        @Override
        public int getItemCount() {
            return reviewList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvUser, tvComment;
            RatingBar rbRating;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvUser = itemView.findViewById(R.id.tvReviewUser);
                tvComment = itemView.findViewById(R.id.tvReviewComment);
                rbRating = itemView.findViewById(R.id.rbItemRating);
            }
        }
    }
}