package com.example.android.inventoryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_container, new MainFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //temp menu item for starting detail activity
        if (id == R.id.action_add_product_activity){
            startActivity(new Intent(this, AddProductActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri productUri) {
        Log.i(LOG_TAG, productUri.toString());
        startActivity(new Intent(this, DetailActivity.class).setData(productUri));
    }
}
