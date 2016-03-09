package com.example.ketansharma.shopping_basket;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ketan.sharma on 23/02/2016.
 * This activity shows all products
 * Users can modify the quantities of what they want to buy.
 */
public class main_activity extends AppCompatActivity {
    String currency;
    double currency_value;
    double gbp_currency_value;

    /** get chosen currency from basket activity (if this was invoked from there)
     * populate list
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (intent.hasExtra("CURRENCY")) {
            currency = intent.getStringExtra("CURRENCY");
            currency_value = intent.getDoubleExtra("CURRENCY_VALUE", 0);
            gbp_currency_value = intent.getDoubleExtra("GBP_CURRENCY_VALUE",0);
        }
        else {
            currency = getResources().getString(R.string.gbp);
            currency_value = 0;
            gbp_currency_value = 0;
        }

        fillList();

        //check if the basket total is not zero, before allowing user to check out.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value_format = getResources().getString(R.string.value_format);
                calculate_basket cb = new calculate_basket();
                double basket_value = cb.calculate_basket_value(main_activity.this,currency,currency_value,gbp_currency_value);

                if (basket_value > 0) {
                    finish();

                    Intent intent = new Intent(main_activity.this, basket_activity.class);
                    intent.putExtra("CURRENCY", currency);
                    intent.putExtra("CURRENCY_VALUE", currency_value);
                    intent.putExtra("GBP_CURRENCY_VALUE", gbp_currency_value);

                    startActivity(intent);
                }
                else {
                    String empty_basket_warning = getResources().getString(R.string.empty_basket_warning);
                    Snackbar.make(findViewById(android.R.id.content), empty_basket_warning, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about :
                Toast.makeText(this, "Developed by Ketan Sharma ketan.sharma.developer@gmail.com", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillList() {
        item Item = new item();
        ArrayList<item> items = Item.getItems(this, "");

        ListView listView = (ListView) findViewById(R.id.item_list_view);
        item_adapter aItem;
        aItem = new item_adapter(main_activity.this, 0, items, currency, currency_value, gbp_currency_value);
        listView.setAdapter(aItem);
    }
}
