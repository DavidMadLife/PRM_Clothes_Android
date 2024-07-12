package com.example.prm_shop.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shop.R;
import com.example.prm_shop.models.request.UpdateUserRequest;
import com.example.prm_shop.models.response.UpdateUserResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.MemberService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends FooterActivity {

    private EditText editUserName;
    private EditText editEmail;
    private EditText editPhone;
    private EditText editAddress;
    private Button buttonUpdate;
    private Button buttonCancel;

    private MemberService memberService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        editUserName = findViewById(R.id.editUserName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonCancel = findViewById(R.id.cancelButton);

        memberService = ApiClient.getRetrofitInstance().create(MemberService.class);

        // Get user information from Intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        editUserName.setText(getIntent().getStringExtra("USER_NAME"));
        editEmail.setText(getIntent().getStringExtra("EMAIL"));
        editPhone.setText(getIntent().getStringExtra("PHONE"));
        editAddress.setText(getIntent().getStringExtra("ADDRESS"));

        // Handle Update button click event
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserRequest request = new UpdateUserRequest(
                        editUserName.getText().toString(),
                        editEmail.getText().toString(),
                        editPhone.getText().toString(),
                        editAddress.getText().toString()
                );
                updateUser(userId, request);
            }
        });

        // Handle Cancel button click event
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Close the EditUserActivity
            }
        });
    }

    private void updateUser(int userId, UpdateUserRequest request) {
        memberService.updateUser(userId, request).enqueue(new Callback<UpdateUserResponse>() {
            @Override
            public void onResponse(Call<UpdateUserResponse> call, Response<UpdateUserResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditUserActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    /*finish();  */// Close the EditUserActivity and return success result
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("UpdateUser", "Failed to update user: " + errorBody);
                        Toast.makeText(EditUserActivity.this, "Failed to update user: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e("UpdateUser", "IOException while parsing error body");
                        Toast.makeText(EditUserActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateUserResponse> call, Throwable t) {
                Log.e("UpdateUser", "Error: " + t.getMessage(), t);
                Toast.makeText(EditUserActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
