package com.example.prm_shop.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_shop.Adapter.ChatAdapter;
import com.example.prm_shop.R;
import com.example.prm_shop.models.request.ChatMessage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChatActivity extends BaseActivity {
    private static final String TAG = "ChatActivity";
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private List<ChatMessage> chatMessages;
    private EditText editTextMessage;
    private ImageView buttonSendMessage;
    private RecyclerView recyclerViewChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();

        initViews();
        setupRecyclerView();
        setupSendButton();
        loadMessages();
    }

    private void initViews() {
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
    }

    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, preferenceManager.getUserId());
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);
    }

    private void setupSendButton() {
        buttonSendMessage.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                editTextMessage.setText("");
            }
        });
    }

    private void sendMessage(String messageText) {
        ChatMessage chatMessage = new ChatMessage(
                preferenceManager.getUserId(),
                getIntent().getStringExtra("userId"),
                messageText,
                new Date()
        );

        database.collection("chats")
                .add(chatMessage)
                .addOnSuccessListener(documentReference -> {
                    // Xóa tin nhắn cũ và thêm tin nhắn mới
                    chatMessages.add(chatMessage);
                    // Cập nhật RecyclerView
                    chatAdapter.notifyDataSetChanged();
                    scrollToBottom();
                })
                .addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Failed to send message!", Toast.LENGTH_SHORT).show());
    }



    private void loadMessages() {
        String currentUserId = preferenceManager.getUserId();
        String otherUserId = getIntent().getStringExtra("userId");

        database.collection("chats")
                .whereEqualTo("senderId", currentUserId)
                .whereEqualTo("receiverId", otherUserId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error fetching sent messages: ", error);
                        return;
                    }

                    if (querySnapshot != null) {
                        List<ChatMessage> sentMessages = new ArrayList<>();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            ChatMessage chatMessage = document.toObject(ChatMessage.class);
                            sentMessages.add(chatMessage);
                        }
                        mergeAndSortMessages(sentMessages);
                    }
                });

        database.collection("chats")
                .whereEqualTo("senderId", otherUserId)
                .whereEqualTo("receiverId", currentUserId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error fetching received messages: ", error);
                        return;
                    }

                    if (querySnapshot != null) {
                        List<ChatMessage> receivedMessages = new ArrayList<>();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            ChatMessage chatMessage = document.toObject(ChatMessage.class);
                            receivedMessages.add(chatMessage);
                        }
                        mergeAndSortMessages(receivedMessages);
                    }
                });
    }

    private void mergeAndSortMessages(List<ChatMessage> newMessages) {
        chatMessages.addAll(newMessages);
        // Sắp xếp lại danh sách chatMessages theo thời gian tạo (createdAt)
        Collections.sort(chatMessages, (msg1, msg2) -> msg1.getCreatedAt().compareTo(msg2.getCreatedAt()));
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
    }



    private void scrollToBottom() {
        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
    }
}
