package com.bsmwireless.screens.carrieredit.fragments.edited;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmwireless.common.utils.DateUtils;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.screens.logs.GraphModel;
import com.bsmwireless.screens.logs.LogHeaderModel;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.alerts.DutyType;
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
import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.bsmwireless.widgets.logs.LogsTitleView.Type.EVENTS;
import static com.bsmwireless.widgets.logs.LogsTitleView.Type.LOG_HEADER;

public class EditedEventsAdapter extends RecyclerView.Adapter<EditedEventsAdapter.LogsHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_EVENTS_TITLE = 1;
    private static final int VIEW_TYPE_EVENTS_ITEM = 2;
    private static final int VIEW_TYPE_LOG_HEADER_TITLE = 3;
    private static final int VIEW_TYPE_LOG_HEADER_ITEM = 4;

    //header + logs + trip info titles
    private final int MIN_LIST_SIZE = 3;

    private CalendarLayout mCalendarLayout;
    private LayoutInflater mLayoutInflater;
    private GraphLayout mGraphLayout;
    private List<LogSheetHeader> mLogHeaders = Collections.emptyList();
    private LogsTitleView mEventsTitleView;
    private LogsTitleView mLogHeaderTitleView;
    private Context mContext;
    private final RecyclerView.SmoothScroller mSmoothScroller;
    private final RecyclerView mRecyclerView;
    private List<EventLogModel> mEventLogs = Collections.emptyList();
    private String mNoAddressLabel;
    private AdapterColors mAdapterColors;
    private HashMap<Integer, Integer> mColors = new HashMap<>();
    private LogHeaderModel mLogHeader = new LogHeaderModel();
    private View.OnClickListener mOnMenuClickListener;
    private GraphModel mGraphModel;
    private View mEditedEventsTopHeader;
    private final EditedEventsPresenter mPresenter;

    public EditedEventsAdapter(Context context, RecyclerView recyclerView, EditedEventsPresenter presenter) {
        mContext = context;
        mRecyclerView = recyclerView;
        mPresenter = presenter;
        mAdapterColors = new AdapterColors(mContext);
        mNoAddressLabel = mContext.getResources().getString(R.string.no_address_available);
        mLayoutInflater = LayoutInflater.from(mContext);

        mOnMenuClickListener = view -> {
            EventLogModel log = (EventLogModel) view.getTag();
        };

        mSmoothScroller = new LinearSmoothScroller(mContext) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        for (DutyType type : DutyType.values()) {
            mColors.put(type.getColor(), ContextCompat.getColor(context, type.getColor()));
        }
    }

    public void setEvents(List<EventLogModel> events) {
        Timber.v("setEvents: ");
        mEventLogs = events;
        TextView eventsTitle = ButterKnife.findById(mEventsTitleView, R.id.list_item_title);
        int resId = 0;
        if (!mEventLogs.isEmpty()) {
            resId = R.drawable.red_dot;
        }
        eventsTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
        notifyDataSetChanged();
    }

    public void setLogHeader(LogHeaderModel logHeaderModel) {
        mLogHeader = logHeaderModel;
        notifyDataSetChanged();
    }

    public void updateCalendarItems(List<CalendarItem> calendarItems) {
        mCalendarLayout.setAllItems(calendarItems);
    }

    @Override
    public LogsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = mLayoutInflater.inflate(R.layout.edited_events_list_item_header, parent, false);
                mCalendarLayout = view.findViewById(R.id.calendar);
                mCalendarLayout.setLogs(mLogHeaders);
                mCalendarLayout.setOnItemSelectedListener(calendarItem -> {
                    mPresenter.onCalendarDaySelected(calendarItem);
                });
                mPresenter.markCalendarItems(mCalendarLayout.getAllItems());
                mGraphLayout = view.findViewById(R.id.graphic);
                //mGraphLayout.setELDEvents(mEventLogs);
                mEditedEventsTopHeader = ButterKnife.findById(view, R.id.top_header_container);
                break;
            case VIEW_TYPE_EVENTS_TITLE:
                mEventsTitleView = new LogsTitleView(mContext);
                mEventsTitleView.setType(EVENTS);
                mEventsTitleView.setOnClickListener(v -> onTitleItemClicked((LogsTitleView) v));
                view = mEventsTitleView;
                break;
            case VIEW_TYPE_EVENTS_ITEM:
                view = mLayoutInflater.inflate(R.layout.edited_list_item_eld_event, parent, false);
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
        } else if (viewType == VIEW_TYPE_HEADER) {
            TextView editedEventsCount = ButterKnife.findById(mEditedEventsTopHeader, R.id.edited_events_top_header);
            if (mEventLogs.size() > 0) {
                mEditedEventsTopHeader.setVisibility(VISIBLE);
                editedEventsCount.setText(Integer.toString(mEventLogs.size())
                        .concat(" ")
                        .concat(mContext.getString(R.string.logsheet_edited)));
            } else {
                mEditedEventsTopHeader.setVisibility(GONE);
            }
            holder.mApprove.setOnClickListener(v -> mPresenter.approveEdits(mEventLogs,
                    mCalendarLayout.getCurrentItem().getLogDay()));
            holder.mDisapprove.setOnClickListener(v -> mPresenter.disapproveEdits(mEventLogs,
                    mCalendarLayout.getCurrentItem().getLogDay()));
        }
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

    public void updateGraph(GraphModel graphModel) {
        mGraphModel = graphModel;
        if (mGraphLayout != null) {
            mGraphLayout.updateGraph(graphModel);
        }
    }

    public void setLogSheetHeaders(List<LogSheetHeader> logHeaders) {
        mLogHeaders = logHeaders;
        if (mCalendarLayout != null) {
            mCalendarLayout.setLogs(logHeaders);
        }
        notifyDataSetChanged();
    }

    private void onTitleItemClicked(LogsTitleView titleView) {
        if (titleView.isCollapsed()) {
            titleView.expand();
            if (titleView.getType() == EVENTS) {
                mLogHeaderTitleView.collapse();
            } else {
                mEventsTitleView.collapse();
            }
        } else {
            titleView.collapse();
        }

        notifyDataSetChanged();
        scrollToPosition(1);
    }

    private void scrollToPosition(int position) {
        mSmoothScroller.setTargetPosition(position);
        mRecyclerView.getLayoutManager().startSmoothScroll(mSmoothScroller);
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

        DutyType currentDuty = log.getDutyType();

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

        @Nullable
        @BindView(R.id.edited_events_disapprove)
        View mDisapprove;

        @Nullable
        @BindView(R.id.edited_events_approve)
        View mApprove;

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
