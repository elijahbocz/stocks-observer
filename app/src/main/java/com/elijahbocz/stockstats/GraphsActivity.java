package com.elijahbocz.stockstats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class GraphsActivity extends AppCompatActivity {
    private String symbol;
    private static final String TAG = GraphsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        Intent intent = getIntent();
        symbol = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView symbolTextView = (TextView) findViewById(R.id.graph_symbol);
        symbolTextView.setText(symbol);
        getPastWeekData();
        getPastMonthData();
    }

    protected void getPastWeekData() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();
        String date_to = dateFormat.format(date);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7 );
        Date to_date1 = cal.getTime();
        String date_from = dateFormat.format(to_date1);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "http://api.marketstack.com/v1/eod?access_key=834f9898821638887c36d4ac835de85e&symbols=" + symbol + "&sort=ASC&interval=3hour&date_from=" + date_from + "&date_to=" + date_to;
        Log.d(TAG, url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.wtf(response.toString());
                LineChart lineChart = (LineChart) findViewById(R.id.week_chart);
                ArrayList<Entry> lineEntries = new ArrayList<>();
                try {
                    JSONArray arr = response.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++) {
                        String date = arr.getJSONObject(i).getString("date");
                        String[] parts = date.split("-");
                        Log.d(TAG, arr.getJSONObject(i).toString());
                        String high = arr.getJSONObject(i).getString("high");
                        float fhigh = Float.parseFloat(high);
                        String day = String.valueOf(parts[2].charAt(0));
                        day += String.valueOf(parts[2].charAt(1));
                        lineEntries.add(new Entry(Float.parseFloat(day), fhigh));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                createGraph(lineChart, lineEntries, "Prices over the past week");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.wtf(error.getMessage(), "utf-8");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    protected void getPastMonthData() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();
        String date_to = dateFormat.format(date);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30 );
        Date to_date1 = cal.getTime();
        String date_from = dateFormat.format(to_date1);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "http://api.marketstack.com/v1/eod?access_key=834f9898821638887c36d4ac835de85e&symbols=" + symbol + "&sort=ASC&interval=3hour&date_from=" + date_from + "&date_to=" + date_to;
        Log.d(TAG, url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.wtf(response.toString());
                LineChart lineChart = (LineChart) findViewById(R.id.month_chart);
                ArrayList<Entry> lineEntries = new ArrayList<>();
                try {
                    JSONArray arr = response.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++) {
                        String date = arr.getJSONObject(i).getString("date");
                        String[] parts = date.split("-");
                        Log.d(TAG, arr.getJSONObject(i).toString());
                        String high = arr.getJSONObject(i).getString("high");
                        float fhigh = Float.parseFloat(high);
                        String day = String.valueOf(parts[2].charAt(0));
                        day += String.valueOf(parts[2].charAt(1));
                        lineEntries.add(new Entry(Float.parseFloat(day), fhigh));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                createGraph(lineChart, lineEntries, "Prices over that past month");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.wtf(error.getMessage(), "utf-8");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    protected void createGraph(LineChart lineChart, ArrayList<Entry> lineEntries, String description) {
        LineDataSet lineDataSet = new LineDataSet(lineEntries,"Price");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setLineWidth(2);
        if (lineEntries.get(0).getY() > lineEntries.get(lineEntries.size() - 1).getY()) {
            lineDataSet.setColor(Color.parseColor("#e63946"));
            lineDataSet.setCircleColor(Color.parseColor("#e63946"));
        } else {
            lineDataSet.setColor(Color.parseColor("#37DB5E"));
            lineDataSet.setCircleColor(Color.parseColor("#37DB5E"));
        }
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setHighLightColor(Color.RED);
        lineDataSet.setValueTextSize(12);
        lineDataSet.setValueTextColor(Color.parseColor("#1d3557"));

        LineData lineData = new LineData(lineDataSet);

        lineChart.getDescription().setText(description);
        lineChart.getDescription().setTextSize(12);
//                lineChart.setDrawMarkers(true);
//                lineChart.setMarker(markerView(context));
//                lineChart.getAxisLeft().addLimitLine(lowerLimitLine(2,"Lower Limit",2,12,getColor("defaultOrange"),getColor("defaultOrange")));
//                lineChart.getAxisLeft().addLimitLine(upperLimitLine(5,"Upper Limit",2,12,getColor("defaultGreen"),getColor("defaultGreen")));
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        lineChart.animateY(1000);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getXAxis().setGranularity(1.0f);
        lineChart.getXAxis().setLabelCount(lineDataSet.getEntryCount());
        lineChart.setData(lineData);
    }
}