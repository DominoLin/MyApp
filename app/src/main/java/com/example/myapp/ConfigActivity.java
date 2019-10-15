package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ConfigActivity extends AppCompatActivity {

    EditText ed1,ed2,ed3;
    float dollarRate=0.0f;
    float euroRate=0.0f;
    float wonRate=0.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        ed1 = findViewById(R.id.ed_exDolloar);
        ed2 = findViewById(R.id.ed_exEuro);
        ed3 = findViewById(R.id.ed_exWon);

        Intent intent = getIntent();

        dollarRate = intent.getFloatExtra("dollar_rate",0.0f);
        euroRate = intent.getFloatExtra("euro_rate",0.0f);
        wonRate = intent.getFloatExtra("won_rate",0.0f);

        ed1.setText(String.format("%.4f",dollarRate));
        ed2.setText(String.format("%.4f",euroRate));
        ed3.setText(String.format("%.4f",wonRate));

    }
    public void save(View view){
        dollarRate = Float.parseFloat(ed1.getText().toString());
        euroRate = Float.parseFloat(ed2.getText().toString());
        wonRate = Float.parseFloat(ed3.getText().toString());

        Intent changed = getIntent();
        Bundle bundle = new Bundle();
        bundle.putFloat("dollar_new",dollarRate);
        bundle.putFloat("euro_new",euroRate);
        bundle.putFloat("won_new",wonRate);
        changed.putExtras(bundle);
        setResult(1,changed);
        finish();

    }
}
