package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract;

/**
 * Created by bplewis5 on 7/28/16.
 */
public class ProductAdapter extends CursorAdapter{

    private final String LOG_TAG = ProductAdapter.class.getSimpleName();



    public static class ViewHolder{
        public final TextView nameTextView;
        public final TextView priceTextView;
        public final TextView quantityTextView;
        public final Button saleButton;

        public ViewHolder(View view) {
            nameTextView = (TextView) view.findViewById(R.id.listitem_name_textview);
            priceTextView = (TextView) view.findViewById(R.id.listitem_price_textview);
            quantityTextView = (TextView) view.findViewById(R.id.listitem_quantity_textview);
            saleButton = (Button) view.findViewById(R.id.listitem_sale_button);
        }
    }

    public ProductAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        String name = cursor.getString(MainFragment.COL_NAME);
        final int quantity = cursor.getInt(MainFragment.COL_QUANTITY);
        double price = cursor.getDouble(MainFragment.COL_PRICE);
        final long id = cursor.getLong(MainFragment.COL_PRODUCT_ID);

        holder.nameTextView.setText(name);
        holder.quantityTextView.setText(Integer.toString(quantity));
        holder.priceTextView.setText(Utils.formatDollar(price));
        holder.saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQuantity = quantity - 1;
                updateQuantity(context, id, newQuantity);
            }
        });
    }

    private void updateQuantity(Context context, long id, int quantity){
        if (quantity < 0){
            return;
        }
        Uri uri = ProductContract.ProductEntry.buildProductWithQuantityUri(id, quantity);
        context.getContentResolver().update(uri, null, null, null);
    }
}
