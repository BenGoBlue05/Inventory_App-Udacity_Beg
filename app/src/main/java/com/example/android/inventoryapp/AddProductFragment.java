package com.example.android.inventoryapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddProductFragment extends Fragment {

    private static final String LOG_TAG = AddProductFragment.class.getSimpleName();

    public AddProductFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);
        return rootView;
    }
}
