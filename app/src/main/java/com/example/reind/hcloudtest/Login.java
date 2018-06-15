package com.example.reind.hcloudtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Login extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    ProgressDialog pd;

    public static List<User> ListUser = null;
    User LoggedinUser = null;
    String EmailText;
    String PasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView tw = (TextView) findViewById(R.id.textView);

    }
    protected void onStart() {
        super.onStart();
        TextView tw = (TextView) findViewById(R.id.textView);
        ProgressBar LoadingApi = (ProgressBar) findViewById(R.id.LoadingAPi);
        LoadingApi.setVisibility(ProgressBar.VISIBLE);
        ToggleVisibillityEveryThingExceptLoading();
        try {
            new JSONTask().execute(BaseAPI.ApiServer+"/api/v2/"+BaseAPI.ApiDBName+"/_table/user?api_key="+BaseAPI.ApiKey+"").get();

        } catch (Exception ex) {
            tw.setText(ex.getMessage());
        }
        LoadingApi.setVisibility(ProgressBar.INVISIBLE);
        ToggleVisibillityEveryThingExceptLoading();
    }
    void ToggleVisibillityEveryThingExceptLoading() {

        EditText Pass = (EditText) findViewById(R.id.passwordtext);
        TextView tw = (TextView) findViewById(R.id.textView);
        if (Pass.getVisibility() == View.VISIBLE) {
            Pass.setVisibility(View.INVISIBLE);
            tw.setVisibility(View.INVISIBLE);
        } else {
            Pass.setVisibility(View.VISIBLE);
            tw.setVisibility(View.VISIBLE);
        }
    }

    protected void SignIn(View v) {
        final EditText Email = (EditText) findViewById(R.id.emailText);
        EditText Pass = (EditText) findViewById(R.id.passwordtext);
        TextView tw = (TextView) findViewById(R.id.textView);
        if ((Email.getText().equals("") || Email.getText() == null) && (Pass.getText().equals("") || Pass.getText() == null)) {
            tw.setText("Voer eerst alle vereiste gegevens in!");
        } else {
            EmailText = Email.getText().toString();


            byte EmailHash[] = null;

            try {
                MessageDigest md = null;
                byte[] bytesOfMessage = EmailText.toLowerCase().toString().getBytes("UTF-8");
                md = MessageDigest.getInstance("MD5");
                byte[] thedigest = md.digest(bytesOfMessage);
                EmailHash = thedigest;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String hashedEmail = Security.Hash(Email.getText().toString()).replaceAll("^\\s+", "");
            String UniqueID = UUID.nameUUIDFromBytes(EmailHash).toString();
            PasswordText = UniqueID + Security.Hash(Pass.getText().toString()).replaceAll("^\\s+", "");
            ;
            String t = PasswordText;
            try {
                //de gebruikergegevens worden opgehaald via de url die een sjon string terug geeft met alle gebruikers binnen het systeem,
                // het is beveilig met een apikey dus niet iedereen kan zomaar de gegevens ophalen
                ProgressBar LoadingApi = (ProgressBar) findViewById(R.id.LoadingAPi);
                LoadingApi.setVisibility(ProgressBar.VISIBLE);


                if (ListUser != null) {
                    LoggedinUser = ListUser.stream().filter((User) -> User.getEmailAdress().toLowerCase().equals(EmailText.toLowerCase())).findFirst().get();
                    if (LoggedinUser != null) {
                        // Create SharedPreferences object.
                        try {
                            Context ctx = getApplicationContext();

                            Gson gson = new Gson();
                            //hier worden de gebruikergegevens opgeslagen in de userpreferences als json formaat en ook gelijk gecontroleerd
                            // wanneer ze niet opgeslagen kunnen worden kan de gebruiker niet inloggen.
                            SharedPreferences sharedPref = getSharedPreferences("User_pref", MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("USER", gson.toJson(LoggedinUser));
                            editor.commit();
                            SharedPreferences sharedPref1 = getSharedPreferences(
                                    "User_pref", MODE_PRIVATE);
                            String size = sharedPref1.getString("USER", "null");
                            if (size != "null") {
                                //hier zijn alle gegevens opgeslagen en kan de gebruiker naar het volgende scherm gaan waar de controle opnieuw zal worden uitgevoerd
                                Intent myIntent = new Intent(Login.this, MainActivity.class);
                                Login.this.startActivity(myIntent);
                            } else {
                                //als de sjon null is kunnen de gebruiker gegevens niet worden opgehaald en zal de gebruiker moeten inloggen
                                Toast.makeText(this, "Kan gebruikergegevens niet opslaan", Toast.LENGTH_SHORT).show();

                            }
                        } catch (Exception ex) {

                            Toast.makeText(this, "Kan gebruikergegevens niet opslaan", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        tw.setText("Geen gebruiker gevonden met deze combinatie van gebruiker en wachtwoord");
                    }
                } else {
                    tw.setText("Database verbinding fout: ListUser: null, probeer opnieuw");
                }
            } catch (Exception e) {
                tw.setText(e.getMessage());
            }
            ProgressBar LoadingApi = (ProgressBar) findViewById(R.id.LoadingAPi);
            LoadingApi.setVisibility(ProgressBar.INVISIBLE);

        }
    }
    // } else {

    // Toast.makeText(Login.this, "Deze combinatie is niet goed", Toast.LENGTH_LONG).show();
    //}

    //hieronder worden de gegevens van uit een api opgehaald doormiddel van een achtergrond proces
    //de api
    class JSONTask extends AsyncTask<String, String, String> {
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
            TextView tw = (TextView) findViewById(R.id.textView);
            //tw.setText(result);
            try {
                EditText Email = (EditText) findViewById(R.id.emailText);
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
                tw.setText(e.getMessage());
            }
        }
    }

    //Nog naar kijken niet zeker of dit nog nodig is!
    class UserData {
        private List<User> users;

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> products) {
            this.users = users;
        }
    }
}






