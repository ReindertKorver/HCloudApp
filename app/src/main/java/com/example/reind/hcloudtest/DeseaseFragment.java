package com.example.reind.hcloudtest;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by reind on 24-4-2018.
 */

public class DeseaseFragment extends Fragment{
    private static final String TAG = "Tab1Fragment";

    private TextView textView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_desease,container,false);

        return view;
    }
}
