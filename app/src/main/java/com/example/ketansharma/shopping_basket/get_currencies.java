package com.example.ketansharma.shopping_basket;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ketan.sharma on 23/02/2016.
 * class to make HTTP Connection to web api,
 * which returns the currenct exchange rates.
 */
public class get_currencies {
    public String get_rates(final String url) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            HttpURLConnection httpcon = null;

            try {
                httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
            } catch (MalformedURLException e2) {
                return "";
            } catch (IOException e2) {
                return "";
            }

            httpcon.setDoOutput(false);
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Accept", "application/json");
            httpcon.setRequestMethod("GET");

            try {
                is = new BufferedInputStream(httpcon.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                result = sb.toString();
            } catch (Exception e) {
                result = null;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        result = null;
                    }
                }
            }

        } catch (Exception e) {
            result = null;
        }

        return result;
    }
}
