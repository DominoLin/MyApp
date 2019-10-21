package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class CalculationActivity extends AppCompatActivity implements TextWatcher {

    private static final String TAG = "myList";
    TextView textView,showView;
    EditText editText;
    String name,rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        rate = intent.getStringExtra("rate");
        editText = findViewById(R.id.ed_cal);
        textView = findViewById(R.id.tv_cal1);
        showView = findViewById(R.id.tv_cal2);
        editText.addTextChangedListener(this);
        textView.setText(name);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(editable!=null) {
            if (!editable.toString().equals("") ) {
                float before = Float.parseFloat(editText.getText().toString());
                float r = Float.parseFloat(rate);
                float after = before * r;
                Log.i(TAG, "afterTextChanged: " + before);
                Log.i(TAG, "afterTextChanged: " + r);
                Log.i(TAG, "afterTextChanged: " + after);
                showView.setText(String.format("%.4f", after));
            }
        }
    }
}
