package com.example.android.inventoryapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(args);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.product_detail_container, detailFragment)
                    .commit();
        }


    }
}
