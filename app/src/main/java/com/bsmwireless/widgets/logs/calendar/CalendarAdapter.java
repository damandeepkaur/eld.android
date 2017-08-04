package com.bsmwireless.widgets.logs.calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmwireless.models.LogSheetHeader;

import java.util.List;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private Context mContext;

    private List<CalendarItem> mItems;

    private int mSelectedPosition = 0;

    private View.OnClickListener mOnClickListener;

    public CalendarAdapter(Context context, List<CalendarItem> items, View.OnClickListener onClickListener) {
        mItems = items;
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CalendarAdapter.ViewHolder holder, int position) {
        CalendarItem item = mItems.get(position);

        holder.mDayOfMonthTV.setText(item.getDay());
        holder.mDayOfWeekTV.setText(item.getDayOfWeek());
        holder.itemView.setSelected(mSelectedPosition == position);

        holder.itemView.setOnClickListener(mOnClickListener);

        LogSheetHeader associatedLog = item.getAssociatedLog();

        if (associatedLog != null) {
            if (associatedLog.getSigned() == (Boolean)true) {
                holder.mDayOfMonthTV.setTextColor(ContextCompat.getColor(mContext, R.color.nasty_green));
                holder.mDayOfMonthTV.setTypeface(null, Typeface.BOLD);
            } else {
                holder.mDayOfMonthTV.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
                holder.mDayOfMonthTV.setTypeface(null, Typeface.NORMAL);
            }
            holder.mDayOfWeekTV.setTextColor(ContextCompat.getColor(mContext, R.color.secondary_text));
        } else {
            holder.mDayOfMonthTV.setTextColor(ContextCompat.getColor(mContext, R.color.disabled_calendar_item_day));
            holder.mDayOfWeekTV.setTextColor(ContextCompat.getColor(mContext, R.color.disabled_calendar_item_week));
            holder.mDayOfMonthTV.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public void setSelectedItem(int position) {
        if (mSelectedPosition != position) {
            int prevPosition = mSelectedPosition;
            mSelectedPosition = position;
            notifyItemChanged(prevPosition);
            notifyItemChanged(mSelectedPosition);
        }
    }

    public void updateLogs(List<LogSheetHeader> logs) {
        if (logs != null) {
            for (LogSheetHeader log : logs) {
                CalendarItem item = findItemByDate(log.getLogDay());
                if (item != null) {
                    item.setAssociatedLog(log);
                }
            }
            notifyDataSetChanged();
        }
    }

    public CalendarItem getItemByPosition(int position) {
        return mItems != null && position < mItems.size() ? mItems.get(position) : null;
    }

    private CalendarItem findItemByDate(Long date) {
        for (CalendarItem item : mItems) {
            if (item.isCurrentDay(date)) {
                return item;
            }
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.day_of_week)
        TextView mDayOfWeekTV;
        @BindView(R.id.day_of_month)
        TextView mDayOfMonthTV;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
