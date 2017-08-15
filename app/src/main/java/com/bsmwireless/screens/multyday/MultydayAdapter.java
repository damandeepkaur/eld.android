package com.bsmwireless.screens.multyday;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MultydayAdapter extends RecyclerView.Adapter<MultydayAdapter.ViewHolder> {

    private List<MultydayItemModel> mItems;

    public MultydayAdapter(List<MultydayItemModel> items) {
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multyday_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MultydayItemModel item = mItems.get(position);

        holder.mDrivingTime.setText(item.getTotalDriving());
        holder.mOnDutyTime.setText(item.getTotalOnDuty());
        holder.mOffDutyTime.setText(item.getTotalOffDuty());
        holder.mSleepingTime.setText(item.getTotalSleeping());
        holder.mItemDay.setText(item.getDay());
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public void updateItems(List<MultydayItemModel> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.total_on_duty_time)
        TextView mOnDutyTime;
        @BindView(R.id.total_driving_time)
        TextView mDrivingTime;
        @BindView(R.id.total_sleeping_time)
        TextView mSleepingTime;
        @BindView(R.id.total_off_duty_time)
        TextView mOffDutyTime;
        @BindView(R.id.item_day)
        TextView mItemDay;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
