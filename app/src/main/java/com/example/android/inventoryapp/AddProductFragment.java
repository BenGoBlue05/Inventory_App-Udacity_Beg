package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class AddProductFragment extends Fragment {

    static final int REQUEST_IMAGE_GET = 1;
    static final int EMPTY_NUMBER = -1;
    private static final String LOG_TAG = AddProductFragment.class.getSimpleName();
    TextView placeHolderTextView;
    ImageView imageView;
    EditText nameEditText;
    EditText supplierEditText;
    EditText priceEditText;
    EditText quantityEditText;
    Button addProductButton;
    View.OnClickListener mImageOnClickListener;
    SQLiteDatabase mDb;
    private String mUriStr;


    public AddProductFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);

        placeHolderTextView = (TextView) rootView.findViewById(R.id.add_product_placeholder_textview);
        imageView = (ImageView) rootView.findViewById(R.id.add_product_image_view);
        nameEditText = (EditText) rootView.findViewById(R.id.product_name_edit_text);
        supplierEditText = (EditText) rootView.findViewById(R.id.supplier_edit_text);
        priceEditText = (EditText) rootView.findViewById(R.id.price_edit_text);
        quantityEditText = (EditText) rootView.findViewById(R.id.quantity_edit_text);
        addProductButton = (Button) rootView.findViewById(R.id.add_product_button);

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidInput()){
                    return;
                }
                new ProductLoader(getActivity()).forceLoad();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        mImageOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
        };

        placeHolderTextView.setOnClickListener(mImageOnClickListener);
        imageView.setOnClickListener(mImageOnClickListener);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            Uri photoUri = data.getData();
            if (photoUri != null) {
                mUriStr = photoUri.toString();
            }
            Log.i(LOG_TAG, "URI: " + photoUri);
            placeHolderTextView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(Utils.getBitmap(getContext(), photoUri));
        }
    }

    private String getName() {
        return nameEditText.getText().toString();
    }

    private String getSupplier() {
        return supplierEditText.getText().toString();
    }

    private int getQuantity() {
        String quantityStr = quantityEditText.getText().toString();
        if (TextUtils.isEmpty(quantityStr)){
            return EMPTY_NUMBER;
        }
        return Integer.parseInt(quantityStr);
    }

    private double getPrice() {
        String priceStr = priceEditText.getText().toString();
        if (TextUtils.isEmpty(priceStr)){
            return EMPTY_NUMBER;
        }
        return Double.parseDouble(priceStr);
    }

    private String getUriStr() {
        return mUriStr;
    }

    public ContentValues createContentValues(
            String name, String supplier, int quantity, double price, String imageUriStr) {
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_NAME, name);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER, supplier);
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quantity);
        values.put(ProductContract.ProductEntry.COLUMN_PRICE, price);
        values.put(ProductContract.ProductEntry.COLUMN_IMAGE_URI, imageUriStr);
        return values;
    }

    private boolean isValidInput() {
        if (TextUtils.isEmpty(getUriStr())) {
            makeToast(getString(R.string.image));
            return false;
        }
        if (TextUtils.isEmpty(getName())) {
            makeToast(getString(R.string.product_name));
            return false;
        }
        if (TextUtils.isEmpty(getSupplier())) {
            makeToast(getString(R.string.supplier));
            return false;
        }
        if (getPrice() == EMPTY_NUMBER) {
            makeToast(getString(R.string.price));
            return false;
        }
        if (getQuantity() == EMPTY_NUMBER) {
            makeToast(getString(R.string.quantity));
            return false;
        }
        return true;
    }

    private void makeToast(String fieldName){
        Toast.makeText(getContext(),
                getString(R.string.please_enter) + " "  + fieldName,
                Toast.LENGTH_LONG).show();
    }

    private class ProductLoader extends AsyncTaskLoader<Void> {

        public ProductLoader(Context context) {
            super(context);
        }

        @Override
        public Void loadInBackground() {
            ProductDbHelper mHelper = new ProductDbHelper(getContext());
            mDb = mHelper.getWritableDatabase();
            ContentValues values = createContentValues(
                    getName(), getSupplier(), getQuantity(), getPrice(), getUriStr());
            long id = mDb.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
            Log.i(LOG_TAG, "insert row successful; row _id = " + id);
            if (id == -1) {
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
