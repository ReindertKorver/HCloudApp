package com.example.reind.hcloudtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class scanner extends AppCompatActivity {
    SurfaceView cameraPreview;
    TextView txtResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        cameraPreview = (SurfaceView) findViewById(R.id.Cameraview);
        txtResult = (TextView) findViewById(R.id.textView2);
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(height, width).setAutoFocusEnabled(true).build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //request permission
                    ActivityCompat.requestPermissions(scanner.this,
                            new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {

                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            String barcodeBefore = "";

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0) {

                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            //Create vibrate

                            if (!barcodeBefore.equals(qrcodes.valueAt(0).displayValue)) {
                                Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                assert vibrator != null;
                                vibrator.vibrate(1000);
                                txtResult.setText(qrcodes.valueAt(0).displayValue);

                                barcodeBefore = (qrcodes.valueAt(0).displayValue);

                            }
                        }
                    });


                }
            }
        });
    }

    List<String> StringListTherapies = new ArrayList<>();

    public void AcceptCodeClick(View v) {
        if (!txtResult.getText().equals("Resultaat:")) {

            try {

                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                String url = BaseAPI.GetAPIUrl("user", "BsnNumber", txtResult.getText().toString());
                Log.d("HCLOUD LOG", "Trying to connect from scanner to api url='" + url + "'");
                asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (responseBody != null) {
                            String tx = new String(responseBody);
                            List<User> Users = JsonToUser(tx);
                            String usertext = Users.get(0).getBsnNumber()+" | "+Users.get(0).getFirstName()+Users.get(0).getLastName();
                            GoBack(usertext);
                            Log.d("HCLOUD LOG", tx);
                        } else {
                            Log.d("HCLOUD LOG", "response is null");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("HCLOUD LOG", new String(responseBody));
                    }
                });

            } catch (Exception e) {
                Toast.makeText(this, "Fout " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Er is nog geen QRcode gescand", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Returns the user to the main activity with the specidfied user as parameter
     * @param User
     */
    void GoBack(String User){
        Intent intent=new Intent();
        intent.putExtra("RESULT_STRING", User);
        setResult(RESULT_OK, intent);
        finish();
}
    public List<User> JsonToUser(String JSon) {
        List<User> Users = new ArrayList<>();
        try {
            JSONObject Jasonobject = new JSONObject();
            JSONObject object = null;

            object = new JSONObject(JSon);

            JSONArray Jarray = object.getJSONArray("resource");

            for (int i = 0; i < Jarray.length(); i++) {
                Jasonobject = Jarray.getJSONObject(i);
                Gson gson = new Gson();
                User user = gson.fromJson(Jasonobject.toString(), User.class);
                Users.add(user);
            }

        } catch (JSONException e)

        {
            e.printStackTrace();
        }
        return Users;
    }

    public void BackToMainClick(View v) {
        Intent myIntent = new Intent(scanner.this, MainActivity.class);
        scanner.this.startActivity(myIntent);
    }

}
