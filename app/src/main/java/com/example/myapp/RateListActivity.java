package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_rate_list);

        Thread t = new Thread(this);
        t.start();
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
                val = Float.toString(v);

                list.add(str1+":"+val);

            }

        } catch (IOException e){
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(2);
        msg.obj = list;
        handler.sendMessage(msg);

    }
}
