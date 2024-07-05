package com.example.prm_shop.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm_shop.Adapter.OrderAdapter;
import com.example.prm_shop.R;
import com.example.prm_shop.models.response.OrderResponse;
import com.example.prm_shop.network.ApiClient;
import com.example.prm_shop.network.OrderService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdminActivity extends BaseActivity {

    private Spinner spinnerActions;
    private Button btnPrev, btnNext;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private OrderService orderService;

    private TextView textPageNumber, editTextSearch;

    private int pageIndex = 1;
    private int pageSize = 2;

    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        spinnerActions = findViewById(R.id.spinnerActions);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        recyclerView = findViewById(R.id.recyclerView);
        textPageNumber = findViewById(R.id.textPageNumber);
        editTextSearch = findViewById(R.id.editTextSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(orderAdapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_actions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActions.setAdapter(adapter);
        spinnerActions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String action = parent.getItemAtPosition(position).toString();
                String keyword = editTextSearch.getText().toString().trim();
                switch (action) {
                    case "Pending":
                        pageIndex = 1;
                        callPendingOrdersApi(keyword);
                        break;
                    case "Confirm":
                        pageIndex = 1;
                        callConfirmedOrdersApi(keyword);
                        break;
                    case "Reject":
                        pageIndex = 1;
                        callRejectedOrdersApi(keyword);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });

        // Set click listeners for navigation buttons
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageIndex > 1) {
                    pageIndex--;
                    handlePagination();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageIndex < totalPages) {
                    pageIndex++;
                    handlePagination();
                }
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                String selectedAction = spinnerActions.getSelectedItem().toString();
                switch (selectedAction) {
                    case "Pending":
                        callPendingOrdersApi(keyword);
                        break;
                    case "Confirm":
                        callConfirmedOrdersApi(keyword);
                        break;
                    case "Reject":
                        callRejectedOrdersApi(keyword);
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Initial API call
        callPendingOrdersApi("");
    }

    private void handlePagination() {
        String selectedAction = spinnerActions.getSelectedItem().toString();
        String keyword = editTextSearch.getText().toString().trim();
        switch (selectedAction) {
            case "Pending":
                callPendingOrdersApi(keyword);
                break;
            case "Confirm":
                callConfirmedOrdersApi(keyword);
                break;
            case "Reject":
                callRejectedOrdersApi(keyword);
                break;
        }
    }



    public void callPendingOrdersApi(String keyword) {
        orderService = ApiClient.getRetrofitInstance().create(OrderService.class);
        Call<List<OrderResponse>> call = orderService.getOrderByStatusPending(keyword, pageIndex, pageSize);

        call.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.isSuccessful()) {
                    List<OrderResponse> orders = response.body();
                    if (orders != null) {
                        orderAdapter.setData(orders);
                        Log.d("OrderAdminActivity", "Pending orders loaded: " + orders.size());
                        getTotalOrderSize("Pending");
                    } else {
                        Log.d("OrderAdminActivity", "No pending orders found");
                        orderAdapter.clear();
                        Toast.makeText(OrderAdminActivity.this, "No pending orders found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("OrderAdminActivity", "Failed to fetch pending orders");
                    Toast.makeText(OrderAdminActivity.this, "Failed to fetch pending orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                Log.e("OrderAdminActivity", "Error fetching pending orders: " + t.toString());
                Toast.makeText(OrderAdminActivity.this, "Error fetching pending orders: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callConfirmedOrdersApi(String keyword) {
        orderService = ApiClient.getRetrofitInstance().create(OrderService.class);
        Call<List<OrderResponse>> call = orderService.getOrderByStatusConfirmed(keyword, pageIndex, pageSize);

        call.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.isSuccessful()) {
                    List<OrderResponse> orders = response.body();
                    if (orders != null) {
                        orderAdapter.setData(orders);
                        Log.d("OrderAdminActivity", "Confirmed orders loaded: " + orders.size());
                        getTotalOrderSize("Confirmed");
                    } else {
                        Log.d("OrderAdminActivity", "No confirmed orders found");
                        orderAdapter.clear();
                        Toast.makeText(OrderAdminActivity.this, "No confirmed orders found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("OrderAdminActivity", "Failed to fetch confirmed orders");
                    Toast.makeText(OrderAdminActivity.this, "Failed to fetch confirmed orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                Log.e("OrderAdminActivity", "Error fetching confirmed orders: " + t.toString());
                Toast.makeText(OrderAdminActivity.this, "Error fetching confirmed orders: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callRejectedOrdersApi(String keyword) {
        orderService = ApiClient.getRetrofitInstance().create(OrderService.class);
        Call<List<OrderResponse>> call = orderService.getOrderByStatusRejected(keyword, pageIndex, pageSize);

        call.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.isSuccessful()) {
                    List<OrderResponse> orders = response.body();
                    if (orders != null) {
                        orderAdapter.setData(orders);
                        Log.d("OrderAdminActivity", "Rejected orders loaded: " + orders.size());
                        getTotalOrderSize("Rejected");
                    } else {
                        Log.d("OrderAdminActivity", "No rejected orders found");
                        orderAdapter.clear();
                        Toast.makeText(OrderAdminActivity.this, "No rejected orders found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("OrderAdminActivity", "Failed to fetch rejected orders");
                    Toast.makeText(OrderAdminActivity.this, "Failed to fetch rejected orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                Log.e("OrderAdminActivity", "Error fetching rejected orders: " + t.toString());
                Toast.makeText(OrderAdminActivity.this, "Error fetching rejected orders: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalOrderSize(String status) {
        // Depending on the status, call the appropriate API to get total order count
        Call<List<OrderResponse>> call;
        switch (status) {
            case "Pending":
                call = orderService.getOrderByStatusPending("", 1, Integer.MAX_VALUE);
                break;
            case "Confirmed":
                call = orderService.getOrderByStatusConfirmed("", 1, Integer.MAX_VALUE);
                break;
            case "Rejected":
                call = orderService.getOrderByStatusRejected("", 1, Integer.MAX_VALUE);
                break;
            default:
                return;
        }

        call.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                if (response.isSuccessful()) {
                    List<OrderResponse> orders = response.body();
                    if (orders != null) {
                        int totalItems = orders.size();
                        totalPages = (int) Math.ceil((double) totalItems / pageSize);
                        Log.d("OrderAdminActivity", "TotalPages: " + totalPages);
                        updatePageNumberText();
                    }
                } else {
                    Log.e("OrderAdminActivity", "Failed to fetch total order size");
                    Toast.makeText(OrderAdminActivity.this, "Failed to fetch total order size", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                Log.e("OrderAdminActivity", "Error fetching total order size: " + t.toString());
                Toast.makeText(OrderAdminActivity.this, "Error fetching total order size: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePageNumberText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= totalPages; i++) {
            sb.append(i);
            if (i < totalPages) {
                sb.append(", ");
            }
        }
        textPageNumber.setText(sb.toString());

        highlightCurrentPage();
    }

    private void highlightCurrentPage() {
        String pageNumbers = textPageNumber.getText().toString();
        SpannableString spannableString = new SpannableString(pageNumbers);

        int startIndex = 0;
        int endIndex = 0;
        int currentPosition = 0;
        for (int i = 1; i <= totalPages; i++) {
            startIndex = currentPosition;
            endIndex = startIndex + String.valueOf(i).length();

            if (i == pageIndex) {
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)),
                        startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            currentPosition = endIndex + 2; // Skip the comma and space
        }

        textPageNumber.setText(spannableString);
    }
}


