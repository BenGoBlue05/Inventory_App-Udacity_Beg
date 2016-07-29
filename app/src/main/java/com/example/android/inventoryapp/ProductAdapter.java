package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by bplewis5 on 7/28/16.
 */
public class ProductAdapter extends CursorAdapter{

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
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        String name = cursor.getString(MainFragment.COL_NAME);
        String quantity = "" + cursor.getInt(MainFragment.COL_QUANTITY);
        String price = "" + cursor.getDouble(MainFragment.COL_PRICE);

        holder.nameTextView.setText(name);
        holder.quantityTextView.setText(quantity);
        holder.priceTextView.setText(price);

    }
}
