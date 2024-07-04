package com.example.prm_shop.network;

import android.util.Pair;

import com.example.prm_shop.models.request.MainViewModel;

import org.jetbrains.annotations.NotNull;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebsocketListener extends WebSocketListener {

    private MainViewModel viewModel;

    public WebsocketListener(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        viewModel.setStatus(true);
        webSocket.send("Android Device Connected.");
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        viewModel.setMessage(new Pair<>(false, text));
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
        viewModel.setStatus(false);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        // Handle failure
    }
}
