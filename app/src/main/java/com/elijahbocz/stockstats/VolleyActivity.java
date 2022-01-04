package com.elijahbocz.stockstats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;


public class VolleyActivity extends AppCompatActivity {
    RequestQueue queue = RequestQueueSingleton.getInstance(this).getRequestQueue();
    private String symbol;
    public static final String EXTRA_MESSAGE = "com.elijahbocz.stockstats.MESSAGE";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);

        Intent intent = getIntent();
        symbol = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        getStockInfo();
        getStockQuote();
        getKeyStats();
    }

    public void goToGraphs(View view) {
        Intent intent = new Intent(this, GraphsActivity.class);
        intent.putExtra(EXTRA_MESSAGE, symbol);
        startActivity(intent);
    }

    protected void getStockInfo() {
        final String url = "https://cloud.iexapis.com/stable/stock/" + symbol + "/company?token=sk_fb4b6af652034c53a2f4321ef9e9159d&period=annual";
        JsonObjectRequest stockCompany = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.wtf(response.toString());
                try {
                    TextView symbolTextView = (TextView) findViewById(R.id.symbol);
                    TextView companyNameTextView = (TextView) findViewById(R.id.company_name);
                    TextView industryTextView = (TextView) findViewById(R.id.industry);
                    TextView exchangeTextView = (TextView) findViewById(R.id.exchange);
                    TextView websiteTextView = (TextView) findViewById(R.id.website);

                    String symbol = response.getString("symbol");
                    String companyName = response.getString("companyName");
                    String industry = response.getString("industry");
                    String exchange = response.getString("exchange");
                    String website = response.getString("website");

                    symbolTextView.setText(symbol);
                    companyNameTextView.setText(companyName);
                    industryTextView.setText(industry);
                    exchangeTextView.setText(exchange);
                    websiteTextView.setText(website);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.wtf(error.getMessage(), "utf-8");
            }
        });
        queue.add(stockCompany);

        final String logoUrl = "https://cloud.iexapis.com/stable/stock/" + symbol + "/logo?token=sk_fb4b6af652034c53a2f4321ef9e9159d&period=annual";
        JsonObjectRequest stockLogo = new JsonObjectRequest(logoUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.wtf(response.toString());
                try {
                    ImageView logoImageView = (ImageView) findViewById(R.id.stock_logo);
                    Picasso.get().load(response.getString("url")).into(logoImageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.wtf(error.getMessage(), "utf-8");
            }
        });
        queue.add(stockLogo);
    }

    protected void getStockQuote() {
        final String url = "https://cloud.iexapis.com/stable/stock/" + symbol + "/quote?token=sk_fb4b6af652034c53a2f4321ef9e9159d&period=annual";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.wtf(response.toString());
                try {
                    TextView priceTextView = (TextView) findViewById(R.id.price_label);
                    TextView changeTextView = (TextView) findViewById(R.id.change_label);
                    TextView changePercentTextView = (TextView) findViewById(R.id.change_percent_label);
                    TextView priceSourceView = (TextView) findViewById(R.id.price_source);
                    TextView latestTimeView = (TextView) findViewById(R.id.latest_time);

                    String price = response.getString("latestPrice");
                    String priceSource = response.getString("latestSource");
                    String latestTime = response.getString("latestTime");
                    String change = response.getString("change");
                    Double changePercent = Double.parseDouble(response.getString("changePercent"));
                    changePercent = changePercent * 100;

                    priceTextView.setText(price);
                    priceSourceView.setText(priceSource);
                    latestTimeView.setText(latestTime);
                    changeTextView.setText(change);
                    changePercentTextView.setText(new DecimalFormat("#.###").format(changePercent));

                    if (Double.parseDouble(change) < 0) {
                        changeTextView.setTextColor(Color.parseColor("#C2303D"));
                        changePercentTextView.setTextColor(Color.parseColor("#C2303D"));
                    } else if (Double.parseDouble(change) > 0) {
                        changeTextView.setTextColor(Color.parseColor("#078F27"));
                        changePercentTextView.setTextColor(Color.parseColor("#078F27"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.wtf(error.getMessage(), "utf-8");
            }
        });
        queue.add(jsonObjectRequest);
    }

    protected void getKeyStats() {
        final String url = "https://cloud.iexapis.com/stable/stock/" + symbol + "/stats?token=sk_fb4b6af652034c53a2f4321ef9e9159d&period=annual";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.wtf(response.toString());
                try {
                    DecimalFormat df = new DecimalFormat("#.##");
                    DecimalFormat df_yield = new DecimalFormat("#.#######");
                    TextView marketCapTextView = (TextView) findViewById(R.id.market_cap);
                    TextView employeesTextView = (TextView) findViewById(R.id.employees);
                    TextView yearRangeTextView = (TextView) findViewById(R.id.year_range);
                    TextView peRatioTextView = (TextView) findViewById(R.id.pe_ratio);
                    TextView betaTextView = (TextView) findViewById(R.id.beta);
                    TextView dividendYieldTextView = (TextView) findViewById(R.id.dividend_yield);

                    String marketCap = response.getString("marketcap");
                    String employees = response.getString("employees");
                    String week52high = response.getString("week52high");
                    String week52low = response.getString("week52low");
                    Double peRatio = Double.parseDouble(response.getString("peRatio"));
                    Double beta = Double.parseDouble(response.getString("beta"));
                    Double dividendYield = Double.parseDouble(response.getString("dividendYield"));

                    marketCapTextView.setText(getReadableMarketCap(marketCap));
                    employeesTextView.setText(employees);
                    yearRangeTextView.setText(week52low + " - " + week52high);
                    peRatioTextView.setText(df.format(peRatio));
                    betaTextView.setText(df.format(beta));
                    dividendYieldTextView.setText(df_yield.format(dividendYield));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.wtf(error.getMessage(), "utf-8");
            }
        });
        queue.add(jsonObjectRequest);
    }

    protected String getReadableMarketCap(String marketCap) {
        DecimalFormat df = new DecimalFormat("#.##");
        long marketCapLong = Long.parseLong(marketCap);
        long quotient;
        long trillion = 1000000000000L;
        if (marketCapLong / trillion > 1) {
            quotient = marketCapLong / trillion;
            return df.format(quotient) + "T";
        } else if (marketCapLong / 1000000000 > 1) {
            quotient = marketCapLong / 1000000000;
            return df.format(quotient) + "B";
        } else if (marketCapLong / 1000000 > 1) {
            quotient = marketCapLong / 1000000;
            return df.format(quotient) + "M";
        }
        return "";
    }
}