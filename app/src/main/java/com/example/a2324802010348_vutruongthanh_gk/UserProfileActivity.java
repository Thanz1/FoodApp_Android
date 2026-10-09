package com.example.a2324802010348_vutruongthanh_gk;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgAvatar;
    private Button btnSelectImage, btnProfileBackHome;
    private TextView tvUserEmail;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        imgAvatar = findViewById(R.id.imgAvatar);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnProfileBackHome = findViewById(R.id.btnProfileBackHome);
        tvUserEmail = findViewById(R.id.tvUserEmail);

        // Hiển thị email người dùng đang đăng nhập qua Firebase Auth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            tvUserEmail.setText("Email: " + currentUser.getEmail());
        }

        // Mở thư viện ảnh để chọn avatar hoặc ảnh món ăn
        btnSelectImage.setOnClickListener(v -> openFileChooser());

        // Nút quay về trang chủ
        btnProfileBackHome.setOnClickListener(v -> finish());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri); // Hiển thị ảnh vừa chọn lên ImageView
            Toast.makeText(this, "Đã chọn ảnh thành công! (Sẵn sàng upload)", Toast.LENGTH_SHORT).show();
        }
    }
}