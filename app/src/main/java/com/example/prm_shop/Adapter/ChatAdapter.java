package com.example.prm_shop.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shop.R;
import com.example.prm_shop.models.request.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private final List<ChatMessage> messages;
    private final String senderId;

    public ChatAdapter(List<ChatMessage> messages, String senderId) {
        this.messages = messages;
        this.senderId = senderId; // Lưu senderId để phân biệt tin nhắn đã gửi và đã nhận
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT; // Tin nhắn đã gửi
        } else {
            return VIEW_TYPE_RECEIVED; // Tin nhắn đã nhận
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_send_message, parent, false); // Layout cho tin nhắn đã gửi
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_message, parent, false); // Layout cho tin nhắn đã nhận
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message); // Gán dữ liệu cho tin nhắn đã gửi
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message); // Gán dữ liệu cho tin nhắn đã nhận
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMessage;
        private final TextView textViewCreatedAt;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage); // TextView để hiển thị nội dung tin nhắn
            textViewCreatedAt = itemView.findViewById(R.id.textViewCreatedAt); // TextView để hiển thị thời gian gửi tin nhắn
        }

        public void bind(ChatMessage message) {
            textViewMessage.setText(message.getMessage()); // Hiển thị nội dung tin nhắn
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(message.getCreatedAt()); // Định dạng thời gian gửi tin nhắn
            textViewCreatedAt.setText(formattedDate); // Hiển thị thời gian gửi tin nhắn
        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMessage;
        private final TextView textViewCreatedAt;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage); // TextView để hiển thị nội dung tin nhắn
            textViewCreatedAt = itemView.findViewById(R.id.textViewCreatedAt); // TextView để hiển thị thời gian gửi tin nhắn
        }

        public void bind(ChatMessage message) {
            textViewMessage.setText(message.getMessage()); // Hiển thị nội dung tin nhắn
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(message.getCreatedAt()); // Định dạng thời gian gửi tin nhắn
            textViewCreatedAt.setText(formattedDate); // Hiển thị thời gian gửi tin nhắn
        }


    }
}
