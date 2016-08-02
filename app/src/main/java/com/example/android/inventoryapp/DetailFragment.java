package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DELETE_DIALOG_KEY = "deleteDialogFragmentKey";
    public static final String ORDER_DIALOG_KEY = "orderDialogKey";

    public static final String ORDER_QUANTITY_KEY = "orderQuantity";
    public static final String ORDER_EMAIL_KEY = "orderEmail";
    public static final String ORDER_NAME_KEY = "orderName";

    static final String DETAIL_URI = "URI";
    static final int COL_PRODUCT_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_SUPPLIER = 2;
    static final int COL_QUANTITY = 3;
    static final int COL_PRICE = 4;
    static final int COL_IMAGE_URI = 5;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    private static final String DELETE_DIALOG_FRAGMENT_TAG = "dialogFragment";
    private static final String ORDER_DIALOG_FRAGMENT_TAG = "orderFragment";
    private static final String[] DETAIL_COLUMNS = {
            ProductContract.ProductEntry._ID,
            ProductContract.ProductEntry.COLUMN_NAME,
            ProductContract.ProductEntry.COLUMN_SUPPLIER,
            ProductContract.ProductEntry.COLUMN_QUANTITY,
            ProductContract.ProductEntry.COLUMN_PRICE,
            ProductContract.ProductEntry.COLUMN_IMAGE_URI
    };

    private Uri mUri;
    private TextView mNameTextView;
    private TextView mSupplierTextView;
    private TextView mQuantityTextView;
    private TextView mPriceTextView;
    private ImageView mImageView;

    private String mProductName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mNameTextView = (TextView) rootView.findViewById(R.id.detail_name_textview);
        mSupplierTextView = (TextView) rootView.findViewById(R.id.detail_supplier_textview);
        mQuantityTextView = (TextView) rootView.findViewById(R.id.detail_quantity_textview);
        mPriceTextView = (TextView) rootView.findViewById(R.id.detail_price_textview);
        mImageView = (ImageView) rootView.findViewById(R.id.detail_imageview);

        Button plusButton = (Button) rootView.findViewById(R.id.add_one_button);
        Button minusButton = (Button) rootView.findViewById(R.id.subtract_one_button);
        Button deleteButton = (Button) rootView.findViewById(R.id.delete_button);
        Button orderButton = (Button) rootView.findViewById(R.id.order_button);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                dialogFragment.show(getFragmentManager(), DELETE_DIALOG_FRAGMENT_TAG);
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment orderDialogFragment = new OrderDialogFragment();
                Bundle args = new Bundle();
                args.putString(ORDER_NAME_KEY, mProductName);
                orderDialogFragment.setArguments(args);
                orderDialogFragment.show(getFragmentManager(), ORDER_DIALOG_FRAGMENT_TAG);

            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onQuantityChanged(int change) {
        Uri uri = mUri;
        if (uri != null) {
            int quantity = ProductContract.ProductEntry.getQuantityFromUri(uri) + change;
            if (quantity < 0) {
                return;
            }
            long id = ProductContract.ProductEntry.getIdFromUri(uri);
            mUri = ProductContract.ProductEntry.buildProductWithQuantityUri(id, quantity);
            getContext().getContentResolver().update(mUri, null, null, null);
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    public Bundle onItemDelete() {
        Bundle bundle = new Bundle();
        Uri uri;
        if (mUri != null) {
            long id = ProductContract.ProductEntry.getIdFromUri(mUri);
            uri = ProductContract.ProductEntry.buildProductUri(id);
            bundle.putParcelable(DELETE_DIALOG_KEY, uri);
            return bundle;
        }
        return null;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
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
        if (data != null && data.moveToFirst()) {
            mProductName = data.getString(COL_NAME);
            String supplier = data.getString(COL_SUPPLIER);
            String quantity = "" + data.getInt(COL_QUANTITY);
            double price = data.getDouble(COL_PRICE);
            Uri imageUri = Uri.parse(data.getString(COL_IMAGE_URI));

            mNameTextView.setText(mProductName);
            mSupplierTextView.setText(supplier);
            mQuantityTextView.setText(quantity);
            mPriceTextView.setText(Utils.formatDollar(price));
            mImageView.setImageBitmap(Utils.getBitmap(getContext(), imageUri));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static class DeleteDialogFragment extends DialogFragment {

        DeleteDialogListener mDeleteDialogListener;

        EditText mDialogQuantityEditText;
        EditText mEmailEditText;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.sure_delete)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDeleteDialogListener.onDialogPositiveClick(DeleteDialogFragment.this);
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

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            try {
                mDeleteDialogListener = (DeleteDialogListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() +
                        " must implement DeleteDialogListener");
            }
        }

        public interface DeleteDialogListener {
            void onDialogPositiveClick(DeleteDialogFragment dialogFragment);
        }
    }

    public static class OrderDialogFragment extends DialogFragment {

        EditText mQuantityEditText;
        EditText mEmailEditText;
        Button mCancelButton;
        Button mSendButton;

        OrderDialogListener mOrderListener;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.dialog_container_order, container, false);
            mQuantityEditText = (EditText) rootView.findViewById(R.id.dialog_quantity);
            mEmailEditText = (EditText) rootView.findViewById(R.id.dialog_supplier_email);
            mCancelButton = (Button) rootView.findViewById(R.id.cancel_button);
            mSendButton = (Button) rootView.findViewById(R.id.send_button);

            mCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().cancel();
                }
            });

            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOrderListener.onOrderClick(OrderDialogFragment.this);
                }
            });

            return rootView;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            try {
                mOrderListener = (OrderDialogListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(
                        context.toString() + " must implement OrderDialogListener");
            }
        }

        public String getEmail() {
            return mEmailEditText.getText().toString();
        }

        public String getQuantity() {
            return mQuantityEditText.getText().toString();
        }

        //From https://developer.android.com/guide/components/intents-common.html#Email
        public void composeEmail(String[] email, String quantity, String productName) {
            Intent intent = new Intent(Intent.ACTION_SENDTO)
                    .setData(Uri.parse("mailto:"))
                    .putExtra(Intent.EXTRA_EMAIL, email)
                    .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_order_subject))
                    .putExtra(Intent.EXTRA_TEXT,
                            String.format(getString(R.string.email_message), quantity, productName));
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), getContext().getString(R.string.email_app_not_found),
                        Toast.LENGTH_LONG).show();
            }
        }

        public interface OrderDialogListener {
            void onOrderClick(DialogFragment dialogFragment);
        }

    }
}
