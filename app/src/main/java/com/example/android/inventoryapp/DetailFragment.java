package com.example.android.inventoryapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            ProductContract.ProductEntry._ID,
            ProductContract.ProductEntry.COLUMN_NAME,
            ProductContract.ProductEntry.COLUMN_SUPPLIER,
            ProductContract.ProductEntry.COLUMN_QUANTITY,
            ProductContract.ProductEntry.COLUMN_PRICE
    };

    static final int COL_PRODUCT_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_SUPPLIER = 2;
    static final int COL_QUANTITY = 3;
    static final int COL_PRICE = 4;

    private TextView mNameTextView;
    private TextView mSupplierTextView;
    private TextView mQuantityTextView;
    private TextView mPriceTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null){
            mUri = args.getParcelable(DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mNameTextView = (TextView) rootView.findViewById(R.id.detail_name_textview);
        mSupplierTextView = (TextView) rootView.findViewById(R.id.detail_supplier_textview);
        mQuantityTextView = (TextView) rootView.findViewById(R.id.detail_quantity_textview);
        mPriceTextView = (TextView) rootView.findViewById(R.id.detail_price_textview);

        Button plusButton = (Button) rootView.findViewById(R.id.add_one_button);
        Button minusButton = (Button) rootView.findViewById(R.id.subtract_one_button);
        Button deleteButton = (Button) rootView.findViewById(R.id.delete_button);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "PLUS BUTTON CLICKED");
                onQuantityChanged(1);

            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onQuantityChanged(-1);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onQuantityChanged(int change){
        Uri uri = mUri;
        if (uri != null){
            int quantity = ProductContract.ProductEntry.getQuantityFromUri(uri) + change;
            if (quantity < 0){
                return;
            }
            long id = ProductContract.ProductEntry.getIdFromUri(uri);
            mUri = ProductContract.ProductEntry.buildProductWithQuantityUri(id, quantity);
            Log.i(LOG_TAG, mUri.toString());
            getContext().getContentResolver().update(mUri, null, null, null);
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri){
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()){
            String name = data.getString(COL_NAME);
            String supplier = data.getString(COL_SUPPLIER);
            String quantity = "" + data.getInt(COL_QUANTITY);
            String price = "" + data.getDouble(COL_PRICE);

            mNameTextView.setText(name);
            mSupplierTextView.setText(supplier);
            mQuantityTextView.setText(quantity);
            mPriceTextView.setText(price);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
