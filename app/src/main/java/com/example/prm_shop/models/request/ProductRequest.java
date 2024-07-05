package com.example.prm_shop.models.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductRequest implements Serializable {

    @SerializedName("productId")
    private int productId;

    @SerializedName("categoryId")
    private int categoryId;

    @SerializedName("providerId")
    private int providerId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("description")
    private String description;

    @SerializedName("unitPrice")
    private double unitPrice;

    @SerializedName("unitsInStock")
    private int unitsInStock;

    @SerializedName("status")
    private String status;

    @SerializedName("img")
    private String img;

    @SerializedName("weight")
    private double weight;

    // Getters and setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getProviderId() { return providerId; }
    public void setProviderId(int providerId) { this.providerId = providerId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getUnitsInStock() { return unitsInStock; }
    public void setUnitsInStock(int unitsInStock) { this.unitsInStock = unitsInStock; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}
