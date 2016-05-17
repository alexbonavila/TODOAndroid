package com.alexbonavila.alumne.todolist2;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;


public class CustomListAtapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<TodoItem> list;
    private final LayoutInflater layputInflater;

    public CustomListAtapter(Context context, ArrayList listData) {
        this.context = context;
        this.list = listData;
        layputInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = layputInflater.inflate(R.layout.todolistitem, null);
        } else {

        }

        return convertView;
    }
}

