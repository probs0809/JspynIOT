package com.tnlsystems.jspynio;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by prabodhmayekar on 10/11/18.
 */

public class JResHandler extends WebSocketListener{
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private String API;

    public JResHandler(String API){
        this.API = API;
    }
    private OkHttpClient client = new OkHttpClient();
    public void start() {
        Request request = new Request.Builder().url("ws://192.168.43.146:3000").build();
        WebSocket ws = client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();
    }


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("{\"api\":\""+this.API+"\",\"id\":\"IOT_DEVICE\"}");

    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {

    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {

    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);

    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {

    }



}
