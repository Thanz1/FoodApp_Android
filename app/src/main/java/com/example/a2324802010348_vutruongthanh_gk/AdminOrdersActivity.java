package com.example.a2324802010348_vutruongthanh_gk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminOrdersActivity extends AppCompatActivity {

    private RecyclerView rcvAdminOrders;
    private FirebaseFirestore db;
    private List<Order> orderList = new ArrayList<>();
    private AdminOrderAdapter adapter;
    private ListenerRegistration ordersListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        rcvAdminOrders = findViewById(R.id.rcvAdminOrders);
        db = FirebaseFirestore.getInstance();

        rcvAdminOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrderAdapter();
        rcvAdminOrders.setAdapter(adapter);

        listenToOrders();
    }

    private void listenToOrders() {
        ordersListener = db.collection("Orders")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Lỗi tải đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (snapshots != null) {
                        orderList.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Order order = doc.toObject(Order.class);
                            orderList.add(order);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ordersListener != null) ordersListener.remove();
    }

    private class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
            return new OrderViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = orderList.get(position);
            holder.tvUser.setText("Khách: " + order.getUserEmail());
            holder.tvFood.setText(order.getFoodName());
            holder.tvToppings.setText("+ Toppings: " + (order.getToppings().isEmpty() ? "Không" : String.join(", ", order.getToppings())));
            holder.tvTotal.setText("Tổng: " + String.format("%,.0f đ", order.getTotalPrice()));
            holder.tvStatus.setText("Trạng thái: " + order.getStatus());

            // Xử lý nút NEXT chuyển trạng thái tuần tự
            if ("Đã làm xong món".equals(order.getStatus()) || "Đã hoàn tất".equals(order.getStatus())) {
                holder.btnNext.setVisibility(View.GONE);
            } else {
                holder.btnNext.setVisibility(View.VISIBLE);
                holder.btnNext.setOnClickListener(v -> {
                    String nextStatus = "";
                    if ("Đang xác nhận yêu cầu".equals(order.getStatus())) {
                        nextStatus = "Đang chuẩn bị món";
                    } else if ("Đang chuẩn bị món".equals(order.getStatus())) {
                        nextStatus = "Đã làm xong món";
                    }

                    if (!nextStatus.isEmpty()) {
                        db.collection("Orders").document(order.getOrderId())
                                .update("status", nextStatus)
                                .addOnSuccessListener(aVoid -> Toast.makeText(AdminOrdersActivity.this, "Đã cập nhật trạng thái!", Toast.LENGTH_SHORT).show());
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView tvUser, tvFood, tvToppings, tvTotal, tvStatus;
            Button btnNext;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                tvUser = itemView.findViewById(R.id.tvAdminOrderUser);
                tvFood = itemView.findViewById(R.id.tvAdminOrderFood);
                tvToppings = itemView.findViewById(R.id.tvAdminOrderToppings);
                tvTotal = itemView.findViewById(R.id.tvAdminOrderTotal);
                tvStatus = itemView.findViewById(R.id.tvAdminOrderStatus);
                btnNext = itemView.findViewById(R.id.btnNextStatus);
            }
        }
    }
}