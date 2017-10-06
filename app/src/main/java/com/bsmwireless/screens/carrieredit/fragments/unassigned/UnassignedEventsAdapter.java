package com.bsmwireless.screens.carrieredit.fragments.unassigned;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.alerts.Type;

import java.util.HashMap;
import java.util.List;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class UnassignedEventsAdapter extends RecyclerView.Adapter<UnassignedEventsAdapter.EventsHolder> {
    private static final int HEADER = 0;
    private static final int ITEM = 1;

    private List<EventLogModel> mEvents;
    private Context mContext;
    private AdapterColors mAdapterColors;
    private HashMap<Integer, Integer> mColors = new HashMap<>();
    private String mNoAddressLabel;
    private UnassignedEventsPresenter mPresenter;
    private String mVehicleName;
    private int mDriverId;

    public UnassignedEventsAdapter(Context context, UnassignedEventsPresenter presenter) {
        Timber.v("UnassignedEventsAdapter: ");
        mContext = context;
        mPresenter = presenter;
        mNoAddressLabel = mContext.getResources().getString(R.string.no_address_available);
        mAdapterColors = new AdapterColors(mContext);
        for (DutyType type : DutyType.values()) {
            mColors.put(type.getColor(), ContextCompat.getColor(context, type.getColor()));
        }
    }

    public void setEvents(List<EventLogModel> events) {
        Timber.v("setEvents: ");
        mEvents = events;
        notifyDataSetChanged();
    }

    public void removeEvent(int position) {
        Timber.v("removeEvent: %d", position);
        mEvents.remove(position - 1);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position - 1, mEvents.size() + 1);
    }

    public void setVehicleName(String vehicleName) {
        Timber.v("setVehicleName: %s", vehicleName);
        mVehicleName = vehicleName;
        notifyItemChanged(HEADER);
    }

    public void setDriverId(int driverId) {
        Timber.v("setDriverId: %d", driverId);
        mDriverId = driverId;
    }

    @Override
    public EventsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case HEADER:
                view = LayoutInflater.from(mContext).inflate(R.layout.unassigned_events_header, parent, false);
                break;
            case ITEM:
                view = LayoutInflater.from(mContext).inflate(R.layout.unassigned_event_item, parent, false);
                break;
        }
        return new EventsHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER : ITEM;
    }

    @Override
    public void onBindViewHolder(EventsHolder holder, int position) {
        int viewType = holder.getViewType();
        if (viewType == HEADER) {
            if (!TextUtils.isEmpty(mVehicleName) && mEvents.size() > 0) {
                holder.mUnassignedTopHeader.setText(String.format("%s %s %d %s",
                        mVehicleName,
                        mContext.getString(R.string.has),
                        mEvents.size(),
                        mContext.getString(R.string.unassigned_driving)));
            }
        } else {
            EventLogModel event = mEvents.get(position - 1);
            bindEventView(holder, event, position);
        }
    }

    @Override
    public int getItemCount() {
        return mEvents == null || mEvents.isEmpty() ? 0 : mEvents.size() + 1;
    }

    private void bindEventView(EventsHolder holder,
                               EventLogModel model, int position) {
        String dayTime = DateUtils.convertTimeInMsToDayTime(model.getDriverTimezone(), model.getEventTime());
        String vehicleName = model.getVehicleName() != null ? String.valueOf(model.getVehicleName()) : "";
        String duration = model.getDuration() != null ? DateUtils.convertTimeInMsToDurationString(model.getDuration(), mContext) : "";
        String address = (model.getLocation() != null) ? model.getLocation() : mNoAddressLabel;

        holder.mEventTime.setText(dayTime);
        holder.mEventDuration.setText(duration);
        holder.mEventVehicleName.setText(vehicleName);
        holder.mAddress.setText(address);

        holder.mAccept.setOnClickListener(v -> mPresenter.acceptEvent(model, mDriverId, position));
        holder.mReject.setOnClickListener(v -> mPresenter.rejectEvent(model, position));

        Type currentDuty = model.getType();

        holder.mEventStatus.setTextColor(mColors.get(currentDuty.getColor()));
        holder.mEventStatus.setText(currentDuty.getTitle());

        if (model.isActive()) {
            holder.itemView.setBackgroundColor(mAdapterColors.mTransparentColor);
            holder.mEventChanged.setVisibility(GONE);
            holder.mEventTime.setTextColor(mAdapterColors.mPrimaryTextColor);
            holder.mEventDuration.setTextColor(mAdapterColors.mPrimaryTextColor);
            holder.mEventVehicleName.setTextColor(mAdapterColors.mSecondaryTextColor);
            holder.mAddress.setTextColor(mAdapterColors.mSecondaryTextColor);
        } else {
            holder.itemView.setBackgroundColor(mAdapterColors.mBackgroundColor);
            holder.mEventChanged.setVisibility(VISIBLE);
            holder.mEventStatus.setTextColor(mAdapterColors.mLightGrayColor);
            holder.mEventTime.setTextColor(mAdapterColors.mLightGrayColor);
            holder.mEventDuration.setTextColor(mAdapterColors.mLightGrayColor);
            holder.mEventVehicleName.setTextColor(mAdapterColors.mLightGrayColor);
            holder.mAddress.setTextColor(mAdapterColors.mLightGrayColor);
        }
    }

    static final class EventsHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.event_changed)
        TextView mEventChanged;
        @Nullable
        @BindView(R.id.event_status)
        TextView mEventStatus;
        @Nullable
        @BindView(R.id.event_time)
        TextView mEventTime;
        @Nullable
        @BindView(R.id.event_duration)
        TextView mEventDuration;
        @Nullable
        @BindView(R.id.event_vehicle_name)
        TextView mEventVehicleName;
        @Nullable
        @BindView(R.id.address)
        TextView mAddress;
        @Nullable
        @BindView(R.id.event_accept)
        View mAccept;
        @Nullable
        @BindView(R.id.event_reject)
        View mReject;

        @Nullable
        @BindView(R.id.unassigned_events_top_header)
        TextView mUnassignedTopHeader;

        private int mViewType;

        public EventsHolder(View itemView, int viewType) {
            super(itemView);
            mViewType = viewType;
            ButterKnife.bind(this, itemView);
        }

        int getViewType() {
            return mViewType;
        }
    }

    private static class AdapterColors {
        private int mPrimaryTextColor;
        private int mSecondaryTextColor;
        private int mTransparentColor;
        private int mLightGrayColor;
        private int mBackgroundColor;

        private AdapterColors(Context context) {
            mPrimaryTextColor = ContextCompat.getColor(context, R.color.primary_text);
            mSecondaryTextColor = ContextCompat.getColor(context, R.color.secondary_text);
            mTransparentColor = ContextCompat.getColor(context, android.R.color.transparent);
            mLightGrayColor = ContextCompat.getColor(context, R.color.light_gray_color);
            mBackgroundColor = ContextCompat.getColor(context, R.color.black_5);
        }
    }
}
