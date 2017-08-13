package com.bsmwireless.screens.logs;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.logs.DutyColors;
import com.bsmwireless.widgets.logs.EventDescription;
import com.bsmwireless.widgets.logs.LogsTitleView;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;
import com.bsmwireless.widgets.logs.calendar.CalendarLayout;
import com.bsmwireless.widgets.logs.graphview.GraphLayout;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bsmwireless.widgets.logs.LogsTitleView.Type.EVENTS;
import static com.bsmwireless.widgets.logs.LogsTitleView.Type.TRIP_INFO;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogsHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_EVENTS_TITLE = 1;
    private static final int VIEW_TYPE_EVENTS_ITEM = 2;
    private static final int VIEW_TYPE_TRIP_INFO_TITLE = 3;
    private static final int VIEW_TYPE_TRIP_INFO_ITEM = 4;

    //header + logs + trip info titles
    private final int MIN_LIST_SIZE = 3;

    private LogsTitleView mEventsTitleView;
    private LogsTitleView mTripInfoTitleView;
    private CalendarLayout mCalendarLayout;
    private GraphLayout mGraphLayout;
    private View mSignLogsheet;

    private List<EventLogModel> mEventLogs = new ArrayList<>();
    private TripInfoModel mTripInfo = new TripInfoModel();
    private List<LogSheetHeader> mLogHeaders = new ArrayList<>();
    private View.OnClickListener mOnMenuClickListener;
    private RecyclerView.SmoothScroller mSmoothScroller;
    private Context mContext;
    private LogsPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private OnLogsTitleStateChangeListener mOnLogsTitleStateChangeListener;
    private DutyColors mDutyColors;
    private String mNoAddressLabel;


    public LogsAdapter(Context context, LogsPresenter presenter, OnLogsTitleStateChangeListener snackBarClickListener) {
        mContext = context;
        mPresenter = presenter;
        mOnLogsTitleStateChangeListener = snackBarClickListener;

        mOnMenuClickListener = view -> {
            EventLogModel log = (EventLogModel) view.getTag();
            showPopupMenu(view, log);
        };

        mSmoothScroller = new LinearSmoothScroller(mContext) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        mDutyColors = new DutyColors(mContext);
        mNoAddressLabel = mContext.getResources().getString(R.string.no_address_available);
    }

    public void setEventLogs(List<EventLogModel> eventLogs) {
        mEventLogs = eventLogs;
        if (mGraphLayout != null) {
            mGraphLayout.setELDEvents(eventLogs, mTripInfo.getStartDayTime());
        }
        notifyDataSetChanged();
    }

    public void setTripInfo(TripInfoModel tripInfo) {
        if (mGraphLayout != null) {
            mGraphLayout.setHOSTimerSleeperBerth(tripInfo.getSleeperBerthTime());
            mGraphLayout.setHOSTimerDriving(tripInfo.getDrivingTime());
            mGraphLayout.setHOSTimerOffDuty(tripInfo.getOffDutyTime());
            mGraphLayout.setHOSTimerOnDuty(tripInfo.getOnDutyTime());
        }
        mTripInfo = tripInfo;
        notifyDataSetChanged();
    }

    public void setLogSheetHeaders(List<LogSheetHeader> logHeaders) {
        mLogHeaders = logHeaders;
        if (mCalendarLayout != null) {
            mCalendarLayout.setLogs(logHeaders);
        }
        notifyDataSetChanged();
    }

    @Override
    public LogsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = layoutInflater.inflate(R.layout.logs_list_item_header, parent, false);

                mSignLogsheet = view.findViewById(R.id.sign_logsheet);
                mSignLogsheet.setOnClickListener(v -> mPresenter.onSignLogsheetButtonClicked());

                mCalendarLayout = (CalendarLayout) view.findViewById(R.id.calendar);
                mCalendarLayout.setLogs(mLogHeaders);
                mCalendarLayout.setOnItemSelectedListener(calendarItem -> mPresenter.onCalendarDaySelected(calendarItem));

                mGraphLayout = (GraphLayout) view.findViewById(R.id.graphic);
                mGraphLayout.setELDEvents(mEventLogs, mTripInfo.getStartDayTime());

                break;
            case VIEW_TYPE_EVENTS_TITLE:
                mEventsTitleView = new LogsTitleView(mContext);
                mEventsTitleView.setType(EVENTS);
                mEventsTitleView.setOnClickListener(v -> onTitleItemClicked((LogsTitleView) v));
                view = mEventsTitleView;
                break;
            case VIEW_TYPE_EVENTS_ITEM:
                view = layoutInflater.inflate(R.layout.logs_list_item_eld_event, parent, false);
                break;
            case VIEW_TYPE_TRIP_INFO_TITLE:
                mTripInfoTitleView = new LogsTitleView(mContext);
                mTripInfoTitleView.setType(TRIP_INFO);
                mTripInfoTitleView.setOnClickListener(v -> onTitleItemClicked((LogsTitleView) v));
                view = mTripInfoTitleView;
                break;
            default:
                view = layoutInflater.inflate(R.layout.logs_list_item_trip_info, parent, false);
                break;
        }
        return new LogsHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(LogsHolder holder, int position) {
        int viewType = holder.getViewType();
        if (viewType == VIEW_TYPE_EVENTS_ITEM) {
            EventLogModel event = mEventLogs.get(position - 2);
            String duration = event.getDuration() != null ? DateUtils.convertTimeInMsToDurationString(event.getDuration(), mContext) : "";
            String address = (event.getLocation() != null) ? event.getLocation() : mNoAddressLabel;
            holder.bindEventView(event, mOnMenuClickListener, mDutyColors.getColor(event.getEventType(), event.getEventCode()), duration, address);
        } else if (viewType == VIEW_TYPE_TRIP_INFO_ITEM) {
            String unit = mContext.getString(mTripInfo.getUnitType() == TripInfoModel.UnitType.KM ? R.string.km : R.string.ml);
            holder.bindTripInfoView(mTripInfo, mContext.getString(R.string.odometer, unit));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    private void onTitleItemClicked(LogsTitleView titleView) {
        if (titleView.isCollapsed()) {
            titleView.expand();
            mOnLogsTitleStateChangeListener.show(titleView.getType());
            if (titleView.getType() == EVENTS) {
                mTripInfoTitleView.collapse();
            } else {
                mEventsTitleView.collapse();
            }
        } else {
            titleView.collapse();
            mOnLogsTitleStateChangeListener.hide();
        }

        notifyDataSetChanged();
        scrollToPosition(1);
    }

    public LogsTitleView.Type getExpandedLogsTitle() {
        LogsTitleView.Type expandedTitle = null;
        if (mTripInfoTitleView != null && !mTripInfoTitleView.isCollapsed()) {
            expandedTitle = TRIP_INFO;
        } else if (mEventsTitleView != null && !mEventsTitleView.isCollapsed()) {
            expandedTitle = EVENTS;
        }
        return expandedTitle;
    }

    private void scrollToPosition(int position) {
        mSmoothScroller.setTargetPosition(position);
        mRecyclerView.getLayoutManager().startSmoothScroll(mSmoothScroller);
    }

    @Override
    public int getItemCount() {
        int eventsSize = (mEventsTitleView == null || mEventsTitleView.isCollapsed()) ? 0 : mEventLogs.size();
        int tripInfoSize = ((mTripInfoTitleView == null) || mTripInfoTitleView.isCollapsed()) ? 0 : 1;
        return MIN_LIST_SIZE + eventsSize + tripInfoSize;

    }

    @Override
    public int getItemViewType(int position) {
        int eventsSize = (mEventsTitleView == null || mEventsTitleView.isCollapsed()) ? 0 : mEventLogs.size();
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else if (position == 1) {
            return VIEW_TYPE_EVENTS_TITLE;
        } else if (position > 1 && position < eventsSize + 2) {
            return VIEW_TYPE_EVENTS_ITEM;
        } else if (position == (eventsSize + 2)) {
            return VIEW_TYPE_TRIP_INFO_TITLE;
        } else {
            return VIEW_TYPE_TRIP_INFO_ITEM;
        }
    }

    private void showPopupMenu(View anchorView, EventLogModel event) {
        PopupMenu popup = new PopupMenu(mContext, anchorView);
        popup.getMenuInflater().inflate(R.menu.menu_eld_event, popup.getMenu());

        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_edit:
                    mPresenter.onEditEventClicked(event);
                    return true;
                case R.id.menu_remove:
                    mPresenter.onRemovedEventClicked(event);
                    return true;
            }
            return false;
        });

        popup.show();
    }

    public CalendarItem getCurrentItem() {
        return mCalendarLayout.getCurrentItme();
    }

    public interface OnLogsTitleStateChangeListener {
        void show(LogsTitleView.Type type);

        void hide();
    }

    static class LogsHolder extends RecyclerView.ViewHolder {
        //event
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
        @BindView(R.id.vehicle_name)
        TextView mVehicleName;
        @Nullable
        @BindView(R.id.address)
        TextView mAddress;
        @Nullable
        @BindView(R.id.menu_button)
        View mMenuButton;

        //trip info
        @Nullable
        @BindView(R.id.co_driver)
        TextView mCoDriverValue;
        @Nullable
        @BindView(R.id.on_duty_left)
        TextView mOnDutyLeftValue;
        @Nullable
        @BindView(R.id.drive)
        TextView mDriveValue;
        @Nullable
        @BindView(R.id.odometer)
        TextView mOdometer;
        @Nullable
        @BindView(R.id.odometer_value)
        TextView mOdometerValue;
        private int mViewType;

        LogsHolder(View itemView, int viewType) {
            super(itemView);
            mViewType = viewType;
            ButterKnife.bind(this, itemView);
        }

        int getViewType() {
            return mViewType;
        }

        private void bindEventView(EventLogModel log, View.OnClickListener menuClickListener,
                                   int color, String duration, String address) {

            mEventStatus.setText(EventDescription.getTitle(log.getEventType(), log.getEventCode()));
            mEventStatus.setTextColor(color);
            String dayTime = DateUtils.convertTimeInMsToDayTime(log.getDriverTimezone(), log.getEventTime());
            mEventTime.setText(dayTime);

            mEventDuration.setText(duration);

            //TODO: probably we need vehicle name here
            String vehicleId = log.getVehicleName() != null ? String.valueOf(log.getVehicleName()) : "";
            mVehicleName.setText(vehicleId);
            mAddress.setText(address);

            if (log.getEventType() == ELDEvent.EventType.DUTY_STATUS_CHANGING.getValue()) {
                mMenuButton.setVisibility(View.VISIBLE);
                mMenuButton.setTag(log);
                mMenuButton.setOnClickListener(menuClickListener);
            } else {
                mMenuButton.setVisibility(View.INVISIBLE);
            }
        }

        private void bindTripInfoView(TripInfoModel tripInfo, String odometerTitle) {
            mCoDriverValue.setText(tripInfo.getCoDriverValue());
            mOnDutyLeftValue.setText(tripInfo.getOnDutyTime());
            mDriveValue.setText(tripInfo.getDrivingTime());
            mOdometer.setText(odometerTitle);
            mOdometerValue.setText(String.valueOf(tripInfo.getOdometerValue()));
        }
    }

}
