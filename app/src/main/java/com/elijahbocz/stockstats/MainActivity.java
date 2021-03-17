package com.elijahbocz.stockstats;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.elijahbocz.stockstats.MESSAGE";
    private boolean resIsValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendRequest(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        String uppercaseMessage = message.toUpperCase();
        isValidSymbol(uppercaseMessage);
    }

    public void isValidSymbol(String symbol) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        VolleyLog.wtf("Symbol: " + symbol);
        final String url = "https://cloud.iexapis.com/stable/stock/" + symbol + "/company?token=sk_fb4b6af652034c53a2f4321ef9e9159d&period=annual";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.wtf(response.toString());
                handleValidSymbol(symbol, true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.wtf(error.getMessage(), "utf-8");
                handleValidSymbol(symbol, false);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void handleValidSymbol(String symbol, boolean isValid) {
        if (isValid) {
            Intent intent = new Intent(this, VolleyActivity.class);
            intent.putExtra(EXTRA_MESSAGE, symbol);
            startActivity(intent);
        } else {
            TextView errorTextView = (TextView) findViewById(R.id.error);
            errorTextView.setText("Please enter a valid symbol");
            errorTextView.setTextColor(Color.parseColor("#e63946"));
        }
    }
}