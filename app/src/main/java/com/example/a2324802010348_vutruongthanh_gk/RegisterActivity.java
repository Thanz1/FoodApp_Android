package com.example.a2324802010348_vutruongthanh_gk;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmailRegister);
        edtPassword = findViewById(R.id.edtPasswordRegister);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Sự kiện bấm nút Đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Vui lòng nhập email!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Vui lòng nhập mật khẩu!");
                    return;
                }
                if (password.length() < 6) {
                    edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự!");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    edtConfirmPassword.setError("Mật khẩu nhập lại không khớp!");
                    return;
                }

                // Tiến hành đăng ký tài khoản lên Firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                // Chuyển về màn hình Đăng nhập
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // Chuyển sang màn hình Đăng nhập nếu đã có tài khoản
        tvGoToLogin.setOnClickListener(v -> {
            finish();
        });
    }
}