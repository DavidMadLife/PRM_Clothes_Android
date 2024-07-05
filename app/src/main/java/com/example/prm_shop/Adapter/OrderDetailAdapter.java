// OrderDetailAdapter.java
package com.example.prm_shop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shop.R;
import com.example.prm_shop.models.response.OrderDetailResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private Context context;
    private List<OrderDetailResponse> orderDetails;

    public OrderDetailAdapter(Context context, List<OrderDetailResponse> orderDetails) {
        this.context = context;
        this.orderDetails = orderDetails;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_detail_item, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetailResponse orderDetail = orderDetails.get(position);
        holder.bind(orderDetail);
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    public class OrderDetailViewHolder extends RecyclerView.ViewHolder {

        private TextView productNameTextView, unitPriceTextView, quantityTextView, sizeTextView;
        private ImageView productImageView;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            unitPriceTextView = itemView.findViewById(R.id.unitPriceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            sizeTextView = itemView.findViewById(R.id.sizeTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
        }

        public void bind(OrderDetailResponse orderDetail) {
            productNameTextView.setText("Product Name: " + orderDetail.getProductName());
            unitPriceTextView.setText("Unit Price: " + orderDetail.getUnitPrice() + "VND");
            quantityTextView.setText("Quantity: " + orderDetail.getQuantity());
            sizeTextView.setText("Size: " + orderDetail.getSize());
            Picasso.get().load(orderDetail.getImg()).into(productImageView);
        }
    }
}
