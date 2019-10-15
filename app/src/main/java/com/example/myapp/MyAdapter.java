package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends ArrayAdapter {

    public MyAdapter(Context context, int resource, ArrayList<HashMap<String, String>> list) {
        super(context, resource, list);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View itemView = convertView;
        if(itemView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_my_list,parent,false);
        }
        Map<String,String> map = (Map<String, String>) getItem(position);
        TextView title = itemView.findViewById(R.id.tv_mylist1);
        TextView detail = itemView.findViewById(R.id.tv_mylist2);
        title.setText(map.get("Name"));
        detail.setText(map.get("Rate"));

        return itemView;
    }

}
