package com.example.reind.hcloudtest;

import android.os.AsyncTask;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.binary.Base64;

import com.google.android.gms.ads.internal.gmsg.HttpClient;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * Created by reind on 18-5-2018.
 */

public class ApiConnection {



    //hieronder worden de gegevens van uit een api opgehaald doormiddel van een achtergrond proces
    //de api
    public static List<User> ListUser;

    public List<User> getUsers() {
        try {
            new JSONTaskGetUsers().execute(BaseAPI.ApiServer + "/api/v2/" + BaseAPI.ApiDBName + "/_table/user?api_key=" + BaseAPI.ApiKey + "").get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ListUser;
    }

    class JSONTaskGetUsers extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                //hierw wordt de connectie gemaakt met de api
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {


                    buffer.append(line);
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        //in de onpostexecute kom het resultaat van de httpurlrequest en wordt het opgeslagen in een User(class) object
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //tw.setText(result);
            try {

                JSONObject Jasonobject = new JSONObject();
                JSONObject object = new JSONObject(result);
                JSONArray Jarray = object.getJSONArray("resource");
                List<User> Users = new ArrayList<>();
                for (int i = 0; i < Jarray.length(); i++) {
                    Jasonobject = Jarray.getJSONObject(i);
                    Gson gson = new Gson();
                    User user = gson.fromJson(Jasonobject.toString(), User.class);
                    Users.add(user);
                }
                ListUser = Users;
            }
            //LoggedinUser = users.stream().filter(o -> o.getEmailAdress().contains(Email.getText())).findFirst().get();


            catch (Exception e) {

            }
        }
    }

    public static List<Therapies> TherapiesList;

    public List<Therapies> GetTherapies(String URL) {
        try {
            String str_result = new JSONTASKGETTherapies().execute(URL).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TherapiesList;
    }

    class JSONTASKGETTherapies extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                //hierw wordt de connectie gemaakt met de api
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {


                    buffer.append(line);
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        //in de onpostexecute kom het resultaat van de httpurlrequest en wordt het opgeslagen in een User(class) object
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //tw.setText(result);
            try {

                JSONObject Jasonobject = new JSONObject();
                JSONObject object = new JSONObject(result);
                JSONArray Jarray = object.getJSONArray("resource");
                List<Therapies> Therapies = new ArrayList<>();
                for (int i = 0; i < Jarray.length(); i++) {
                    Jasonobject = Jarray.getJSONObject(i);
                    Gson gson = new Gson();
                    Therapies therapies = gson.fromJson(Jasonobject.toString(), Therapies.class);
                    Therapies.add(therapies);
                }
                TherapiesList = Therapies;
            }
            //LoggedinUser = users.stream().filter(o -> o.getEmailAdress().contains(Email.getText())).findFirst().get();


            catch (Exception e) {
                e.getMessage();
            }
        }
    }
}
