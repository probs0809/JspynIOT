package com.tnlsystems.jspynio;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by prabodhmayekar on 09/11/18.
 */

public class Jspyn {
    private MediaType JSON;
    private String postUrl = "https://us-central1-jspyn-39604.cloudfunctions.net/jspynio/transmit";
    private String postBody;
    private String API;
    private String temp;

    public Jspyn(String API) {
        this.API = API;

    }

    public void digitalWrite(int pin,String state) {


        String Pin;

        if (pin < 10){

            Pin = "0" + Integer.toString(pin);
        }
        else{
            Pin =  Integer.toString(pin);
        }

        if (state == "HIGH" ||  state == "1"){
            temp = "H";
        }
        else if (state == "LOW" ||  state == "0"){
            temp = "L";
        }
        String GOD = "DN" + Pin + temp + "000";


        //postUrl = "https://us-central1-myapplication3-da8f2.cloudfunctions.net/app/transmit";
        postBody = "{\n" +
                "    \"api\": \"" + API + "\",\n" +
                "    \"gpioValue\": \"" + GOD + "\"\n" +
                "}";

        JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            postRequest(postUrl,postBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void digitalWrite(int pin,int opr) {


        String Pin;

        if (pin < 10){

            Pin = "0" + Integer.toString(pin);
        }
        else{
            Pin =  Integer.toString(pin);
        }

        if (opr == 1){
            temp = "H";
        }
        else if (opr == 0){
            temp = "L";
        }
        String GOD = "DN" + Pin + temp + "000";


        //postUrl = "https://us-central1-myapplication3-da8f2.cloudfunctions.net/app/transmit";
        postBody = "{\n" +
                "    \"api\": \"" + API + "\",\n" +
                "    \"gpioValue\": \"" + GOD + "\"\n" +
                "}";

        JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            postRequest(postUrl,postBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void analogWrite(int pin,int pwm) {


        String Pin;

        if (pin < 10){

            Pin = "0" + Integer.toString(pin);
        }
        else{
            Pin =  Integer.toString(pin);
        }

        String GOD;
        if (pwm/10 <1){

            GOD = "AP" + Pin + "H00" + pwm;
        }
        else if (pwm/100 <1){

            GOD = "AP" + Pin + "H0" + pwm;
        }
        else{
            GOD = "AP" + Pin + "H" + pwm;
        }



        //postUrl = "https://us-central1-myapplication3-da8f2.cloudfunctions.net/app/transmit";
        postBody = "{\n" +
                "    \"api\": \"" + API + "\",\n" +
                "    \"gpioValue\": \"" + GOD + "\"\n" +
                "}";

        JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            postRequest(postUrl,postBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void analogWrite(int pin,String pwm) {

        String Pin;

        if (pin < 10){

            Pin = "0" + Integer.toString(pin);
        }
        else{
            Pin =  Integer.toString(pin);
        }

        int l = pwm.length();
        if (l == 2){
            pwm = "0" + pwm;
        }
        else if (l ==1){
            pwm = "00" + pwm;
        }
        String GOD = "AP" + Pin + "H" + pwm;


        //postUrl = "https://us-central1-myapplication3-da8f2.cloudfunctions.net/app/transmit";
        postBody = "{\n" +
                "    \"api\": \"" + API + "\",\n" +
                "    \"gpioValue\": \"" + GOD + "\"\n" +
                "}";

        JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            postRequest(postUrl,postBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void postRequest(String postUrl,String postBody) throws IOException {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, postBody);

        final Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}
