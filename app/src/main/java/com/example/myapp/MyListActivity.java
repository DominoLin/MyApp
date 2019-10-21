package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyListActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "myList";
    Handler handler;
    ArrayList<HashMap<String ,String>>list_data;

    ListAdapter adapter;
    MyAdapter myAdapter;
    ArrayList<HashMap<String,String>> list_item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_list);
        Thread t = new Thread(this);
        t.start();
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 3) {
                    list_item = (ArrayList<HashMap<String,String>>) msg.obj;
//                    adapter = new SimpleAdapter(MyListActivity.this,
//                            list_item,
//                            R.layout.activity_my_list,
//                            new String[]{"Name", "Rate"},
//                            new int[]{R.id.tv_mylist1, R.id.tv_mylist2}
//                    );
//                    setListAdapter(adapter);

                    myAdapter = new MyAdapter(MyListActivity.this, R.layout.activity_my_list, list_item);
                    setListAdapter(myAdapter);

                    getListView().setOnItemClickListener(MyListActivity.this);
                }
                super.handleMessage(msg);
            }
        };
//        getListView().setEmptyView(findViewById(R.id.tv_nodata));
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("是否要删除当前数据")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Log.i(TAG, "onClick: 对话框事件处理");
                        list_item.remove(position);
                        myAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("否",null);
        builder.create().show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Object itemAtPosition = getListView().getItemAtPosition(i);
        HashMap<String, String> map = (HashMap<String, String>) itemAtPosition;
        String titleStr = map.get("Name");
        String detailStr = map.get("Rate");
        Log.i(TAG, "onItemClick: titleStr:"+titleStr);
        Log.i(TAG, "onItemClick: detailStr:"+detailStr);

        Intent rate = new Intent(this, CalculationActivity.class);
        rate.putExtra("name",titleStr);
        rate.putExtra("rate",detailStr);
        startActivity(rate);
    }

    @Override
    public void run() {
        list_data = new ArrayList<HashMap<String, String>>();

        String url = "http://www.usd-cny.com/bankofchina.htm";
        try {
            Document doc = Jsoup.connect(url).get();
            Elements tables = doc.getElementsByTag("table");
            Element table = tables.get(0);
            Elements tds = table.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i += 6) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                String str1 = td1.text();
                String val = td2.text();
                float v = 100f / Float.parseFloat(val);
                val = Float.toString(v);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Name", str1);
                map.put("Rate", val);
                list_data.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(3);
        msg.obj = list_data;
        handler.sendMessage(msg);
    }


}
