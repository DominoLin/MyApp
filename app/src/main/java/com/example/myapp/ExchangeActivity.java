package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExchangeActivity extends AppCompatActivity implements Runnable{
    private static final String TAG = "Exchange";
    EditText input;
    TextView show;
    float before,after;
    float dollarRate =0.0f;
    float euroRate =0.0f;
    float wonRate =0.0f;
    String updateDate = "";
    Handler handler;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        input = findViewById(R.id.ed_exInput);
        show = findViewById(R.id.tv_exShow);

        //获取sharedPreferences保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);
        updateDate = sharedPreferences.getString("update_date","");
        Log.i(TAG, "onCreate: 从sharedPreferences中获得数据");
        Log.i(TAG, "onCreate: dollarRate="+ dollarRate);
        Log.i(TAG, "onCreate: euroRate="+ euroRate);
        Log.i(TAG, "onCreate: wonRate="+ wonRate);
        Log.i(TAG, "onCreate: updateDate="+ updateDate);

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = simpleDateFormat.format(today);
        Log.i(TAG, "onCreate: 当前系统时间："+todayStr);

        //判断时间
        if(!todayStr.equals(updateDate)){
            Log.i(TAG, "onCreate: 需要更新");
            //开启子线程
            Thread t = new Thread(this);
            t.start();
        }else {
            Log.i(TAG, "onCreate: 不需要更新");
        }


        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if(msg.what==1){

                    Bundle bundle = (Bundle) msg.obj;
                    dollarRate = bundle.getFloat("dollar_net",0.0f);
                    euroRate = bundle.getFloat("euro_net",0.0f);
                    wonRate = bundle.getFloat("won_net",0.0f);

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("dollar_rate",dollarRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.putFloat("won_rate",wonRate);
                    editor.putString("update_date",todayStr);
                    editor.apply();

                    Toast.makeText(ExchangeActivity.this,"汇率已更新",Toast.LENGTH_SHORT).show();
                }
            }
        };

    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exchange, menu);
        return true;
    }
    //菜单点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item1){
            Intent intent = new Intent(this, RateListActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.item2){
            Intent intent = new Intent(this, MyListActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void dollar(View view){
        show.setText("＄"+exchange(dollarRate));
    }

    public void euro(View view){
        show.setText("€ "+exchange(euroRate));
    }

    public void won(View view){
        show.setText("₩ "+exchange(wonRate));
    }

    public void config(View view){
        Intent data_config = new Intent(this, ConfigActivity.class);

        data_config.putExtra("dollar_rate", dollarRate);
        data_config.putExtra("euro_rate", euroRate);
        data_config.putExtra("won_rate", wonRate);
        //打开返回结果的Activity
        startActivityForResult(data_config,1);
    }

    //处理返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       //返回之后从Intent中的Bundle中得到新的数据
        if(requestCode == 1 && resultCode == 1){
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("dollar_new",0.1f);
            euroRate = bundle.getFloat("euro_new",0.1f);
            wonRate = bundle.getFloat("won_new",0.1f);

            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();
            Log.i(TAG, "onActivityResult: 将数据保存到sharedPreferences");
            Log.i(TAG, "onActivityResult: dollarRate="+ dollarRate);
            Log.i(TAG, "onActivityResult: euroRate="+ euroRate);
            Log.i(TAG, "onActivityResult: wonRate="+ wonRate);
            Log.i(TAG, "onActivityResult: 数据已保存到sharedPreferences");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String exchange(float rate){
        String value = "";
        if(input.getText().toString().equals("")){
            Toast.makeText(this,"Please input number",Toast.LENGTH_SHORT).show();
        }
        else{
            before = Float.parseFloat(input.getText().toString());
            after = before*rate;
            value = String.format("%.4f",after);
        }
        return value;
    }

    //子线程
    @Override
    public void run() {
        Log.i(TAG, "run: 子线程从网络获取汇率");
        Bundle bundle = new Bundle();
        List<RateItem> rateList = new ArrayList<RateItem>();

        String url="http://www.usd-cny.com/bankofchina.htm";
        try {
            Document doc = Jsoup.connect(url).get();
            Elements tables = doc.getElementsByTag("table");
            Element table = tables.get(0);
            Elements tds = table.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                String str1 = td1.text();
                String val = td2.text();
                float v = 100f/Float.parseFloat(val);
                rateList.add(new RateItem(str1, String.valueOf(v)));
                if(str1.equals("欧元")){
                    bundle.putFloat("euro_net",v);
                    Log.i(TAG, "run: "+str1+":"+v);
                }else if(str1.equals("韩元")){
                    bundle.putFloat("won_net",v);
                    Log.i(TAG, "run: "+str1+":"+v);
                }else if(str1.equals("美元")){
                    bundle.putFloat("dollar_net",v);
                    Log.i(TAG, "run: "+str1+":"+v);
                }
            }
            //将数据写入数据库
            RateManager manager = new RateManager(this);
            manager.deleteAll();
            manager.addAll(rateList);

        } catch (IOException e){
            e.printStackTrace();
        }


        Message msg = handler.obtainMessage(1);
        msg.obj = bundle;
        handler.sendMessage(msg);
    }
}
