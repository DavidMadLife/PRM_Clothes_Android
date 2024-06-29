package com.example.prm_shop.Adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shop.R;
import com.example.prm_shop.models.response.CartItemResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItemResponse> items;
    private OnItemClickListener onItemClickListener;
    private boolean showingDeleteButton = false; // Trạng thái hiển thị nút xóa
    private boolean showingUpdateButton = false; // Trạng thái hiển thị nút cập nhật

    public CartAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public void showDeleteButton(int position, boolean show) {
        showingDeleteButton = show; // Hiển thị nút xóa khi vuốt
        notifyItemChanged(position);
    }

    public void showUpdateButton(int position, boolean show) {
        showingUpdateButton = show; // Hiển thị nút cập nhật khi vuốt
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemResponse item = items.get(position);
        holder.bind(item, showingDeleteButton, showingUpdateButton); // Truyền trạng thái hiển thị nút xóa và cập nhật
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImageView;
        private TextView productNameTextView;
        private TextView quantityTextView;
        private TextView unitPriceTextView;

        private TextView sizeTextView;
        private Button btnDelete;
        private Button btnUpdate;

        public CartViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.image_product);
            productNameTextView = itemView.findViewById(R.id.text_product_name);
            quantityTextView = itemView.findViewById(R.id.text_quantity);
            unitPriceTextView = itemView.findViewById(R.id.text_unit_price);
            sizeTextView = itemView.findViewById(R.id.text_size);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnUpdate = itemView.findViewById(R.id.btn_update);

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onDeleteClick(position);
                }
            });

            btnUpdate.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onUpdateClick(position);
                }
            });
        }

        public void bind(CartItemResponse item, boolean showingDeleteButton, boolean showingUpdateButton) {
            productNameTextView.setText(item.getProductName());
            quantityTextView.setText("Quantity: " + item.getQuantity());
            unitPriceTextView.setText("Unit Price: $" + item.getUnitPrice());
            sizeTextView.setText("Size: " + item.getSize());
            Picasso.get().load(item.getImg()).into(productImageView);

            // Đặt nút xóa ẩn hoặc hiển thị dựa trên trạng thái
            if (showingDeleteButton) {
                btnDelete.setVisibility(View.VISIBLE);
            } else {
                btnDelete.setVisibility(View.GONE);
            }

            // Đặt nút cập nhật ẩn hoặc hiển thị dựa trên trạng thái
            if (showingUpdateButton) {
                btnUpdate.setVisibility(View.VISIBLE);
            } else {
                btnUpdate.setVisibility(View.GONE);
            }

            itemView.setOnTouchListener(new View.OnTouchListener() {
                private float initialX = 0;
                private static final int SWIPE_THRESHOLD = 100;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = event.getX();
                            return true;
                        case MotionEvent.ACTION_UP:
                            float finalX = event.getX();
                            if (finalX - initialX > SWIPE_THRESHOLD) {
                                // Vuốt sang phải
                                btnDelete.setVisibility(View.GONE);
                                btnUpdate.setVisibility(View.GONE); // Ẩn cả nút cập nhật khi vuốt sang phải
                            } else if (initialX - finalX > SWIPE_THRESHOLD) {
                                // Vuốt sang trái
                                btnDelete.setVisibility(View.VISIBLE);
                                btnUpdate.setVisibility(View.VISIBLE); // Hiển thị nút cập nhật khi vuốt sang trái
                            }
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);

        void onUpdateClick(int position);
    }
}
