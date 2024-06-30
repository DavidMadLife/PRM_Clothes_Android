package com.example.prm_shop.models.request;

public class OrderRequest {
    private int memberId;

    public OrderRequest(int memberId) {
        this.memberId = memberId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
}
