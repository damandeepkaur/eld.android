package com.bsmwireless.screens.common.menu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bsmwireless.widgets.alerts.DutyType;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseMenuAdapter extends ArrayAdapter<DutyType> {
    private DutyType[] mDutyTypes = new DutyType[0];

    BaseMenuAdapter(@NonNull Context context, @NonNull DutyType[] dutyTypes) {
        super(context, R.layout.view_item_dashboard, dutyTypes);
        mDutyTypes = dutyTypes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_item_dashboard, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.bind(mDutyTypes[position]);

        return convertView;
    }

    static class Holder {
        @BindView(R.id.dashboard_title)
        TextView mTitleText;

        Holder(View view) {
            ButterKnife.bind(this, view);
        }

        void bind(DutyType dutyType) {
            mTitleText.setText(dutyType.getName());
            mTitleText.setCompoundDrawablesWithIntrinsicBounds(dutyType.getIcon(), 0, 0, 0);
        }
    }
}
