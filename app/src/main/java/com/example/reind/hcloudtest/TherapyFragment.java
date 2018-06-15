package com.example.reind.hcloudtest;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reind on 24-4-2018.
 */

public class TherapyFragment extends Fragment{
    private static final String TAG = "TherapyFragment";
    ArrayAdapter<String> ListviewTherapies;
    List<String> stringListTherapies;
    private TextView textView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_therapy,container,false);


        return view;
    }
public void SetListview(String JsonTherapies, ListView listView){

}
    public void AddItemsToTherapyView(String JsonTherapies) {

        try {
            ListView Listview = (ListView) getView().findViewById(R.id.TherapiesListView);
            List<Therapies> therapies = BaseAPI.ParseListTherapies(JsonTherapies);
            if (therapies != null) {
                stringListTherapies = new ArrayList<>();
                for (final Therapies therapie : therapies) {
                    stringListTherapies.add(therapie.getDate() + " \n " + therapie.getDescription() + "\n" + therapie.getLocation());
                }
                ListviewTherapies = new ArrayAdapter<String>
                        (getActivity(), android.R.layout.simple_list_item_1, stringListTherapies);
                Listview.setAdapter(ListviewTherapies);

            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Er is iets fout gegaan bij het ophalen van de behandelingen", Toast.LENGTH_SHORT).show();
        }
    }

}
