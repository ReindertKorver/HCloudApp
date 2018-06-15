package com.example.reind.hcloudtest;

import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by reind on 2-6-2018.
 */

class test {
    private static final test ourInstance = new test();

    static test getInstance() {
        return ourInstance;
    }

    private test() {
        try {

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.post(/*The API URL*/"", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (responseBody != null) {
                        //Response string with JSON
                    } else {
                        //Response string with JSON is NULL
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    //Something went wrong
                }
            });

        } catch (Exception e) {
            //Something went wrong
        }
    }
}
