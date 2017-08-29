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

import java.util.ArrayList;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseMenuAdapter extends ArrayAdapter<BaseMenuAdapter.DutyItem> {
    private ArrayList<DutyItem> mDutyItems = new ArrayList<>();

    BaseMenuAdapter(@NonNull Context context, @NonNull ArrayList<DutyItem> dutyItems) {
        super(context, R.layout.view_item_dashboard, dutyItems);
        mDutyItems = dutyItems;
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
        holder.bind(mDutyItems.get(position));

        return convertView;
    }

    static class Holder {
        @BindView(R.id.dashboard_title)
        TextView mTitleText;

        Holder(View view) {
            ButterKnife.bind(this, view);
        }

        void bind(DutyItem dutyItem) {
            mTitleText.setText(dutyItem.getDutyType().getName());
            mTitleText.setCompoundDrawablesWithIntrinsicBounds(dutyItem.getDutyType().getIcon(), 0, 0, 0);
            mTitleText.setEnabled(dutyItem.isEnabled());
            mTitleText.setFocusable(!dutyItem.isEnabled());
            mTitleText.setFocusableInTouchMode(!dutyItem.isEnabled());
        }
    }

    public static class DutyItem {
        private DutyType mDutyType;
        private boolean mIsEnabled;

        public DutyItem(DutyType dutyType, boolean isEnabled) {
            mDutyType = dutyType;
            mIsEnabled = isEnabled;
        }

        public DutyType getDutyType() {
            return mDutyType;
        }

        public boolean isEnabled() {
            return mIsEnabled;
        }
    }
}
