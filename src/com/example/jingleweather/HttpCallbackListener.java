package com.example.jingleweather;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

}
