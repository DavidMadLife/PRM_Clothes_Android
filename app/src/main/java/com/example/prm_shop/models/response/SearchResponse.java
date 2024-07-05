package com.example.prm_shop.models.response;

import java.util.List;

public class SearchResponse {
    private List<ProductResponse> products;
    private int totalCount;

    public List<ProductResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponse> products) {
        this.products = products;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
