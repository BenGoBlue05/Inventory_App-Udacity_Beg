package com.example.android.inventoryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class DetailActivity extends AppCompatActivity
        implements DetailFragment.DeleteDialogFragment.DeleteDialogListener {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(args);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.product_detail_container, detailFragment)
                    .commit();
        }
    }

    @Override
    public void onDialogPositiveClick(DetailFragment.DeleteDialogFragment dialogFragment) {
        Uri uri = dialogFragment.getArguments().getParcelable(DetailFragment.DELETE_DIALOG_KEY);
        Log.i(LOG_TAG, "Uri: " + uri);
        if (uri != null) {
            getContentResolver().delete(uri, null, null);
        }
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
