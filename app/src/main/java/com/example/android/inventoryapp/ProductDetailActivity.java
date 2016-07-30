package com.example.android.inventoryapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class ProductDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = ProductDetailActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Log.i(LOG_TAG, "ProductDetail Activity started");
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.product_detail_container, new ProductDetailFragment())
                    .commit();
        }


    }
}
