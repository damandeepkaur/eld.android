package com.bsmwireless.screens.editevent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bsmwireless.widgets.alerts.DutyType;

import java.util.List;

public final class DutyTypeSpinnerAdapter extends ArrayAdapter<DutyType> {

    public DutyTypeSpinnerAdapter(@NonNull Context context, @NonNull List<DutyType> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getItemView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getItemView(position, convertView, parent);
    }

    private View getItemView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DutyType type = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null);
        }

        TextView name = convertView.findViewById(android.R.id.text1);
        name.setText(getContext().getString(type.getName()));

        return convertView;
    }
}
