package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {
    TextView showA,showB;
    String scoreA,scoreB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        showA = findViewById(R.id.tv_showA);
        showB = findViewById(R.id.tv_showB);
    }
    public void add1(View view){
        if(view.getId()==R.id.btn_teamA1) {
            addA(1);
        }else {
            addB(1);
        }
    }

    public void add2(View view){
        if(view.getId()==R.id.btn_teamA2) {
            addA(2);
        }else {
            addB(2);
        }
    }

    public void add3(View view){
        if(view.getId()==R.id.btn_teamA3) {
            addA(3);
        }else {
            addB(3);
        }
    }

    public void reset(View view){
        showA.setText("0");
        showB.setText("0");
    }

    public void addA(int i){
        scoreA = showA.getText().toString();
        int temp = Integer.parseInt(scoreA)+i;
        showA.setText(Integer.toString(temp));
    }
    public void addB(int i){
        scoreB = showB.getText().toString();
        int temp = Integer.parseInt(scoreB)+i;
        showB.setText(Integer.toString(temp));
    }

}
