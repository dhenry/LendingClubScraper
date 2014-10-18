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

import butterknife.ButterKnife;
import butterknife.InjectView;

public class KeyValueAdapter<F, S> extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<Pair<F, S>> list = new ArrayList<Pair<F, S>>();

    public KeyValueAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Pair<F, S> getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.key_value_view, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        Pair<F, S> item = list.get(position);

        holder.key.setText(item.first.toString());
        holder.value.setText(item.second.toString());

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.key) TextView key;
        @InjectView(R.id.value) TextView value;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public void add(Pair<F, S> listItem) {
        list.add(listItem);
        notifyDataSetChanged();
    }

    public void clear(){
        list.clear();
        this.notifyDataSetChanged();
    }
}
