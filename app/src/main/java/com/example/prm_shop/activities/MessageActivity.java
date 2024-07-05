package com.example.prm_shop.activities;

import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm_shop.R;
import com.example.prm_shop.models.request.MainViewModel;
import com.example.prm_shop.network.WebsocketListener;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MessageActivity extends BaseActivity {

    private WebsocketListener websocketListener;
    private MainViewModel viewModel;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private WebSocket webSocket;

    private Button btnConnect;
    private Button btnDisconnect;
    private TextView tvMessage;
    private EditText edtMessage;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        btnConnect = findViewById(R.id.btnConnect);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        tvMessage = findViewById(R.id.tvMessage);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        websocketListener = new WebsocketListener(viewModel);

        viewModel.getSocketStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean connected) {
                runOnUiThread(() -> {
                    tvMessage.setText(connected ? "Connected" : "Disconnected");
                });
            }
        });

        viewModel.getMessage().observe(this, new Observer<Pair<Boolean, String>>() {
            @Override
            public void onChanged(Pair<Boolean, String> messagePair) {
                runOnUiThread(() -> {
                    String text = tvMessage.getText().toString();
                    text += messagePair.first ? "You: " : "Other: ";
                    text += messagePair.second + "\n";
                    tvMessage.setText(text);
                });
            }
        });

        btnConnect.setOnClickListener(view -> {
            webSocket = okHttpClient.newWebSocket(createRequest(), websocketListener);
        });

        btnDisconnect.setOnClickListener(view -> {
            if (webSocket != null) {
                webSocket.close(1000, "Cancelled Manually");
            }
        });

        btnSend.setOnClickListener(view -> {
            String messageText = edtMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                if (webSocket != null) {
                    webSocket.send(messageText);
                    viewModel.setMessage(new Pair<>(true, messageText));
                    edtMessage.setText("");
                }
            } else {
                Toast.makeText(MessageActivity.this, "Enter something here...", Toast.LENGTH_LONG).show();
            }
        });
    }

    private Request createRequest() {
        String webSocketUrl = "wss://free.blr2.piesocket.com/v3/1?api_key=z2gDo30Gas9jsw21pdZoL46FxoX92nwF0Myo9BNh&notify_self=1"; // Replace with your WebSocket URL
        return new Request.Builder().url(webSocketUrl).build();
    }
}
