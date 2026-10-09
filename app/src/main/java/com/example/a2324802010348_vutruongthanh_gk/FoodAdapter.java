package com.example.a2324802010348_vutruongthanh_gk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<FoodItem> foodList;
    private OnItemClickListener listener;

    // Interface để bắt sự kiện click vào món ăn (chuyển sang màn hình chi tiết)
    public interface OnItemClickListener {
        void onItemClick(FoodItem foodItem);
    }

    public FoodAdapter(Context context, List<FoodItem> foodList, OnItemClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem food = foodList.get(position);
        if (food == null) return;

        holder.tvName.setText(food.getName());
        holder.tvPrice.setText(String.format("%,.0f đ", food.getPrice()));

        // Tải ảnh từ URL qua thư viện Glide hoặc hiển thị icon mặc định
        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(food.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.imgFood);
        } else {
            holder.imgFood.setImageResource(R.mipmap.ic_launcher);
        }

        // Bắt sự kiện khi click vào một món ăn
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList != null ? foodList.size() : 0;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvName, tvPrice;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
        }
    }
}