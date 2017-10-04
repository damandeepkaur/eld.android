package com.bsmwireless.screens.logs;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
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
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.alerts.Type;
import com.bsmwireless.widgets.logs.LogsTitleView;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;
import com.bsmwireless.widgets.logs.calendar.CalendarLayout;
import com.bsmwireless.widgets.logs.graphview.GraphLayout;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.bsmwireless.widgets.logs.LogsTitleView.Type.EVENTS;
import static com.bsmwireless.widgets.logs.LogsTitleView.Type.LOG_HEADER;

public final class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogsHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_EVENTS_TITLE = 1;
    private static final int VIEW_TYPE_EVENTS_ITEM = 2;
    private static final int VIEW_TYPE_LOG_HEADER_TITLE = 3;
    private static final int VIEW_TYPE_LOG_HEADER_ITEM = 4;

    //header + logs + trip info titles
    private final int MIN_LIST_SIZE = 3;
    private LogsTitleView mEventsTitleView;
    private LogsTitleView mLogHeaderTitleView;
    private CalendarLayout mCalendarLayout;
    private GraphLayout mGraphLayout;
    private TextView mSignLogsheet;
    private View mSigned;
    private List<EventLogModel> mEventLogs = Collections.emptyList();
    private LogHeaderModel mLogHeader = new LogHeaderModel();
    private GraphModel mGraphModel = new GraphModel();
    private List<LogSheetHeader> mLogHeaders = Collections.emptyList();
    private View.OnClickListener mOnMenuClickListener;
    private RecyclerView.SmoothScroller mSmoothScroller;
    private Context mContext;
    private LogsPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private OnLogsStateChangeListener mOnLogsStateChangeListener;
    private AdapterColors mAdapterColors;
    private String mNoAddressLabel;
    private LayoutInflater mLayoutInflater;

    private HashMap<Integer, Integer> mColors = new HashMap<>();


    public LogsAdapter(Context context, LogsPresenter presenter,
                       OnLogsStateChangeListener snackBarClickListener) {
        mContext = context;
        mPresenter = presenter;
        mOnLogsStateChangeListener = snackBarClickListener;

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

        mAdapterColors = new AdapterColors(mContext);
        mNoAddressLabel = mContext.getResources().getString(R.string.no_address_available);
        mLayoutInflater = LayoutInflater.from(mContext);

        for (DutyType type : DutyType.values()) {
            mColors.put(type.getColor(), ContextCompat.getColor(context, type.getColor()));
        }
    }

    public void setEventLogs(List<EventLogModel> eventLogs) {
        mEventLogs = eventLogs;
        notifyDataSetChanged();
    }

    public void updateGraph(GraphModel graphModel) {
        mGraphModel = graphModel;
        if (mGraphLayout != null) {
            mGraphLayout.updateGraph(graphModel);
        }
    }

    public void setLogHeader(LogHeaderModel logHeaderModel) {
        mLogHeader = logHeaderModel;
        notifyDataSetChanged();
    }

    public void setLogSheetHeaders(List<LogSheetHeader> logHeaders) {
        mLogHeaders = logHeaders;
        if (mCalendarLayout != null) {
            mCalendarLayout.setLogs(logHeaders);
            updateSignButton();
        }
        notifyDataSetChanged();
    }

    private void updateSignButton() {
        CalendarItem calendarItem = mCalendarLayout.getCurrentItem();
        LogSheetHeader logSheetHeader = calendarItem.getAssociatedLogSheet();
        if (logSheetHeader != null && Boolean.TRUE.equals(logSheetHeader.getSigned())) {
            mSignLogsheet.setVisibility(GONE);
            mSigned.setVisibility(VISIBLE);
        } else {
            mSignLogsheet.setVisibility(VISIBLE);
            mSigned.setVisibility(GONE);
        }
    }

    @Override
    public LogsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = mLayoutInflater.inflate(R.layout.logs_list_item_header, parent, false);

                mCalendarLayout = view.findViewById(R.id.calendar);
                mCalendarLayout.setLogs(mLogHeaders);
                mCalendarLayout.setOnItemSelectedListener(calendarItem -> {
                    updateSignButton();
                    mPresenter.onCalendarDaySelected(calendarItem);
                });

                mSignLogsheet = view.findViewById(R.id.sign_logsheet);
                mSigned = view.findViewById(R.id.signed);

                mSignLogsheet.setOnClickListener(v -> mOnLogsStateChangeListener.onSignButtonClicked(
                        mCalendarLayout.getCurrentItem()));

                mGraphLayout = view.findViewById(R.id.graphic);
                mGraphLayout.updateGraph(mGraphModel);

                break;
            case VIEW_TYPE_EVENTS_TITLE:
                mEventsTitleView = new LogsTitleView(mContext);
                mEventsTitleView.setType(EVENTS);
                mEventsTitleView.setOnClickListener(v -> onTitleItemClicked((LogsTitleView) v));
                view = mEventsTitleView;
                break;
            case VIEW_TYPE_EVENTS_ITEM:
                view = mLayoutInflater.inflate(R.layout.logs_list_item_eld_event, parent, false);
                break;
            case VIEW_TYPE_LOG_HEADER_TITLE:
                mLogHeaderTitleView = new LogsTitleView(mContext);
                mLogHeaderTitleView.setType(LOG_HEADER);
                mLogHeaderTitleView.setOnClickListener(v -> onTitleItemClicked((LogsTitleView) v));
                view = mLogHeaderTitleView;
                break;
            default:
                view = mLayoutInflater.inflate(R.layout.logs_list_item_log_header, parent, false);
                break;
        }
        return new LogsHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(LogsHolder holder, int position) {
        int viewType = holder.getViewType();
        if (viewType == VIEW_TYPE_EVENTS_ITEM) {
            EventLogModel event = mEventLogs.get(position - 2);
            bindEventView(holder, event);
        } else if (viewType == VIEW_TYPE_LOG_HEADER_ITEM) {
            holder.bindLogHeaderView(mLogHeader);
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
            mOnLogsStateChangeListener.showTitle(titleView.getType());
            if (titleView.getType() == EVENTS) {
                mLogHeaderTitleView.collapse();
            } else {
                mEventsTitleView.collapse();
            }
        } else {
            titleView.collapse();
            mOnLogsStateChangeListener.hideTitle(titleView.getType());
        }

        notifyDataSetChanged();
        scrollToPosition(1);
    }

    public LogsTitleView.Type getExpandedLogsTitle() {
        LogsTitleView.Type expandedTitle = null;
        if (mLogHeaderTitleView != null && !mLogHeaderTitleView.isCollapsed()) {
            expandedTitle = LOG_HEADER;
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
        int tripInfoSize = ((mLogHeaderTitleView == null) || mLogHeaderTitleView.isCollapsed()) ? 0 : 1;
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
            return VIEW_TYPE_LOG_HEADER_TITLE;
        } else {
            return VIEW_TYPE_LOG_HEADER_ITEM;
        }
    }

    private void showPopupMenu(View anchorView, EventLogModel event) {
        PopupMenu popup = new PopupMenu(mContext, anchorView);
        popup.getMenuInflater().inflate(R.menu.menu_eld_event, popup.getMenu());

        if (!event.isActive() || !event.isDutyEvent()) {
            popup.getMenu().findItem(R.id.menu_edit).setEnabled(false);
        }
        if (!event.isActive() || !event.isDutyEvent() ||
                !DutyType.getDutyTypeByCode(event.getEventType(), event.getEventCode()).equals(DutyType.DRIVING)) {
            popup.getMenu().findItem(R.id.menu_assign).setEnabled(false);
        }
        if (!event.isActive() || !event.isDutyEvent() ||
                !ELDEvent.EventOrigin.DRIVER.getValue().equals(event.getEvent().getOrigin())) {
            popup.getMenu().findItem(R.id.menu_remove).setEnabled(false);
        }

        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_edit:
                    mPresenter.onEditEventClicked(event);
                    return true;
                case R.id.menu_remove:
                    mPresenter.onRemovedEventClicked(event);
                    return true;
                case R.id.menu_assign:
                    mPresenter.onReassignEventClicked(event);
                    return true;
            }
            return false;
        });

        popup.show();
    }

    public CalendarItem getCurrentItem() {
        return (mCalendarLayout != null) ? mCalendarLayout.getCurrentItem() : null;
    }

    public LogHeaderModel getLogHeaderModel() {
        return mLogHeader;
    }

    private void bindEventView(LogsHolder holder,
                               EventLogModel log) {
        String dayTime = DateUtils.convertTimeInMsToDayTime(log.getDriverTimezone(), log.getEventTime());
        String vehicleName = log.getVehicleName() != null ? String.valueOf(log.getVehicleName()) : "";
        String duration = log.getDuration() != null ? DateUtils.convertTimeInMsToDurationString(log.getDuration(), mContext) : "";
        String address = (log.getLocation() != null) ? log.getLocation() : mNoAddressLabel;

        holder.mEventTime.setText(dayTime);
        holder.mEventDuration.setText(duration);
        holder.mEventVehicleName.setText(vehicleName);
        holder.mAddress.setText(address);

        holder.mMenuButton.setTag(log);
        holder.mMenuButton.setOnClickListener(mOnMenuClickListener);

        Type currentDuty = log.getType();

        holder.mMenuButton.setVisibility(log.isDutyEvent() ? VISIBLE : GONE);

        holder.mEventStatus.setTextColor(mColors.get(currentDuty.getColor()));
        holder.mEventStatus.setText(currentDuty.getTitle());

        if (log.isActive()) {
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

    public interface OnLogsStateChangeListener {
        void showTitle(LogsTitleView.Type type);

        void hideTitle(LogsTitleView.Type type);

        void onSignButtonClicked(CalendarItem calendarItem);
    }

    static final class LogsHolder extends RecyclerView.ViewHolder {
        //event
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
        @BindView(R.id.menu_button)
        View mMenuButton;

        //log header
        @Nullable
        @BindView(R.id.timezone)
        TextInputEditText mTimezone;
        @Nullable
        @BindView(R.id.driver_name)
        TextInputEditText mDriverName;
        @Nullable
        @BindView(R.id.co_drivers_name)
        TextInputEditText mCoDriversName;
        @Nullable
        @BindView(R.id.vehicle_name)
        TextInputEditText mVehicleName;
        @Nullable
        @BindView(R.id.vehicle_license)
        TextInputEditText mVehicleLicense;
        @Nullable
        @BindView(R.id.start_odometer)
        TextInputEditText mStartOdometer;
        @Nullable
        @BindView(R.id.end_odometer)
        TextInputEditText mEndOdometer;
        @Nullable
        @BindView(R.id.distance_driven)
        TextInputEditText mDistanceDriven;
        @Nullable
        @BindView(R.id.carrier_name)
        TextInputEditText mCarrierName;
        @Nullable
        @BindView(R.id.home_terminal_name)
        TextInputEditText mHomeTerminalName;
        @Nullable
        @BindView(R.id.home_terminal_address)
        TextInputEditText mHomeTerminalAddress;
        @Nullable
        @BindView(R.id.trailers)
        TextInputEditText mTrailers;
        @Nullable
        @BindView(R.id.shipping_id)
        TextInputEditText mShippingId;
        @Nullable
        @BindView(R.id.driving_exemptions)
        TextInputEditText mDrivingExemptions;

        private int mViewType;

        LogsHolder(View itemView, int viewType) {
            super(itemView);
            mViewType = viewType;
            ButterKnife.bind(this, itemView);
        }

        int getViewType() {
            return mViewType;
        }

        private void bindLogHeaderView(LogHeaderModel logHeader) {
            mTimezone.setText(logHeader.getTimezone());
            mDriverName.setText(logHeader.getDriverName());
            mCoDriversName.setText(logHeader.getCoDriversName());
            mVehicleName.setText(logHeader.getVehicleName());
            mVehicleLicense.setText(logHeader.getVehicleLicense());
            mStartOdometer.setText(logHeader.getStartOdometer());
            mEndOdometer.setText(logHeader.getEndOdometer());
            mDistanceDriven.setText(logHeader.getDistanceDriven());
            mCarrierName.setText(logHeader.getCarrierName());
            mHomeTerminalName.setText(logHeader.getHomeTerminalName());
            mHomeTerminalAddress.setText(logHeader.getHomeTerminalAddress());
            mTrailers.setText(logHeader.getTrailers());
            mShippingId.setText(logHeader.getShippingId());
            mDrivingExemptions.setText(logHeader.getSelectedExemptions());
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
