package com.example.prm_shop.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shop.R;
import com.example.prm_shop.models.response.OrderDetailResponse;
import com.example.prm_shop.models.response.OrderResponse;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderResponse> orderList;

    public OrderAdapter(Context context, List<OrderResponse> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setData(List<OrderResponse> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderResponse order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView orderCodeTextView, orderDateTextView, totalTextView, statusTextView;
        private Button detailButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderCodeTextView = itemView.findViewById(R.id.orderCodeTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            totalTextView = itemView.findViewById(R.id.totalTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            detailButton = itemView.findViewById(R.id.detailButton);

            detailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        OrderResponse order = orderList.get(position);
                        showDetailPopup(order);
                    }
                }
            });
        }

        public void bind(OrderResponse order) {
            orderCodeTextView.setText("Order Code: " + order.getOrderCode());
            orderDateTextView.setText("Order Date: " + order.getOrderDate());
            totalTextView.setText("Total: $" + order.getTotal());
            int color;
            switch (order.getStatus()) {
                case "Confirmed":
                    color = 0xFF00FF00; // Green color
                    break;
                case "Rejected":
                    color = 0xFFFF0000; // Red color
                    break;
                case "Pending":
                default:
                    color = 0xFF000000; // Black color (default)
                    break;
            }
            statusTextView.setText("Order Status: " + order.getStatus());
            statusTextView.setTextColor(color);
        }
    }

    private void showDetailPopup(OrderResponse order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.popup_order_detail, null);
        builder.setView(popupView);

        TextView productNameTextView = popupView.findViewById(R.id.productNameTextView);
        TextView unitPriceTextView = popupView.findViewById(R.id.unitPriceTextView);
        TextView quantityTextView = popupView.findViewById(R.id.quantityTextView);
        TextView discountTextView = popupView.findViewById(R.id.discountTextView);
        TextView sizeTextView = popupView.findViewById(R.id.sizeTextView);
        ImageView productImageView = popupView.findViewById(R.id.productImageView);

        // Set data to views in the popup
        OrderDetailResponse orderDetail = order.getOrderDetails().get(0); // Assuming first detail for simplicity
        productNameTextView.setText("Product Name: " + orderDetail.getProductName());
        unitPriceTextView.setText("Unit Price: $" + orderDetail.getUnitPrice());
        quantityTextView.setText("Quantity: " + orderDetail.getQuantity());
        discountTextView.setText("Discount: $" + orderDetail.getDiscount());
        sizeTextView.setText("Size: " + orderDetail.getSize());

        // Load image using Glide or Picasso
        // Example with Picasso:
        // Picasso.get().load(orderDetail.getImg()).into(productImageView);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
