package com.example.prm_shop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shop.R;
import com.example.prm_shop.models.response.ProductResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductManageAdapter extends RecyclerView.Adapter<ProductManageAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductResponse> productList;
    private ProductManageListener manageListener;

    public ProductManageAdapter(Context context, ProductManageListener manageListener) {
        this.context = context;
        this.manageListener = manageListener;
    }

    public void setProductList(List<ProductResponse> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductResponse product = productList.get(position);
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText("Price: " + product.getUnitPrice() + "VND");
        Picasso.get().load(product.getImg()).into(holder.productImage);

        holder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageListener.onProductUpdate(product);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageListener.onProductDelete(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        TextView productPrice;
        ImageView productImage;
        Button updateButton;
        Button deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.manage_product_name);
            productPrice = itemView.findViewById(R.id.manage_product_price);
            productImage = itemView.findViewById(R.id.manage_product_image);
            updateButton = itemView.findViewById(R.id.button_manage_update);
            deleteButton = itemView.findViewById(R.id.button_manage_delete);
        }
    }

    public interface ProductManageListener {
        void onProductUpdate(ProductResponse product);
        void onProductDelete(ProductResponse product);
    }
}
