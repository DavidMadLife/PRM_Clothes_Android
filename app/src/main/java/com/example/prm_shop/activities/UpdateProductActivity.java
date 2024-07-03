package com.example.prm_shop.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_shop.Helper.FileUtils;
import com.example.prm_shop.R;
import com.example.prm_shop.models.request.ProductRequest;
import com.example.prm_shop.models.response.ProductResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.ProductService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText productNameInput, productDescriptionInput, productPriceInput, productStockInput;
    private Button updateButton, selectImageButton;
    private ImageView productImageView;
    private ProductResponse productToUpdate;
    private ProductService productService;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_update);

        // Initialize UI elements
        productNameInput = findViewById(R.id.edit_text_product_name);
        productDescriptionInput = findViewById(R.id.edit_text_product_description);
        productPriceInput = findViewById(R.id.edit_text_product_price);
        productStockInput = findViewById(R.id.edit_text_product_stock);
        updateButton = findViewById(R.id.button_update_product);
        selectImageButton = findViewById(R.id.button_select_image);
        productImageView = findViewById(R.id.product_image_view);

        // Get product data from intent
        productToUpdate = (ProductResponse) getIntent().getSerializableExtra("product");

        // Set initial values in UI
        if (productToUpdate != null) {
            productNameInput.setText(productToUpdate.getProductName());
            productDescriptionInput.setText(productToUpdate.getDescription());
            productPriceInput.setText(String.valueOf(productToUpdate.getUnitPrice()));
            productStockInput.setText(String.valueOf(productToUpdate.getUnitsInStock()));
            // Load the existing image into the ImageView
            if (productToUpdate.getImg() != null && !productToUpdate.getImg().isEmpty()) {
                Picasso.get().load(productToUpdate.getImg()).into(productImageView);
            }
        }

        // Initialize Retrofit service
        productService = ApiClient.getRetrofitInstance().create(ProductService.class);

        // Handle update button click
        updateButton.setOnClickListener(view -> updateProduct());

        // Handle select image button click
        selectImageButton.setOnClickListener(view -> selectImage());
    }

    private void updateProduct() {
        // Get updated values from UI
        String productName = productNameInput.getText().toString().trim();
        String description = productDescriptionInput.getText().toString().trim();
        double price = Double.parseDouble(productPriceInput.getText().toString().trim());
        int stock = Integer.parseInt(productStockInput.getText().toString().trim());

        // Create request body parts
        RequestBody categoryId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(productToUpdate.getCategoryId()));
        RequestBody providerId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(productToUpdate.getProviderId()));
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), productName);
        RequestBody weight = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(productToUpdate.getWeight()));
        RequestBody unitPrice = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(price));
        RequestBody unitsInStock = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stock));
        RequestBody status = RequestBody.create(MediaType.parse("text/plain"), productToUpdate.getStatus());
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);

        // Create file part
        MultipartBody.Part filePart = null;
        if (imageFile != null) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            filePart = MultipartBody.Part.createFormData("ImgFile", imageFile.getName(), fileBody);
        }

        // Make API call to update product
        Call<ProductRequest> call = productService.updateProduct(
                productToUpdate.getProductId(),
                categoryId,
                providerId,
                name,
                weight,
                unitPrice,
                unitsInStock,
                status,
                descriptionBody,
                filePart
        );

        call.enqueue(new Callback<ProductRequest>() {
            @Override
            public void onResponse(Call<ProductRequest> call, Response<ProductRequest> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    // Finish activity and refresh product list on the previous screen
                    finishAndUpdateProductList();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(UpdateProductActivity.this, "Failed to update product: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(UpdateProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductRequest> call, Throwable t) {
                Toast.makeText(UpdateProductActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void finishAndUpdateProductList() {
        // Return to previous activity (ProductManageActivity) and refresh product list
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                productImageView.setImageBitmap(selectedImage);
                String imagePath = FileUtils.getPath(this, selectedImageUri);
                imageFile = new File(imagePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
