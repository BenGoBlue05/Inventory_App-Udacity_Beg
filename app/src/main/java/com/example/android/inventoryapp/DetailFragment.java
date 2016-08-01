package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
    public static final String DELETE_DIALOG_KEY = "deleteDialogFragmentKey";

    private static final String DIALOG_FRAGMENT_TAG = "dialogFragment";

    private static final String[] DETAIL_COLUMNS = {
            ProductContract.ProductEntry._ID,
            ProductContract.ProductEntry.COLUMN_NAME,
            ProductContract.ProductEntry.COLUMN_SUPPLIER,
            ProductContract.ProductEntry.COLUMN_QUANTITY,
            ProductContract.ProductEntry.COLUMN_PRICE,
            ProductContract.ProductEntry.COLUMN_IMAGE_URI
    };

    static final int COL_PRODUCT_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_SUPPLIER = 2;
    static final int COL_QUANTITY = 3;
    static final int COL_PRICE = 4;
    static final int COL_IMAGE_URI = 5;

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
                DeleteDialogFragment dialogFragment = new DeleteDialogFragment();
                dialogFragment.setArguments(onItemDelete());
                dialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
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

    public Bundle onItemDelete(){
        Bundle bundle = new Bundle();
        Uri uri;
        if (mUri != null){
            long id = ProductContract.ProductEntry.getIdFromUri(mUri);
            uri = ProductContract.ProductEntry.buildProductUri(id);
            bundle.putParcelable(DELETE_DIALOG_KEY, uri);
            return bundle;
        }
        return null;
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

    public static class DeleteDialogFragment extends DialogFragment{

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.sure_delete)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mListener.onDialogPositiveClick(DeleteDialogFragment.this);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            return builder.create();
        }

        public interface DeleteDialogListener {
            void onDialogPositiveClick(DeleteDialogFragment dialogFragment);
        }

        DeleteDialogListener mListener;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            try{
                mListener = (DeleteDialogListener) context;
            }
            catch(ClassCastException e){
                throw new ClassCastException(context.toString() +
                        " must implement DeleteDialogListener");
            }
        }

    }
}
