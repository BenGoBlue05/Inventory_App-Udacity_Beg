package com.example.android.inventoryapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by bplewis5 on 8/1/16.
 */
public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    //From Google Sample
    public static Bitmap getBitmap(Context context, Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ContentResolver resolver = context.getContentResolver();
            try{
                resolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
        try {
            parcelFileDescriptor = context.getContentResolver()
                    .openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return bitmap;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Image load failed", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error closing ParcelFile Descriptor", e);
            }
        }
    }

    public static void makeToast(String fieldName, Context context){
        Toast.makeText(context,
                context.getString(R.string.please_enter) + " "  + fieldName,
                Toast.LENGTH_LONG).show();
    }

    public static String formatDollar(double dollars){
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter(builder, Locale.US);
        return formatter.format("$%.2f", dollars).toString();
    }

}
