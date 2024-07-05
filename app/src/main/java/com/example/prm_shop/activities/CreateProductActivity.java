package com.example.prm_shop.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.prm_shop.Helper.FileUtils;
import com.example.prm_shop.R;
import com.example.prm_shop.models.response.ProductResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.ProductService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateProductActivity extends FooterActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText unitsInStock, unitPrice, providerId, status, weight, categoryId, productName, description;
    private ImageView imgPreview;
    private Uri imgUri;
    private Button chooseImageButton, createProductButton;

    private static final int REQUEST_CODE_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        if (!hasStoragePermissions()) {
            requestStoragePermissions();
        }

        unitsInStock = findViewById(R.id.units_in_stock);
        unitPrice = findViewById(R.id.unit_price);
        providerId = findViewById(R.id.provider_id);
        status = findViewById(R.id.status);
        weight = findViewById(R.id.weight);
        categoryId = findViewById(R.id.category_id);
        productName = findViewById(R.id.product_name);
        description = findViewById(R.id.description);
        imgPreview = findViewById(R.id.img_preview);
        chooseImageButton = findViewById(R.id.choose_image_button);
        createProductButton = findViewById(R.id.create_product_button);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        createProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateProductActivity.this, ProductManageActivity.class);
                createProduct();
                startActivity(intent);
            }
        });
    }

    private boolean hasStoragePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            int readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(android.net.Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, REQUEST_CODE_PERMISSIONS);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, REQUEST_CODE_PERMISSIONS);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            imgPreview.setImageURI(imgUri);
        }
    }

    private void createProduct() {
        if (imgUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductService productService = ApiClient.getRetrofitInstance().create(ProductService.class);

        RequestBody unitsInStockBody = RequestBody.create(MediaType.parse("text/plain"), unitsInStock.getText().toString());
        RequestBody unitPriceBody = RequestBody.create(MediaType.parse("text/plain"), unitPrice.getText().toString());
        RequestBody providerIdBody = RequestBody.create(MediaType.parse("text/plain"), providerId.getText().toString());
        RequestBody statusBody = RequestBody.create(MediaType.parse("text/plain"), status.getText().toString());
        RequestBody weightBody = RequestBody.create(MediaType.parse("text/plain"), weight.getText().toString());
        RequestBody categoryIdBody = RequestBody.create(MediaType.parse("text/plain"), categoryId.getText().toString());
        RequestBody productNameBody = RequestBody.create(MediaType.parse("text/plain"), productName.getText().toString());
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description.getText().toString());

        String filePath = FileUtils.getPath(this, imgUri);
        if (filePath == null) {
            Toast.makeText(this, "Failed to get image file path", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(this, "Image file does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imgUri)), file);
        MultipartBody.Part imgFile = MultipartBody.Part.createFormData("ImgFile", file.getName(), requestFile);

        Call<ProductResponse> call = productService.createProduct(unitsInStockBody, unitPriceBody, providerIdBody, statusBody, weightBody, categoryIdBody, productNameBody, descriptionBody, imgFile);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateProductActivity.this, "Product created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateProductActivity.this, "Failed to create product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(CreateProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
