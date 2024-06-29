package com.example.prm_shop.models.request;

public class RemoveItemRequest {
    private int memberId;
    private int productId;

    public RemoveItemRequest(int memberId, int productId) {
        this.memberId = memberId;
        this.productId = productId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
