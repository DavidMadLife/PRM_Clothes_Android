package com.example.prm_shop.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shop.R;
import com.example.prm_shop.models.request.UpdateUserRequest;
import com.example.prm_shop.models.response.UserResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.MemberService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends FooterActivity {

    private EditText editTextSearch;
    private ImageView imageSearch;
    private ListView userListView;
    private UserAdapter userAdapter;
    private MemberService memberService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        editTextSearch = findViewById(R.id.editTextSearch);
        imageSearch = findViewById(R.id.imageSearch);
        userListView = findViewById(R.id.userListView);
        memberService = ApiClient.getRetrofitInstance().create(MemberService.class);

        fetchUsers("");

        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = editTextSearch.getText().toString();
                fetchUsers(keyword);
            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserResponse user = (UserResponse) userAdapter.getItem(position);
                navigateToEditUserActivity(user);
            }
        });

        ImageView imageHome = findViewById(R.id.imageHome);
        imageHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserListActivity.this, ManagePageActivity.class);
                startActivity(intent);
                finish();  // Optional: Finish current activity if needed
            }
        });

        ImageView imageSetting = findViewById(R.id.imageSetting);
        imageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserListActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();  // Optional: Finish current activity if needed
            }
        });
    }

    private void fetchUsers(String keyword) {
        memberService.searchUser(keyword, 1, 10).enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserResponse> users = response.body();
                    userAdapter = new UserAdapter(UserListActivity.this, users);
                    userListView.setAdapter(userAdapter);
                } else {
                    Toast.makeText(UserListActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                Toast.makeText(UserListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToEditUserActivity(final UserResponse user) {
        Intent intent = new Intent(this, EditUserActivity.class);
        intent.putExtra("USER_ID", user.getMemberId());
        intent.putExtra("USER_NAME", user.getUserName());
        intent.putExtra("EMAIL", user.getEmail());
        intent.putExtra("PHONE", user.getPhone());
        intent.putExtra("ADDRESS", user.getAddress());
        startActivityForResult(intent, UserAdapter.REQUEST_CODE_EDIT_USER);
    }

    public void deleteUser(long userId) {
        memberService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserListActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchUsers(""); // Refresh the user list
                } else {
                    Toast.makeText(UserListActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UserAdapter.REQUEST_CODE_EDIT_USER && resultCode == RESULT_OK) {
            fetchUsers(""); // Refresh the user list
        }
    }
}
