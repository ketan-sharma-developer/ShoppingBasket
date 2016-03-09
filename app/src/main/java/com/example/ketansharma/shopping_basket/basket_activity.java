package com.example.ketansharma.shopping_basket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ketan.sharma on 23/02/2016.
 * This activity only shows items that have a quantity above zero
 * Users can still modify the contents of the basket, as they can on main_activity
 */
public class basket_activity extends AppCompatActivity {
    public JSONObject rates;
    String currency;
    double currency_value;
    double gbp_currency_value;
    private BroadcastReceiver mReceiver;

    //get chosen currency from main activity, populate list and retrieve exchange rates
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        currency = intent.getStringExtra("CURRENCY");
        currency_value = intent.getDoubleExtra("CURRENCY_VALUE", 0);
        gbp_currency_value = intent.getDoubleExtra("GBP_CURRENCY_VALUE",0);

        fillList();

        new get_rates().execute();
    }

    // handle user tapping back button.  Call main activity as a new intent to force a refresh
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(basket_activity.this, main_activity.class);
        intent.putExtra("CURRENCY", currency);
        intent.putExtra("CURRENCY_VALUE", currency_value);
        intent.putExtra("GBP_CURRENCY_VALUE", gbp_currency_value);

        startActivity(intent);
    }

    //Initialize a broadcast receiver, so array adapter can notify that the basket total should be re-calculated
    @Override
    protected void onResume() {
        super.onResume();
        String intent_action = this.getResources().getString(R.string.intent_action);

        IntentFilter intentFilter = new IntentFilter(
                intent_action);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sql = getResources().getString(R.string.sql);
                String update_total = getResources().getString(R.string.update_total);
                String update_msg = getResources().getString(R.string.update_msg);
                String msg = intent.getStringExtra(update_msg);

                if (msg.equals(update_total)) {
                    calculate_basket();
                }
            }
        };

        this.registerReceiver(mReceiver, intentFilter);
    }

    //unregister the broadcast receiver
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mReceiver);
    }

    //if the back button on the toolbar is pressed, handle it in the same way as a hardware back key
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //get array of basket items from the SQLite database and display in listview
    private void fillList() {
        String sql = getResources().getString(R.string.sql);
        item Item = new item();
        ArrayList<item> items = Item.getItems(this,sql);

        ListView listView = (ListView) findViewById(R.id.basket_list_view);
        item_adapter aItem;
        aItem = new item_adapter(basket_activity.this,0,items,currency, currency_value, gbp_currency_value);
        listView.setAdapter(aItem);

        calculate_basket();
    }

    //method to re-calculate the total of the basket, using the calculate_basket class
    public void calculate_basket() {
        String value_format = this.getResources().getString(R.string.value_format);
        calculate_basket cb = new calculate_basket();
        double basket_value = cb.calculate_basket_value(basket_activity.this,currency,currency_value,gbp_currency_value);
        TextView basket_total_value = (TextView) findViewById(R.id.basket_total_value);
        basket_total_value.setText(String.format(value_format, basket_value));
    }

    //creates an async task to get current rates from the api
    private class get_rates extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            get_currencies currencies = new get_currencies();
            String url = getResources().getString(R.string.currency_url);
            String sJSON = currencies.get_rates(url);

            return sJSON;
        }

        //OnPostExecute processes the JSON returned and populates the currency spinner
        @Override
        protected void onPostExecute(String sJSON) {
            super.onPostExecute(sJSON);

            if (sJSON != null) {
                try {
                    String eur = getResources().getString(R.string.eur);
                    String rates_string = getResources().getString(R.string.rates);

                    JSONObject jsonObj = new JSONObject(sJSON);
                    rates = jsonObj.getJSONObject(rates_string);
                    final ArrayList<String> aKeys = new ArrayList<String>();
                    aKeys.add(eur);

                    Iterator<String> keys = rates.keys();
                    while (keys.hasNext()) {
                        aKeys.add(keys.next());
                    }

                    Spinner currency_spinner = (Spinner) findViewById(R.id.currency_value);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(basket_activity.this, android.R.layout.simple_spinner_item, aKeys);
                    currency_spinner.setAdapter(adapter);
                    currency_spinner.setSelection(aKeys.indexOf(currency),false);

                    currency_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            currency = aKeys.get(position);

                            try {
                                currency_value = rates.getDouble(aKeys.get(position));
                            } catch (JSONException e) {
                                currency_value = 1;
                            }

                            try {
                                gbp_currency_value = rates.getDouble(aKeys.get(aKeys.indexOf(getResources().getString(R.string.gbp))));
                            } catch (JSONException e) {

                            }

                            fillList();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {

                        }
                    });
                } catch (JSONException e) {

                }
            }
        }
    }
}
