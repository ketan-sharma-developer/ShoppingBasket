package com.example.ketansharma.shopping_basket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ketan.sharma on 23/02/2016.
 * Class that inherits from ArrayAdapter to
 * populate the listviews.
 * This is where it is determined what and how
 * each row is shown.
 * OnClickListeners to ImageButtons exist to
 * allow users to change the quantity of
 * items directly from the listview and
 * update the basket total using a broadcast receiver.
 */
public class item_adapter extends ArrayAdapter<item>
{
    private Activity activity;
    private ArrayList<item> lItem;
    private static LayoutInflater inflater = null;
    private int lastFocussedPosition = -1;
    private Handler handler = new Handler();
    private String currency_country = "";
    private double currency_value = 0;
    private double gbp_currency_value = 0;
    private int basket_qty = 0;

    public item_adapter(Activity activity, int textViewResourceId, ArrayList<item> results,
                        String currency, double currency_value, double gbp_currency_value) {
        super(activity, textViewResourceId, results);
        try {
            this.activity = activity;
            this.lItem = results;
            this.currency_country = currency;
            this.currency_value = currency_value;
            this.gbp_currency_value = gbp_currency_value;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {

        }
    }

    public int getCount() {
        return lItem.size();
    }

    public item getItem(item position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView item_id;
        public TextView item_name;
        public TextView item_unit_price;
        public TextView item_unit;
        public TextView basket_qty_value;
        public ImageButton basket_qty_add;
        public ImageButton basket_qty_minus;
        public ImageButton basket_qty_delete;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        final String basket_qty_string = getContext().getResources().getString(R.string.basket_qty);
        final String intent_action = getContext().getResources().getString(R.string.intent_action);
        final String update_total = getContext().getResources().getString(R.string.update_total);
        final String update_msg = getContext().getResources().getString(R.string.update_msg);
        final String value_format = getContext().getResources().getString(R.string.value_format);

        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.item_list_row, null);
                holder = new ViewHolder();

                holder.item_id = (TextView) vi.findViewById(R.id.item_id);
                holder.item_name = (TextView) vi.findViewById(R.id.item_name);
                holder.item_unit_price = (TextView) vi.findViewById(R.id.item_unit_price);
                holder.item_unit = (TextView) vi.findViewById(R.id.item_unit);
                holder.basket_qty_value = (TextView) vi.findViewById(R.id.basket_qty_value);
                holder.basket_qty_add = (ImageButton) vi.findViewById(R.id.basket_qty_add);
                holder.basket_qty_minus = (ImageButton) vi.findViewById(R.id.basket_qty_minus);
                holder.basket_qty_delete = (ImageButton) vi.findViewById(R.id.basket_qty_delete);

                vi.setTag(holder);
            }
            else {
                holder = (ViewHolder) vi.getTag();
            }


             double unit_price = lItem.get(position).getUnitPrice();

            calculate_basket cb = new calculate_basket();
            unit_price = cb.calculate_unit_price(getContext(),unit_price,
                    currency_country, currency_value, gbp_currency_value);

            holder.item_id.setText(String.valueOf(lItem.get(position).getID()));
            holder.item_name.setText(lItem.get(position).getName().trim());
            holder.item_unit_price.setText(String.format(value_format, unit_price));
            holder.item_unit.setText(" " + currency_country + " " + lItem.get(position).getUnit());

            basket_qty = lItem.get(position).getBasketQty();
            holder.basket_qty_value.setText(String.valueOf(basket_qty));

            holder.basket_qty_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_database item_db = new item_database(getContext());
                    basket_qty = item_db.getInteger(lItem.get(position).getID(), basket_qty_string);
                    basket_qty += 1;
                    item_db.update(lItem.get(position).getID(), basket_qty);
                    holder.basket_qty_value.setText(String.valueOf(basket_qty));

                    final Intent intent = new Intent();
                    intent.setAction(intent_action);
                    intent.putExtra(update_msg, update_total);
                    getContext().sendBroadcast(intent);
                }
            });

            holder.basket_qty_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_database item_db = new item_database(getContext());
                    basket_qty = item_db.getInteger(lItem.get(position).getID(), basket_qty_string);
                    if(basket_qty > 0) {
                        basket_qty -= 1;
                        item_db.update(lItem.get(position).getID(), basket_qty);
                        holder.basket_qty_value.setText(String.valueOf(basket_qty));

                        final Intent intent = new Intent();
                        intent.setAction(intent_action);
                        intent.putExtra(update_msg, update_total);
                        getContext().sendBroadcast(intent);
                    }
                }
            });

            holder.basket_qty_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    basket_qty=0;
                    item_database item_db = new item_database(getContext());
                    item_db.update(lItem.get(position).getID(), basket_qty);
                    holder.basket_qty_value.setText(String.valueOf(basket_qty));

                    final Intent intent = new Intent();
                    intent.setAction(intent_action);
                    intent.putExtra(update_msg, update_total);
                    getContext().sendBroadcast(intent);
                }
            });

        } catch (Exception e) {

        }
        return vi;
    }
}
