package com.example.prm_shop.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shop.R;
import com.example.prm_shop.activities.OrderAdminActivity;
import com.example.prm_shop.activities.ProductManageActivity;
import com.example.prm_shop.models.response.OrderResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.OrderService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public void clear() {
        orderList.clear();
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
        private ImageButton detailButton;

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

            String statusText = "Order Status: " + order.getStatus();
            SpannableString spannableString = new SpannableString(statusText);
            spannableString.setSpan(new ForegroundColorSpan(color), 13, statusText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            statusTextView.setText(spannableString);
        }
    }

    /*private void showDetailPopup(OrderResponse order) {
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
        Picasso.get().load(orderDetail.getImg()).into(productImageView);

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
    }*/
    // OrderAdapter.java (showDetailPopup method)
    private void showDetailPopup(OrderResponse order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.popup_order_detail, null);
        builder.setView(popupView);

        //TextView orderCodeTextView = popupView.findViewById(R.id.orderCodeTextView);
        RecyclerView orderDetailsRecyclerView = popupView.findViewById(R.id.orderDetailsRecyclerView);

       // orderCodeTextView.setText("Order Code: " + order.getOrderCode());

        // Setup RecyclerView for order details
        orderDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(context, order.getOrderDetails());
        orderDetailsRecyclerView.setAdapter(orderDetailAdapter);

        ImageButton confirmButton = popupView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder(order.getOrderId());
            }
        });

        ImageButton rejectButton = popupView.findViewById(R.id.rejectButton);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder(order.getOrderId());
            }
        });

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createOrder(int orderId) {

        OrderResponse order;
        OrderService orderService = ApiClient.getRetrofitInstance().create(OrderService.class);

        Call<Void> call = orderService.confirmOrder(orderId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(context, "Order confirmed successfully", Toast.LENGTH_SHORT).show();
                    Log.d("OrderAdminActivity", "Order confirmed");

                    ((OrderAdminActivity) context).callPendingOrdersApi("");

                } else {
                    Log.d("OrderAdminActivity", "Order fail " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("OrderAdminActivity", "Order failure");
            }
        });
    }


    private void rejectOrder(int orderId) {

        OrderResponse order;
        OrderService orderService = ApiClient.getRetrofitInstance().create(OrderService.class);

        Call<Void> call = orderService.rejectOrder(orderId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(context, "Order Rejected successfully", Toast.LENGTH_SHORT).show();
                    Log.d("OrderAdminActivity", "Order Rejected");

                    ((OrderAdminActivity) context).callPendingOrdersApi("");

                } else {
                    Log.d("OrderAdminActivity", "Order Rejected fail " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("OrderAdminActivity", "Order Rejected failure");
            }
        });
    }

}
