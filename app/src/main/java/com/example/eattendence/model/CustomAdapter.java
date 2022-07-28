package com.example.eattendence.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.eattendence.R;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> stringlist;
    ArrayList<String> list;
    LayoutInflater inflater;
    public static ArrayList<String> array;

    public CustomAdapter(Context applicationContext, ArrayList<String> stringlist , ArrayList<String> list){
        this.context=applicationContext;
        this.stringlist=stringlist;
        this.list=list;

        array=new ArrayList<>();
        for (int i=0;i<stringlist.size();i++){
            array.add("Not Attempted");
        }
        inflater = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return stringlist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listview_attendence,null);

        TextView textView=view.findViewById(R.id.text1);
        TextView textView1=view.findViewById(R.id.text2);
        RadioButton absent=view.findViewById(R.id.absent);
        RadioButton present=view.findViewById(R.id.present);
        //RadioButton executed=view.findViewById(R.id.executed);

        absent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    array.set(i,"Absent");
                    Log.d("Attendence","Absent");
                }
            }
        });

        present.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    array.set(i,"Present");
                    Log.d("Attendence","present");
                }
            }
        });

        /*executed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    array.set(i,"Executed");
                }
            }
        });*/

        textView.setText(stringlist.get(i));
        textView1.setText(list.get(i));
        return view;
    }
}
