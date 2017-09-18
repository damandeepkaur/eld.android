package com.bsmwireless.screens.diagnostic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.models.ELDEvent;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public final class DiagnosticEventsAdapter extends RecyclerView.Adapter<DiagnosticEventsAdapter.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private String mTimezone;
    private List<ELDEvent> mEldEvents;

    public DiagnosticEventsAdapter(@NonNull LayoutInflater layoutInflater, @NonNull Context context) {
        mLayoutInflater = layoutInflater;
        mContext = context;
        mEldEvents = Collections.emptyList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.view_diagnostic_event, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ELDEvent event = mEldEvents.get(position);
        holder.mEventCode.setText(event.getMalCode().getCode());
        holder.mEventTitle.setText(mContext.getString(event.getMalCode().getStringRes()));
        Date date = new Date(event.getEventTime());
        holder.mEventDate.setText(DateUtils.convertToFullTime(mTimezone, date));
    }

    @Override
    public int getItemCount() {
        return mEldEvents.size();
    }

    public void setItems(List<ELDEvent> events) {
        mEldEvents = events;
    }

    public void setTimeZone(String timeZone) {
        this.mTimezone = timeZone;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.even_icon)
        TextView mEventCode;
        @BindView(R.id.event_title)
        TextView mEventTitle;
        @BindView(R.id.event_time)
        TextView mEventDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
