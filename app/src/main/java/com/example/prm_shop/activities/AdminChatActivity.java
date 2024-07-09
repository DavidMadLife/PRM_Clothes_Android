package com.example.prm_shop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shop.Adapter.UserChatAdapter;
import com.example.prm_shop.R;
import com.example.prm_shop.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminChatActivity extends BaseActivity implements UserChatAdapter.UserClickListener {
    private RecyclerView recyclerViewUsers;
    private UserChatAdapter userChatAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_chat_activity);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userChatAdapter = new UserChatAdapter(userList, this); // Truyền context và listener vào adapter
        recyclerViewUsers.setAdapter(userChatAdapter);

        loadUsersFromFirestore();
    }

    private void loadUsersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = getCurrentUserId(); // Hàm này để lấy userId của người dùng hiện tại

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getId(); // Lấy userId từ Firestore document
                            if (!userId.equals(currentUserId)) {
                                String userName = document.getString("name");
                                String roleName = document.getString("roleId");
                                User user = new User(userId, userName, roleName); // Truyền userId vào User model
                                userList.add(user);
                            }
                        }
                        userChatAdapter.notifyDataSetChanged();
                    } else {
                        // Xử lý lỗi
                    }
                });
    }

    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("userId", ""); // Lấy userId từ SharedPreferences
    }

    @Override
    public void onUserClick(int position) {
        User user = userList.get(position);
        // Chuyển sang ChatActivity với userId của người dùng được chọn
        Intent intent = new Intent(AdminChatActivity.this, ChatActivity.class);
        intent.putExtra("userId", user.getUserId()); // Truyền userId của người dùng đến ChatActivity
        startActivity(intent);
    }
}