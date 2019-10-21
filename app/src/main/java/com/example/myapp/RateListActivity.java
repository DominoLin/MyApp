package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable {

    public static final String TAG = "RateList";
    Handler handler;
    private String update = "";
    private final String DATE_SP_KEY = "update_date";
    List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_rate_list);

        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        update = sharedPreferences.getString("update_date","");
        Log.i(TAG, "lastRateUpdateDate : " + update);
        String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Thread t = new Thread(this);

        if(curDateStr.equals(update)){
            Log.i(TAG, "日期相等，oncreate从数据库获取数据");
            RateManager manager = new RateManager(this);
            for (RateItem item : manager.listAll()){
                list.add(item.getCurName()+" = "+item.getCurRate());
            }
            ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list);
            setListAdapter(adapter);
        }else {
            t.start();
        }

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what==2){
                    List<String> list_data = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list_data);
                    setListAdapter(adapter);
                }

                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {

        List<String> list = new ArrayList<String>();
        String url="http://www.usd-cny.com/bankofchina.htm";

        String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Log.i(TAG, "curDateStr: "+curDateStr + " | lastRateUpdateDate :" + update);
        if(curDateStr.equals(update)){
            Log.i(TAG, "日期相等，从数据库获取数据");
            RateManager manager = new RateManager(this);
            for (RateItem item : manager.listAll()){
                list.add(item.getCurName()+" = "+item.getCurRate());
            }
        }else {
            Log.i(TAG, "日期不相等，从网络上获取数据");

            try {
                List<RateItem> rateList = new ArrayList<RateItem>();
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
                    val = Float.toString(v);
                    list.add(str1+":"+val);
                    rateList.add(new RateItem(str1, val));
                }
                //将数据写入数据库
                RateManager manager = new RateManager(this);
                manager.deleteAll();
                manager.addAll(rateList);

                //记录更新日期
                SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("update_date",curDateStr);
                editor.apply();

            } catch (IOException e){
                e.printStackTrace();
            }


        }

        Message msg = handler.obtainMessage(2);
        msg.obj = list;
        handler.sendMessage(msg);

    }
}
