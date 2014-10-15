package com.dhenry.lendingclubscraper.app.views.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dhenry.lendingclubscraper.app.R;

import java.util.ArrayList;

public class KeyValueAdapter extends BaseAdapter {

    private Context mContext;
    private static LayoutInflater inflater = null;
    private ArrayList<Pair<String, String>> list = new ArrayList<Pair<String, String>>();

    public KeyValueAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View newView = convertView;
        ViewHolder holder;

        Pair<String, String> curr = list.get(position);

        if (null == convertView) {
            holder = new ViewHolder();
            newView = inflater.inflate(R.layout.key_value_view, null);
            holder.key = (TextView) newView.findViewById(R.id.key);
            holder.value = (TextView) newView.findViewById(R.id.value);
            newView.setTag(holder);

        } else {
            holder = (ViewHolder) newView.getTag();
        }

        holder.key.setText(curr.first);
        holder.value.setText(curr.second);

        return newView;
    }

    static class ViewHolder {
        TextView key;
        TextView value;
    }

    public void add(Pair<String, String> listItem) {
        list.add(listItem);
        notifyDataSetChanged();
    }

    public void removeAllViews(){
        list.clear();
        this.notifyDataSetChanged();
    }
}
