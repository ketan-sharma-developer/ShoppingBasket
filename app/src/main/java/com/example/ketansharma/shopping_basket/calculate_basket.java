package com.example.ketansharma.shopping_basket;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by ketan.sharma.
 * class to calculate the unit price and the overall basket
 */
public class calculate_basket {

    /**calculates the unit price, based on the given currency chosen.
     * This also requires the gbp currency value as it stands, so that
     * it can use this to get back to the base Euro value and calculate
     * from there.
     * Does nothing if the currency is already GBP as these values are already
     * stored in SQLite.
     * @param c
     * @param unit_price
     * @param currency_country
     * @param currency_value
     * @param gbp_currency_value
     * @return
     */
    double calculate_unit_price(Context c, double unit_price, String currency_country,
                              double currency_value, double gbp_currency_value) {
        double currency_inflated = 0;

        if(!currency_country.equals(c.getResources().getString(R.string.gbp))) {
            // get GBP value back to Euro base, if GBP is not chosen
            currency_inflated = (gbp_currency_value * 100);

            unit_price = (unit_price / currency_inflated) * 100;
            if (!currency_country.equals(c.getResources().getString(R.string.eur))) {
                // if not EUR, calculate further
                unit_price *= currency_value;
            }
        }

        return unit_price;
    }

    /**calculates the whole basket by querying the SQLite database and
     * iterating through it to add up the total as it goes along.
     * Also calls the calculate_unit_price method above, to ensure
     * it's calculating from the correct currency.
     * @param c
     * @param currency_country
     * @param currency_value
     * @param gbp_currency_value
     * @return
     */
    double calculate_basket_value(Context c, String currency_country,
                                  double currency_value, double gbp_currency_value) {
        double total = 0;

        item Item = new item();
        String sql = c.getResources().getString(R.string.sql);
        ArrayList<item> items = Item.getItems(c,sql);

        for (int i=0;i<items.size();i++) {
            double unit_price = 0;
            int basket_qty = 0;

            unit_price = items.get(i).getUnitPrice();
            unit_price = calculate_unit_price(c,unit_price,
                    currency_country,currency_value,gbp_currency_value);
            total += (unit_price * items.get(i).getBasketQty());
        }

        return total;
    }
}
