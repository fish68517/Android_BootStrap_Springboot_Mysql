package com.example.orderfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.orderfood.R;
import com.example.orderfood.model.Store;

import java.util.List;

public class StoreAdapter extends ArrayAdapter<Store> {
    public StoreAdapter(Context context, List<Store> stores) {
        super(context, R.layout.item_store, stores);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_store, parent, false);
        }

        Store store = getItem(position);
        TextView textView = convertView.findViewById(R.id.textViewStoreName);
        textView.setText(store.getStoreName());

        return convertView;
    }
} 