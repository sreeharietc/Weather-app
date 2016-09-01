package com.example.qbclct.netwrkcn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.qbclct.netwrkcn.models.Weather;

import java.util.ArrayList;

/**
 * Created by QBCLCT on 19/7/16.
 */
public class WeatherListAdapter extends BaseAdapter{
    private static LayoutInflater inflater = null;
    private final Context context;
    ArrayList weatherList = new ArrayList();

    public WeatherListAdapter(Context context, int resource , ArrayList objects){
        super();
        this.context = context;
        weatherList = objects;
        int layout = resource;;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
       return weatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return weatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        WetherListCellView whetherData;
        if(view == null)
        {
            view = inflater.inflate(R.layout.list_view, viewGroup, false);
            whetherData = new WetherListCellView(view);
            view.setTag(whetherData);
        }
        else{
            whetherData = (WetherListCellView) view.getTag();
        }
        Weather weather = (Weather) getItem(position);
        whetherData.date.setText(weather.getDate());
        whetherData.text.setText(weather.getText());
        return view;
    }


    private class WetherListCellView {
        TextView date, text;
        public WetherListCellView(View item){
            date = (TextView) item.findViewById(R.id.textView2);
            text = (TextView) item.findViewById(R.id.textView3);
        }
    }
}
