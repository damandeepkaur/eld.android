package com.bsmwireless.widgets.calendar;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmwireless.models.LogSheetHeader;

import java.util.List;

import app.bsmuniversal.com.R;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private Context mContext;

    private List<CalendarItem> mItems;

    private OnItemSelectListener mListener;

    private int mSelectedPosition = -1;

    public interface OnItemSelectListener {
        void onItemSelected(CalendarItem log);
    }

    public CalendarAdapter(Context context, List<CalendarItem> items) {
        mItems = items;
        mContext = context;
    }

    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CalendarAdapter.ViewHolder holder, int position) {
        CalendarItem item = mItems.get(position);

        holder.mDayOfMonthTV.setText(String.valueOf(item.getDay()));
        holder.mDayOfWeekTV.setText(item.getDayOfWeek().substring(0, 3).toUpperCase());
        holder.itemView.setSelected(mSelectedPosition == position);

        if (item.getAssociatedLog() != null) {
            holder.itemView.setOnClickListener(v -> {
                if (mSelectedPosition != position) {
                    int prevSelected = mSelectedPosition;
                    mSelectedPosition = position;
                    holder.itemView.setSelected(true);
                    notifyItemChanged(prevSelected);
                }

                if (mListener != null) {
                    mListener.onItemSelected(item);
                }
            });
            holder.mDayOfMonthTV.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
            holder.mDayOfWeekTV.setTextColor(ContextCompat.getColor(mContext, R.color.secondary_text));
        } else {
            holder.itemView.setOnClickListener(null);
            holder.mDayOfMonthTV.setTextColor(ContextCompat.getColor(mContext, R.color.disabled_calendar_item_day));
            holder.mDayOfWeekTV.setTextColor(ContextCompat.getColor(mContext, R.color.disabled_calendar_item_week));
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemSelectedListener(OnItemSelectListener listener) {
        mListener = listener;
    }

    public void updateLogs(List<LogSheetHeader> logs) {
        for (LogSheetHeader log:
             logs) {
            CalendarItem item = findItemByDate(log.getLogDay());
            if (item != null) {
                item.setAssociatedLog(log);
            }
        }
        notifyDataSetChanged();
    }

    private CalendarItem findItemByDate(Long date) {
        for (CalendarItem item:
             mItems) {
            if (item.isDateValid(date)) {
                return item;
            }
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mDayOfWeekTV;
        private TextView mDayOfMonthTV;

        public ViewHolder(View itemView) {
            super(itemView);
            mDayOfWeekTV = (TextView) itemView.findViewById(R.id.day_of_week);
            mDayOfMonthTV = (TextView) itemView.findViewById(R.id.day_of_month);
        }
    }
}
