package com.example.prm_shop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.example.prm_shop.activities.BlankActivity;
import com.example.prm_shop.activities.CartActivity;
import com.example.prm_shop.activities.CreateProductActivity;
import com.example.prm_shop.activities.ProductActivity;
import com.example.prm_shop.activities.ProductManageActivity;
import com.example.prm_shop.activities.ProductOptionActivity;
import com.example.prm_shop.activities.RegisterActivity;
import com.example.prm_shop.activities.UserInformationActivity;
import com.example.prm_shop.models.request.LoginView;
import com.example.prm_shop.models.response.TokenResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.MemberService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.example.prm_shop.activities.RegisterActivity;
import com.example.prm_shop.activities.UserActivity;
import com.example.prm_shop.activities.UserInformationActivity;
import com.example.prm_shop.models.request.LoginView;
import com.example.prm_shop.models.response.TokenResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.MemberService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private MemberService memberService;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        registerLink = findViewById(R.id.textViewRegisterLink);

        memberService = ApiClient.getRetrofitInstance().create(MemberService.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginView loginView = new LoginView();
        loginView.setEmail(email);
        loginView.setPassword(password);

        Call<TokenResponse> call = memberService.login(loginView);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    TokenResponse tokenResponse = response.body();
                    if (tokenResponse != null) {
                        String token = tokenResponse.getToken();
                        JWT jwt = new JWT(token);
                        String userId = jwt.getClaim("memberId").asString(); // Ensure "memberId" is used correctly
                        String roleId = jwt.getClaim("role").asString();
                        String username = jwt.getClaim("name").asString();

                        if (userId != null && !userId.isEmpty()) {
                            int userID = Integer.parseInt(userId);

                            // Lưu token vào SharedPreferences
                            saveToken(token);
                            saveUserId(userId);
                            saveRoleId(roleId);
                            saveUsername(username);

                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String fcmToken = task.getResult();
                                    saveTokenToFirestore(userId, fcmToken, roleId, username);
                                } else {
                                    Log.e("MainActivity", "Fetching FCM token failed", task.getException());
                                }
                            });

                            // Chuyển sang UserActivity
                            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "User ID is missing", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to get token", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("MainActivity", "Login failed: " + response.message());
                    Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToken(String token) {
        // Lưu token vào SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TOKEN", token);
        editor.apply();
    }

    private void saveUserId(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.apply();
    }

    private void saveRoleId(String roleId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("roleId", roleId);
        editor.apply();
    }

    private void saveUsername(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }


    private void saveTokenToFirestore(String userId, String fcmToken, String roleId, String name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("fcmToken", fcmToken);
        data.put("roleId", roleId);
        data.put("name", name);

        db.collection("users")
                .document(userId) // Sử dụng userId làm ID của tài liệu
                .set(data, SetOptions.merge()) // Sử dụng SetOptions.merge() để cập nhật dữ liệu nếu tài liệu đã tồn tại
                .addOnSuccessListener(documentReference -> Log.d("MainActivity", "User data saved successfully"))
                .addOnFailureListener(e -> Log.e("MainActivity", "Error saving user data", e));
    }


}
