package com.example.reind.hcloudtest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    boolean firstStart = true;
    ListView ClientListview;
    List<String> stringListUsers;
    ArrayAdapter<String> ListviewUsers;
    ArrayAdapter<String> ListviewTherapies;
    List<String> stringListTherapies;
    EditText tv;
    ProgressDialog progressNewMeasure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isOnline()) {

            try {
                SharedPreferences sharedPref1 = getSharedPreferences(
                        "User_pref", MODE_PRIVATE);
                String user = sharedPref1.getString("USER", "null");
                if (user == "null") {
                    GoToLogin();
                } else {
                    Gson gson = new Gson();
                    User LoggedInUser = gson.fromJson(user, User.class);
                    //PROBEER DIT NOG OM TE BOUWEN NAAR EEN CONTROLE VOOR DE RECHTEN op dit moment is er alleen controle op de rol (alleen zuster en admin mogen in de app)
                    if (LoggedInUser != null) {
                        if (LoggedInUser.getEmailAdress().equals("") || LoggedInUser.getPasswordHash().equals("") || LoggedInUser.getBsnNumber().equals("") || RoleControle(LoggedInUser.getRoleID())) {
                            GoToLogin();
                            firstStart = false;
                        } else {
                            if (!firstStart) {
                                Toast.makeText(MainActivity.this, "Welkom " + LoggedInUser.getFirstName() + LoggedInUser.getLastName(), Toast.LENGTH_SHORT).show();
                            }
                            TextView LoggedinAs = (TextView) findViewById(R.id.loggedInAs);
                            LoggedinAs.setText("" + LoggedInUser.getFirstName() + " " + LoggedInUser.getLastName());
                            mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

                            //set up the viewpager with the sections adapter
                            mViewPager = (ViewPager) findViewById(R.id.container);
                            setupViewPager(mViewPager);

                            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                            tabLayout.setupWithViewPager(mViewPager);

                            mViewPager.setOffscreenPageLimit(3);

                        }
                    } else {
                        GoToLogin();
                    }

                }
            } catch (Exception ex) {
                Toast.makeText(this, "Er is iets fout gegaan", Toast.LENGTH_SHORT).show();
                GoToLogin();
            }
            EditText inputSearch = findViewById(R.id.SearchClient);
            inputSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    if (ListviewUsers != null) {
                        MainActivity.this.ListviewUsers.getFilter().filter(cs);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });
        } else {
            // de applicatie heeft geen internet connectie
            showNoInternetConnection();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //_____________________Commented OLD code______________________________
//        TabLayout tab = findViewById(R.id.tabs);
//        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getText().toString()){
//                    case "Aandoeningen":
//                        break;
//                    case "Medicaties":
//                        break;
//                    case "Behandelingen":
//                    {
//                        tv = findViewById(R.id.apithherapies);
//                        //wanneer de behandelingen zijn opgehaald zal de json string in een edittext geplaats worden zodat deze met een onchange even de loader kan uizetten
//                        // en dan de gegevens inladen dit is om zo te voorkomen dat de api het resultaat nog niet heeft terug gegeven
//                        tv.addTextChangedListener(new TextWatcher() {
//
//                            public void afterTextChanged(Editable s) {
//                                ListView TherapiesList = findViewById(R.id.TherapiesListView);
//
//                                if (!tv.getText().toString().equals("")) {
//
//                                    AddItemsToTherapyView(tv.getText().toString(), TherapiesList);
//
//                                }
//                            }
//
//                            public void beforeTextChanged(CharSequence s, int start,
//                                                          int count, int after) {
//                            }
//
//                            public void onTextChanged(CharSequence s, int start,
//                                                      int before, int count) {
//
//                            }
//                        });
//                    }
//                        break;
//
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
        tv = findViewById(R.id.apithherapies);
        //wanneer de behandelingen zijn opgehaald zal de json string in een edittext geplaats worden zodat deze met een onchange even de loader kan uizetten
        // en dan de gegevens inladen dit is om zo te voorkomen dat de api het resultaat nog niet heeft terug gegeven
        tv.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                ListView TherapiesList = findViewById(R.id.TherapiesListView);

                if (!tv.getText().toString().equals("")) {
                    ProgressBar pg = findViewById(R.id.TherapiesListviewLoader);
                    if (pg != null) {
                        pg.setVisibility(View.INVISIBLE);
                    }
                    AddItemsToTherapyView(tv.getText().toString(), TherapiesList);

                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                //Not needed
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //Not needed
            }
        });

    }

    /**
     * Checks the internet connection
     *
     * @return returns a bool that indecates if there is a internet connection
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void AddItemsToTherapyView(String JsonTherapies, ListView listView) {

        try {
            List<Therapies> therapies = BaseAPI.ParseListTherapies(JsonTherapies);
            if (therapies != null) {
                stringListTherapies = new ArrayList<>();
                for (final Therapies therapie : therapies) {
                    stringListTherapies.add(therapie.getDate() + " \n " + therapie.getDescription() + "\n" + therapie.getLocation());
                }
                ListviewTherapies = new ArrayAdapter<String>
                        (this, android.R.layout.simple_list_item_1, stringListTherapies);
                listView.setAdapter(ListviewTherapies);

            }
        } catch (Exception ex) {
            Toast.makeText(this, "Er is iets fout gegaan bij het ophalen van de behandelingen", Toast.LENGTH_SHORT).show();
        }
    }

    int CREATE_REQUEST_CODE = 4;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Use Data to get string
                String string = data.getStringExtra("RESULT_STRING");
                TextView UserTxt = findViewById(R.id.SelectedClientText);
                UserTxt.setText(string);
                String result = string.toString().substring(0, string.toString().indexOf(" "));
                getTherapies(result);
            }
        }
    }

    // zorgt er voor dat wanneer de app geopend wordt en de gebruiker nog niet is ingelogd het inlogscherm getoond wordt
    void GoToLogin() {
        Toast.makeText(this, "Er moet ingelogd worden", Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(MainActivity.this, Login.class);
        MainActivity.this.startActivity(myIntent);
    }

    //controleerd de rol van de gebruiker niet alle gebruikers mogen ingelod worden alleen met bepaalde rechten dit is nog niet goed uitgevoerd
    boolean RoleControle(int id) {
        if (id == 7) {
            return false;
        }
        if (id == 9 && id != 7) {
            return false;
        } else {
            return true;
        }

    }

    //zorgt voor dynamisch afbeelden van de tabs in de mainactivity
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new DeseaseFragment(), "Aandoeningen");
        adapter.addFragment(new MedicationFragment(), "Medicaties");
        adapter.addFragment(new TherapyFragment(), "Behandelingen");
        viewPager.setAdapter(adapter);
    }

    //wanneer er op de knop clientselection wordt geklikt zal de card getoond worden zodat een client kan worden geselecteerd
    protected void ClientSelectionClick(View view) {
        CardView ClientCard = (CardView) findViewById(R.id.ClientCard);
        CardView ClientCard1 = (CardView) findViewById(R.id.CardNewMeasure);
        if (ClientCard.getVisibility() == CardView.VISIBLE) {
            ClientCard.setVisibility(CardView.INVISIBLE);
        } else {
            ClientCard.setVisibility(CardView.VISIBLE);
            ClientCard1.setVisibility(CardView.INVISIBLE);
        }

    }

    protected void NewMeasureClick(View view) {
        CardView ClientCard = (CardView) findViewById(R.id.CardNewMeasure);
        CardView ClientCard1 = (CardView) findViewById(R.id.ClientCard);
        if (ClientCard.getVisibility() == CardView.VISIBLE) {
            ClientCard.setVisibility(CardView.INVISIBLE);
        } else {
            ClientCard.setVisibility(CardView.VISIBLE);
            ClientCard1.setVisibility(CardView.INVISIBLE);
        }
    }

    public User GetLoggedInUser() {
        User LoggedInUser = new User();
        SharedPreferences sharedPref1 = getSharedPreferences(
                "User_pref", MODE_PRIVATE);
        String user = sharedPref1.getString("USER", "null");
        if (user != "" || user != null) {
            Gson gson = new Gson();
            LoggedInUser = gson.fromJson(user, User.class);
        }
        return LoggedInUser;
    }

    /**
     * Saves the measure by getting the temperature text bloodpresstop text and the bloodpressdown text and then compressing it to a json string, so it can be used for a API request
     *
     * @param view
     */
    protected void SaveMeasureClick(View view) {
        try {

            TextView SelectedClient = findViewById(R.id.SelectedClientText);
            if (SelectedClient.getText().toString().isEmpty() || SelectedClient.getText().toString().equals("Geen cliënt geselecteerd")) {
                Toast.makeText(this, "Selecteer eerst een cliënt", Toast.LENGTH_SHORT).show();
            } else {

                EditText Temperature = findViewById(R.id.TemperatureCelsius);
                EditText BloodPressTop = findViewById(R.id.bloodPressHigh);
                EditText BloodPressDown = findViewById(R.id.BloodPressLow);
                if (Temperature.getText().toString().equals("") || Temperature.getText() == null || BloodPressDown.getText().toString().equals("") || BloodPressDown.getText() == null || BloodPressTop.getText().toString().equals("") || BloodPressTop.getText() == null) {
                    Toast.makeText(MainActivity.this, "Eén of meer van de vereiste gegevens zijn niet ingevuld", Toast.LENGTH_SHORT).show();
                } else {
                    progressNewMeasure = new ProgressDialog(this);
                    progressNewMeasure.setTitle("Meting opslaan");
                    progressNewMeasure.setMessage("De meting wordt opgeslagen");
                    progressNewMeasure.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progressNewMeasure.show();
                    String thing = SelectedClient.getText().toString();
                    String result1 = thing.toString().substring(0, thing.toString().indexOf(" "));
                    User user = GetLoggedInUser();
                    Integer x = Integer.valueOf(result1);
                    String Json = BaseAPI.CareControlNewMeasureJSONString(x, user.getID(), Double.parseDouble(Temperature.getText().toString()), BloodPressTop.getText().toString() + "/" + BloodPressDown.getText().toString());
                    String url = BaseAPI.ApiServer + "/api/v2/" + BaseAPI.ApiDBName + "/_table/carecontrol?api_key=" + BaseAPI.ApiKey;
                    String result = PostToAPI(url, Json);

                }
            }
        } catch (Exception ex) {
            showMessage("Er is een fout opgetreden: " + ex.getMessage() + "\nProbeer opnieuw", "Fout");
        }
    }

    //posts the measures to the api
    public String PostToAPI(String URL, String JSONPOST) {
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = null;
        try {
            jsonParams = new JSONObject(JSONPOST);
            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(null, URL, entity, "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    TextView tv = findViewById(R.id.SaveMeasureResult);
                    showMessage("Succesvol opgeslagen", "Opgeslagen");
                    tv.setText("Succesvol opgeslagen");

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    TextView tv = findViewById(R.id.SaveMeasureResult);

                    if(responseBody!=null)
                    {
                    showMessage("Er is een fout opgetreden: \n" + new String(responseBody), "Fout");
                    tv.setText("Er is een fout opgetreden \n" + new String(responseBody));
                    }
                    else{
                        showMessage("Fout bij weg schrijven prbeer opnieuw", "Fout");
                        tv.setText("Fout bij weg schrijven prbeer opnieuw");
                    }

                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    //wanneer deze methode wordt aan geroepen zal de gebruiker worden uitgelogd en de opgeslagen gebruiker worden verwijderd,
    // zodat een nieuwe kan inloggen
    protected void LogOutClick(View view) {
        SharedPreferences sharedPref = getSharedPreferences("User_pref", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("USER", "null");
        editor.commit();
        GoToLogin();
    }

    //dze methode open de scanner zodat er een bsnnummer gescanned kan worden
    public void openScannerClick(View v) {
        Toast.makeText(this, "Scanner wordt geopend", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(this, scanner.class), CREATE_REQUEST_CODE);
    }

    //TODO eventueel verbeteren zodat het resultaat wordt afgewacht en de clientlist gelijk gevuld wordt. want nu wordt eerst de json opgehaald
    // TODO en terwijl hij dat ophaalt probeert hij het aan de list toe tevoegen maar dan is het resultaat nog niet opgehaald
    //werkt nog niet goed genoeg maar haalt de clienten op en plaatst ze in de listview zodat ze geselecteerd kunnen worden
    //ook is er een onclick listener die er voor zorgt dat wanneer een gebruiker is geselecteerd bij de bsn nummer de behandelingen, medicaties en aandoenigne  worden opgehaald
    public void RefreshClients(View v) {
        try {
            getUsers();
            if (ListviewUsers != null) {
                ClientListview = findViewById(R.id.ClientListView);
Log.d("HCLOUD","Set adapter clientlistview\n"+ListviewUsers);
                ClientListview.setAdapter(ListviewUsers);
                //zet de kleur van de geselecteerde item
                ClientListview.setOnItemClickListener((arg0, view, position, id) -> {
                    Object thing = ClientListview.getItemAtPosition(position);
                    EditText Search = findViewById(R.id.SearchClient);
                    Search.setText(thing.toString());
                    TextView ClientText = findViewById(R.id.SelectedClientText);
                    ClientText.setText(thing.toString());
                    String result = thing.toString().substring(0, thing.toString().indexOf(" "));

                    getTherapies(result);
                    ClientSelectionClick(null);
                });
            }
            else{
                Toast.makeText(MainActivity.this,"Refresh opnieuw!",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            showMessage(ex.getMessage(), "Fout: ");
        }
    }

    public void getTherapies(String bsn) {
        try {
            Log.d("HCLOUD LOG", "Trying to connect to api");
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            String url = BaseAPI.GetAPIUrl("therapies", "BsnNumber", bsn);
            asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (responseBody != null) {
                        tv.setText(new String(responseBody));
                        String tx = new String(responseBody);
                        Log.d("HCLOUD LOG", tx);

                    } else {
                        Log.d("HCLOUD LOG", "response is null");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(responseBody!=null)
                    {
                        showMessage("Er is een fout opgetreden: \n" + new String(responseBody), "Fout");
                    }
                    else{
                        showMessage("Kan geen behandelingen ophalen bij deze client", "Fout");
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Fout " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void showNoInternetConnection() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Internet Connection Error");
        builder.setMessage("Deze applicatie heeft een internet verbinding nodig, op dit moment heeft deze applicatie geen verbinding, de applicatie zal worden afgesloten.");

        // add a button
        builder.setPositiveButton("Afsluiten", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void showMessage(String text, String title) {
if(progressNewMeasure!=null&&progressNewMeasure.isShowing()){
    progressNewMeasure.dismiss();
}
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(text);

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void getUsers() {
        try {
            Log.d("HCLOUD LOG", "Trying to connect to api");
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            String url = BaseAPI.ApiServer + "/api/v2/" + BaseAPI.ApiDBName + "/_table/user?api_key=" + BaseAPI.ApiKey + "";
            asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (responseBody != null) {
                        String tx = new String(responseBody);
                        Log.d("HCLOUD LOG", tx);
                        try {
                            ProcessUsers(tx);
                        } catch (Exception ex) {
                            Toast.makeText(MainActivity.this, "Er is iets fout gegaan bij het ophalen van de gebruikers", Toast.LENGTH_SHORT).show();
                        }
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
    }

    public void ProcessUsers(String tx) {
        try {
            JSONObject Jasonobject = new JSONObject();
            JSONObject object = new JSONObject(tx);
            JSONArray Jarray = object.getJSONArray("resource");
            List<User> Users = new ArrayList<>();
            for (int i = 0; i < Jarray.length(); i++) {
                Jasonobject = Jarray.getJSONObject(i);
                Gson gson = new Gson();
                User user = gson.fromJson(Jasonobject.toString(), User.class);
                Users.add(user);
            }

            if (Users != null) {
                stringListUsers = new ArrayList<>();
                for (final User user : Users) {
                    //alle gebruikers zijn tegelijkertijd ook patienten dus komen ze allemaal in het object
                    stringListUsers.add(user.getBsnNumber() + " | " + user.getFirstName() + user.getLastName());
                }
                ListviewUsers = new ArrayAdapter<String>
                        (MainActivity.this, android.R.layout.simple_list_item_1, stringListUsers);

            } else {
                Log.d("HCLOUD: ", "users is null");
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Fout " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }
}
