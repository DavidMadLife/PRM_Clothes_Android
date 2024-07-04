package com.example.prm_shop.models.request;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Boolean> socketStatus = new MutableLiveData<>();
    public LiveData<Boolean> getSocketStatus() {
        return socketStatus;
    }

    private MutableLiveData<Pair<Boolean, String>> message = new MutableLiveData<>();
    public LiveData<Pair<Boolean, String>> getMessage() {
        return message;
    }

    public void setStatus(boolean status) {
        dispatchMain(() -> socketStatus.setValue(status));
    }

    public void setMessage(Pair<Boolean, String> messagePair) {
        dispatchMain(() -> {
            if (socketStatus.getValue() != null && socketStatus.getValue()) {
                message.setValue(messagePair);
            }
        });
    }

    private void dispatchMain(Runnable block) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(block);
    }
}
