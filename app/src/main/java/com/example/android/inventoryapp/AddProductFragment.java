package com.example.android.inventoryapp;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class AddProductFragment extends Fragment {

    private static final String LOG_TAG = AddProductFragment.class.getSimpleName();

    EditText nameEditText;
    EditText supplierEditText;
    EditText priceEditText;
    EditText quantityEditText;
    Button addProductButton;

    SQLiteDatabase mDb;

    public AddProductFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);

        nameEditText = (EditText) rootView.findViewById(R.id.product_name_edit_text);
        supplierEditText = (EditText) rootView.findViewById(R.id.supplier_edit_text);
        priceEditText = (EditText) rootView.findViewById(R.id.price_edit_text);
        quantityEditText = (EditText) rootView.findViewById(R.id.quantity_edit_text);
        addProductButton = (Button) rootView.findViewById(R.id.add_product_button);

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProductLoader(getActivity()).forceLoad();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }

        });


        return rootView;
    }

    public String getName() {
        return nameEditText.getText().toString();
    }

    public String getSupplier() {
        return supplierEditText.getText().toString();
    }

    public int getQuantity() {
        return Integer.parseInt(quantityEditText.getText().toString());
    }

    public double getPrice() {
        return Double.parseDouble(priceEditText.getText().toString());
    }

    public ContentValues createContentValues(String name, String supplier, int quantity, double price) {
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_NAME, name);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER, supplier);
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quantity);
        values.put(ProductContract.ProductEntry.COLUMN_PRICE, price);
        return values;
    }

    private class ProductLoader extends AsyncTaskLoader<Void>{

        public ProductLoader(Context context) {
            super(context);
        }

        @Override
        public Void loadInBackground() {
            ProductDbHelper mHelper = new ProductDbHelper(getContext());
            mDb = mHelper.getWritableDatabase();
            ContentValues values =
                    createContentValues(getName(), getSupplier(), getQuantity(), getPrice());
            long id = mDb.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
            Log.i(LOG_TAG, "insert row successful; row _id = " + id);
            if (id == -1){
                Log.i(LOG_TAG, "insert failed");
            }
            return null;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }


}
