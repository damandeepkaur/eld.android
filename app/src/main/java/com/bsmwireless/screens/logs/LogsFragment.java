package com.bsmwireless.screens.logs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.logs.dagger.DaggerLogsComponent;
import com.bsmwireless.screens.logs.dagger.LogsModule;
import com.bsmwireless.screens.navigation.NavigateView;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.logs.LogsTitleView;
import com.bsmwireless.widgets.logs.calendar.CalendarLayout;
import com.bsmwireless.widgets.logs.graphview.GraphLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bsmwireless.widgets.logs.LogsTitleView.Type.EVENTS;
import static com.bsmwireless.widgets.logs.LogsTitleView.Type.TRIP_INFO;

public class LogsFragment extends BaseFragment implements LogsView {

    @Inject
    LogsPresenter mPresenter;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private LogsAdapter mAdapter;
    private Unbinder mUnbinder;
    private NavigateView mNavigateView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigateView) {
            mNavigateView = (NavigateView) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NavigateView");
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && mAdapter != null) {
            mAdapter.showSnackBar();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        DaggerLogsComponent.builder().appComponent(App.getComponent()).logsModule(new LogsModule(this)).build().inject(this);

        mAdapter = new LogsAdapter(mContext);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(mAdapter);

        mPresenter.onViewCreated();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mPresenter.onDestroy();
    }

    @Override
    public void setELDEvents(List<ELDEvent> events) {
        mAdapter.setELDEvents(events);
    }

    @Override
    public void setTripInfo(TripInfo tripInfo) {
        mAdapter.setTripInfo(tripInfo);
    }

    @Override
    public void setLogSheetHeaders(List<LogSheetHeader> logs) {
        mAdapter.setLogSheetHeaders(logs);
    }

    @Override
    public void goToAddEventScreen() {
        //TODO: go to add event screen
        Toast.makeText(mContext, "Go to add event screen", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goToEditEventScreen(ELDEvent event) {
        //TODO: go to edit event screen
        Toast.makeText(mContext, "Go to edit event screen", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goToEditTripInfoScreen() {
        //TODO: go to edit trip info screen
        Toast.makeText(mContext, "Go to edit trip info screen", Toast.LENGTH_SHORT).show();
    }

    private void showPopupMenu(View anchorView, ELDEvent event) {
        PopupMenu popup = new PopupMenu(mContext, anchorView);
        popup.getMenuInflater().inflate(R.menu.menu_eld_event, popup.getMenu());

        //TODO: Add getting delete property from event data (can event be deleted or not)
        boolean isDeletable = false;
        popup.getMenu().getItem(1).setEnabled(isDeletable);

        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_edit:
                    mPresenter.onEditEventClicked(event);
                    return true;
                case R.id.menu_remove:
                    mPresenter.onDeleteEventClicked(event);
                    return true;
            }
            return false;
        });

        popup.show();
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
        private DateFormat mDateFormatter;
        private int mViewType;

        LogsHolder(View itemView, int viewType) {
            super(itemView);
            mViewType = viewType;
            ButterKnife.bind(this, itemView);

            mDateFormatter = new SimpleDateFormat("HH:mm:ss");
        }

        int getViewType() {
            return mViewType;
        }

        private void bindEventView(ELDEvent event, View.OnClickListener menuClickListener, Context context) {

            //TODO: change to data from event
            event.getEventCode();

            mEventStatus.setText(DutyType.getNameById(event.getEventCode()));

            int color = ContextCompat.getColor(context, DutyType.getColorById(event.getEventCode()));
            mEventStatus.setTextColor(color);

            String dateFormatted = mDateFormatter.format(new Date(event.getEventTime()));
            mEventTime.setText(dateFormatted);

            //TODO: find how we should calculate duration
            mEventDuration.setText("0 hrs 0 mins");

            //TODO: probably we need vehicle name here
            String vehicleId = event.getVehicleId() != null ? String.valueOf(event.getVehicleId()) : "";
            mVehicleName.setText(vehicleId);

            mAddress.setText(event.getLocation());

            mMenuButton.setTag(event);
            mMenuButton.setOnClickListener(menuClickListener);
        }

        private void bindTripInfoView(TripInfo tripInfo, String odometerTitle) {
            mCoDriverValue.setText(tripInfo.getCoDriverValue());
            mOnDutyLeftValue.setText(tripInfo.getOnDutyTime());
            mDriveValue.setText(tripInfo.getDrivingTime());
            mOdometer.setText(odometerTitle);
            mOdometerValue.setText(String.valueOf(tripInfo.getOdometerValue()));
        }
    }

    private class LogsAdapter extends RecyclerView.Adapter<LogsHolder> {
        static final int VIEW_TYPE_HEADER = 0;
        static final int VIEW_TYPE_EVENTS_TITLE = 1;
        static final int VIEW_TYPE_EVENTS_ITEM = 2;
        static final int VIEW_TYPE_TRIP_INFO_TITLE = 3;
        static final int VIEW_TYPE_TRIP_INFO_ITEM = 4;

        //header + logs + trip info titles
        private final int MIN_LIST_SIZE = 3;

        private LogsTitleView mEventsTitleView;
        private LogsTitleView mTripInfoTitleView;
        private CalendarLayout mCalendarLayout;
        private GraphLayout mGraphLayout;
        private View mSignLogsheet;

        private List<ELDEvent> mELDEvents = new ArrayList<>();
        private TripInfo mTripInfo = new TripInfo();
        private List<LogSheetHeader> mLogHeaders = new ArrayList<>();
        private View.OnClickListener mOnClickListener;
        private RecyclerView.SmoothScroller mSmoothScroller;
        private Context mContext;

        public LogsAdapter(Context context) {
            mContext = context;

            mOnClickListener = view -> {
                ELDEvent event = (ELDEvent) view.getTag();
                showPopupMenu(view, event);
            };

            mSmoothScroller = new LinearSmoothScroller(mContext) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };
        }

        public void setELDEvents(List<ELDEvent> eldEvents) {
            mELDEvents = eldEvents;
            if (mGraphLayout != null) {
                mGraphLayout.setELDEvents(eldEvents);
            }
            notifyDataSetChanged();
        }

        public void setTripInfo(TripInfo tripInfo) {
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
                    mGraphLayout.setELDEvents(mELDEvents);

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
                ELDEvent event = mELDEvents.get(position - 2);
                holder.bindEventView(event, mOnClickListener, mContext);
            } else if (viewType == VIEW_TYPE_TRIP_INFO_ITEM) {
                String unit = getString(mTripInfo.getUnitType() == TripInfo.UnitType.KM ? R.string.km : R.string.ml);
                holder.bindTripInfoView(mTripInfo, getString(R.string.odometer, unit));
            }
        }

        private void onTitleItemClicked(LogsTitleView titleView) {
            if (titleView.isCollapsed()) {
                titleView.expand();
                showSnackBar(titleView.getType());
                if (titleView.getType() == EVENTS) {
                    mTripInfoTitleView.collapse();
                } else {
                    mEventsTitleView.collapse();
                }
            } else {
                titleView.collapse();
                mNavigateView.getSnackBar().hideSnackbar();
            }

            notifyDataSetChanged();
            scrollToPosition(1);
        }

        public void showSnackBar() {
            if (mTripInfoTitleView != null && !mTripInfoTitleView.isCollapsed()) {
                showSnackBar(TRIP_INFO);
            } else if (mEventsTitleView != null && !mEventsTitleView.isCollapsed()) {
                showSnackBar(EVENTS);
            }
        }

        private void showSnackBar(LogsTitleView.Type title) {
            switch (title) {
                case EVENTS:
                    mNavigateView.getSnackBar()
                            .setPositiveLabel(mContext.getString(R.string.add_event), v -> mPresenter.onAddEventClicked())
                            .showSnackbar();
                    break;

                case TRIP_INFO:
                    mNavigateView.getSnackBar()
                            .setPositiveLabel(mContext.getString(R.string.edit), v -> mPresenter.onEditTripInfoClicked())
                            .showSnackbar();
                    break;
            }
        }

        private void scrollToPosition(int position) {
            mSmoothScroller.setTargetPosition(position);
            mRecyclerView.getLayoutManager().startSmoothScroll(mSmoothScroller);
        }

        @Override
        public int getItemCount() {
            int eventsSize = (mEventsTitleView == null || mEventsTitleView.isCollapsed()) ? 0 : mELDEvents.size();
            int tripInfoSize = ((mTripInfoTitleView == null) || mTripInfoTitleView.isCollapsed()) ? 0 : 1;
            return MIN_LIST_SIZE + eventsSize + tripInfoSize;

        }

        @Override
        public int getItemViewType(int position) {
            int eventsSize = (mEventsTitleView == null || mEventsTitleView.isCollapsed()) ? 0 : mELDEvents.size();
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
    }
}
