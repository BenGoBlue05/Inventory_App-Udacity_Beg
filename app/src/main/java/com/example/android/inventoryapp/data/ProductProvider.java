package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by bplewis5 on 7/28/16.
 */
public class ProductProvider extends ContentProvider {

    private static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PRODUCT = 100;
    private static final int PRODUCT_WITH_ID = 101;
    private static final int PRODUCT_ID_AND_QUANTITY = 200;
    private static final String sProductIdSelection =
            ProductContract.ProductEntry._ID + " = ? ";

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCT, PRODUCT);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCT + "/#", PRODUCT_WITH_ID);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCT + "/#/#", PRODUCT_ID_AND_QUANTITY);
    }

    private ProductDbHelper mOpenHelper;

    private Cursor getProductById(Uri uri, String[] projection) {
        String id = "" + ProductContract.ProductEntry.getIdFromUri(uri);
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return db.query(
                ProductContract.ProductEntry.TABLE_NAME,
                projection,
                sProductIdSelection,
                new String[]{id},
                null,
                null,
                null
        );

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case PRODUCT:
                retCursor = db.query(
                        ProductContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PRODUCT_WITH_ID:
                retCursor = getProductById(uri, projection);
                break;
            case PRODUCT_ID_AND_QUANTITY:
                retCursor = getProductById(uri, projection);
                Log.i(LOG_TAG, "RECEIVED URI");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PRODUCT:
                return ProductContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT_WITH_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case PRODUCT:
                long _id = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = ProductContract.ProductEntry.buildProductUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case PRODUCT:
                rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case PRODUCT:
                rowsUpdated = db.update(
                        ProductContract.ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case PRODUCT_ID_AND_QUANTITY:
                rowsUpdated = updateQuantity(uri);
                Log.i(LOG_TAG, "URI RECOGNIZED");
                Log.i(LOG_TAG, "URI: " + uri.toString());
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int updateQuantity(Uri uri) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int quantity = ProductContract.ProductEntry.getQuantityFromUri(uri);
        long id = ProductContract.ProductEntry.getIdFromUri(uri);
        Log.i(LOG_TAG, "QUANTITY = " + quantity);
        if (quantity < 0) {
            quantity = 0;
        }
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quantity);
        return db.update(ProductContract.ProductEntry.TABLE_NAME, values,
                sProductIdSelection, new String[]{Long.toString(id)});
    }
}
