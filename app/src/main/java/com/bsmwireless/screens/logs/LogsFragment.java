package com.bsmwireless.screens.logs;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.bsmwireless.widgets.logs.LogsBottomBar;
import com.bsmwireless.widgets.logs.LogsTitleView;
import com.bsmwireless.widgets.logs.calendar.CalendarLayout;
import com.bsmwireless.widgets.logs.graphview.GraphLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;

import static com.bsmwireless.widgets.logs.LogsBottomBar.Type.ADD_EVENT;
import static com.bsmwireless.widgets.logs.LogsBottomBar.Type.EDIT;
import static com.bsmwireless.widgets.logs.LogsTitleView.Type.EVENTS;
import static com.bsmwireless.widgets.logs.LogsTitleView.Type.TRIP_INFO;

public class LogsFragment extends BaseFragment implements LogsView {

    @Inject
    LogsPresenter mPresenter;
    private LogsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private LogsBottomBar mLogsBottomBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);

        DaggerLogsComponent.builder().appComponent(App.getComponent()).logsModule(new LogsModule(this)).build().inject(this);

        mAdapter = new LogsAdapter();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        mLogsBottomBar = (LogsBottomBar) view.findViewById(R.id.bottom_bar);
        mLogsBottomBar.setAddEventClickListener(v -> mPresenter.onAddEventClicked());
        mLogsBottomBar.setEditClickListener(v -> mPresenter.onEditTripInfoClicked());

        mPresenter.onViewCreated();
        return view;
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

    private void showPopupMenu(View anchorView, ELDEvent event) {
        PopupMenu popup = new PopupMenu(getActivity(), anchorView);
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

    private final class LogsHolder extends RecyclerView.ViewHolder {

        //event
        private TextView mEventStatus;
        private TextView mEventTime;
        private TextView mEventDuration;
        private TextView mVehicleName;
        private TextView mAddress;
        private View mMenuButton;

        //trip info
        private TextView mCoDriverValue;
        private TextView mOnDutyLeftValue;
        private TextView mDriveValue;
        private TextView mOdometer;
        private TextView mOdometerValue;

        private int mViewType;

        LogsHolder(View itemView, int viewType) {
            super(itemView);
            mViewType = viewType;

            //event
            mEventStatus = (TextView) itemView.findViewById(R.id.event_status);
            mEventTime = (TextView) itemView.findViewById(R.id.event_time);
            mEventDuration = (TextView) itemView.findViewById(R.id.event_duration);
            mVehicleName = (TextView) itemView.findViewById(R.id.vehicle_name);
            mMenuButton = itemView.findViewById(R.id.menu_button);
            mAddress = (TextView) itemView.findViewById(R.id.address);

            //trip info
            mCoDriverValue = (TextView) itemView.findViewById(R.id.co_driver);
            mOnDutyLeftValue = (TextView) itemView.findViewById(R.id.on_duty_left);
            mDriveValue = (TextView) itemView.findViewById(R.id.drive);
            mOdometer = (TextView) itemView.findViewById(R.id.odometer);
            mOdometerValue = (TextView) itemView.findViewById(R.id.odometer_value);
        }

        int getViewType() {
            return mViewType;
        }

        private void bindEventView(ELDEvent event) {
            //TODO: change to data from event
            mEventStatus.setText("Driving");
            mEventTime.setText("00:23:30");
            mEventDuration.setText("6 hrs 0 mins");
            mVehicleName.setText("VEH_ID_123");
            mAddress.setText("86 Oak Street, Toronto, ON");
            mMenuButton.setOnClickListener(v -> showPopupMenu(mMenuButton, event));
        }

        private void bindTripInfoView(TripInfo tripInfo) {
            if (tripInfo != null) {
                mCoDriverValue.setText(tripInfo.getCoDriverValue());
                mOnDutyLeftValue.setText(tripInfo.getOnDutyLeftValue());
                mDriveValue.setText(tripInfo.getDriveValue());
                String unit = getString(tripInfo.getUnitType() == TripInfo.UnitType.KM ? R.string.km : R.string.ml);
                mOdometer.setText(getString(R.string.odometer, unit));
                mOdometerValue.setText(String.valueOf(tripInfo.getOdometerValue()));
            }
        }
    }

    private class LogsAdapter extends RecyclerView.Adapter<LogsHolder> {
        static final int VIEW_TYPE_HEADER = 0;
        static final int VIEW_TYPE_EVENTS_TITLE = 1;
        static final int VIEW_TYPE_EVENTS_ITEM = 2;
        static final int VIEW_TYPE_TRIP_INFO_TITLE = 3;
        static final int VIEW_TYPE_TRIP_INFO_ITEM = 4;

        //header + logs + trip info items
        private final int MIN_LIST_SIZE = 3;

        private LogsTitleView mEventsTitleView;
        private LogsTitleView mTripInfoTitleView;
        private CalendarLayout mCalendarLayout;
        private GraphLayout mGraphLayout;
        private View mSignLogsheet;

        private List<ELDEvent> mELDEvents = new ArrayList<>();
        private TripInfo mTripInfo;
        private List<LogSheetHeader> mLogHeaders = new ArrayList<>();

        public LogsAdapter() {
        }

        public void setELDEvents(List<ELDEvent> ELDEvents) {
            mELDEvents = ELDEvents;
        }

        public void setTripInfo(TripInfo tripInfo) {
            mTripInfo = tripInfo;
        }

        public void setLogSheetHeaders(List<LogSheetHeader> logHeaders) {
            mLogHeaders = logHeaders;
        }

        @Override
        public LogsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
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
                    mEventsTitleView = new LogsTitleView(getContext());
                    mEventsTitleView.setType(EVENTS);
                    mEventsTitleView.setOnClickListener(v -> onTitleItemClicked((LogsTitleView) v));
                    view = mEventsTitleView;
                    break;
                case VIEW_TYPE_EVENTS_ITEM:
                    view = layoutInflater.inflate(R.layout.logs_list_item_eld_event, parent, false);
                    break;
                case VIEW_TYPE_TRIP_INFO_TITLE:
                    mTripInfoTitleView = new LogsTitleView(getContext());
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
                holder.bindEventView(event);
            } else if (viewType == VIEW_TYPE_TRIP_INFO_ITEM) {
                holder.bindTripInfoView(mTripInfo);
            }
        }

        private void onTitleItemClicked(LogsTitleView titleView) {
            if (titleView.isCollapsed()) {
                titleView.expand();
                if (titleView.getType() == EVENTS) {
                    mLogsBottomBar.show(ADD_EVENT);
                    mTripInfoTitleView.collapse();
                } else {
                    mLogsBottomBar.show(EDIT);
                    mEventsTitleView.collapse();
                }
            } else {
                titleView.collapse();
                mLogsBottomBar.hide();
            }

            notifyDataSetChanged();
            scrollToPosition(1);
        }

        private void scrollToPosition(int position) {
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };
            smoothScroller.setTargetPosition(position);
            mLayoutManager.startSmoothScroll(smoothScroller);
        }

        @Override
        public int getItemCount() {
            int eventsSize = (mEventsTitleView == null || mEventsTitleView.isCollapsed()) ? 0 : mELDEvents.size();
            return MIN_LIST_SIZE + eventsSize + (((mTripInfoTitleView == null) || mTripInfoTitleView.isCollapsed()) ? 0 : 1);
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
