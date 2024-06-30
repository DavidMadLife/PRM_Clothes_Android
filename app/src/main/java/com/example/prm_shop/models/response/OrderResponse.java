package com.example.prm_shop.models.response;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderResponse {

    private int orderId;
    private String orderCode;

    @SerializedName("orderDate")
    @JsonAdapter(DateTypeAdapter.class)
    private Date orderDate;

    @SerializedName("shippedDate")
    @JsonAdapter(DateTypeAdapter.class)
    private Date shippedDate;

    private List<OrderDetailResponse> orderDetails;
    private String status;
    private double total;

    // Constructors, getters, and setters

    public OrderResponse(int orderId, String orderCode, Date orderDate, Date shippedDate, List<OrderDetailResponse> orderDetails, String status, double total) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.orderDate = orderDate;
        this.shippedDate = shippedDate;
        this.orderDetails = orderDetails;
        this.status = status;
        this.total = total;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public List<OrderDetailResponse> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailResponse> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Inner class DateTypeAdapter
    public static class DateTypeAdapter extends TypeAdapter<Date> {

        private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.US);

        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(dateFormat.format(value));
            }
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            if (in != null) {
                try {
                    return dateFormat.parse(in.nextString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
