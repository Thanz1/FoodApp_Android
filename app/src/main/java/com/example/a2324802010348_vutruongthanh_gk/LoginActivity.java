package com.example.a2324802010348_vutruongthanh_gk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private CheckBox cbRememberMe;
    private TextView tvGoToRegister, tvForgotPassword;
    private FirebaseAuth mAuth;

    // Khởi tạo SharedPreferences để lưu trữ cục bộ
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        edtEmail = findViewById(R.id.edtEmailLogin);
        edtPassword = findViewById(R.id.edtPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Kiểm tra xem trước đó đã lưu thông tin chưa
        boolean isRemembered = sharedPreferences.getBoolean(KEY_REMEMBER, false);
        if (isRemembered) {
            edtEmail.setText(sharedPreferences.getString(KEY_EMAIL, ""));
            edtPassword.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
            cbRememberMe.setChecked(true);
        }

        // 1. Sự kiện bấm nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Vui lòng nhập email!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Vui lòng nhập mật khẩu!");
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Xử lý ghi nhớ tài khoản nếu Checkbox được tích chọn
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                if (cbRememberMe.isChecked()) {
                                    editor.putString(KEY_EMAIL, email);
                                    editor.putString(KEY_PASSWORD, password);
                                    editor.putBoolean(KEY_REMEMBER, true);
                                } else {
                                    editor.clear(); // Xóa dữ liệu đã lưu nếu bỏ tích
                                }
                                editor.apply();

                                // Lấy thông tin user vừa đăng nhập
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                String userEmail = (currentUser != null && currentUser.getEmail() != null)
                                        ? currentUser.getEmail().toLowerCase()
                                        : "";

                                // Phân quyền: Kiểm tra nếu là email Admin
                                if (userEmail.contains("admin")) {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập quyền ADMIN thành công!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Kiểm tra lại email/mật khẩu!", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // 2. Chuyển sang màn hình Đăng ký
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 3. Quên mật khẩu
        tvForgotPassword.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                edtEmail.setError("Vui lòng nhập email vào ô trên trước!");
                return;
            }
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Đã gửi email đặt lại mật khẩu!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}