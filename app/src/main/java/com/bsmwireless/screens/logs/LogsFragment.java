package com.bsmwireless.screens.logs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.editevent.EditEventActivity;
import com.bsmwireless.screens.logs.LogsAdapter.OnLogsStateChangeListener;
import com.bsmwireless.screens.logs.dagger.DaggerLogsComponent;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.screens.logs.dagger.LogsModule;
import com.bsmwireless.screens.navigation.NavigateView;
import com.bsmwireless.widgets.logs.LogsTitleView;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.bsmwireless.screens.editevent.EditEventActivity.DAY_TIME_EXTRA;
import static com.bsmwireless.screens.editevent.EditEventActivity.NEW_ELD_EVENT_EXTRA;
import static com.bsmwireless.screens.editevent.EditEventActivity.OLD_ELD_EVENT_EXTRA;

public class LogsFragment extends BaseFragment implements LogsView {

    private static final int REQUEST_CODE_EDIT_EVENT = 101;
    private static final int REQUEST_CODE_ADD_EVENT = 102;

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
            showSnackBar();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        DaggerLogsComponent.builder().appComponent(App.getComponent()).logsModule(new LogsModule(this)).build().inject(this);

        mAdapter = new LogsAdapter(mContext, mPresenter, new OnLogsStateChangeListener() {
            @Override
            public void showTitle(LogsTitleView.Type expandedType) {
                showSnackBar(expandedType);
            }

            @Override
            public void hideTitle() {
                mNavigateView.getSnackBar().hideSnackbar();
            }

            @Override
            public void onSignButtonClicked(CalendarItem calendarItem) {
                showSignDialog(calendarItem);
            }
        });

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

    public void showSnackBar() {
        LogsTitleView.Type expandedType = mAdapter.getExpandedLogsTitle();
        if (expandedType != null) showSnackBar(expandedType);
    }

    public void showSnackBar(LogsTitleView.Type expandedType) {
        switch (expandedType) {
            case EVENTS:
                mNavigateView.getSnackBar()
                             .setOnReadyListener(snackBar ->
                                     snackBar.reset()
                                             .setPositiveLabel(mContext.getString(R.string.add_event), v -> mPresenter.onAddEventClicked(mAdapter.getCurrentItem())))
                             .showSnackbar();
                break;
            case TRIP_INFO:
                mNavigateView.getSnackBar()
                             .setOnReadyListener(snackBar ->
                                     snackBar.reset()
                                             .setPositiveLabel(mContext.getString(R.string.edit), v -> mPresenter.onEditTripInfoClicked()))
                             .showSnackbar();
                break;
        }
    }

    public void showNotificationSnackBar(String message) {
        mNavigateView.getSnackBar()
                     .setOnReadyListener(snackBar -> {
                         snackBar.reset()
                                 .setMessage(message)
                                 .setHideableOnTimeout(SnackBarLayout.DURATION_LONG)
                                 .setOnCloseListener(new SnackBarLayout.OnCloseListener() {
                                     @Override
                                     public void onClose(SnackBarLayout snackBar) {
                                         showSnackBar();
                                     }

                                     @Override
                                     public void onOpen(SnackBarLayout snackBar) {}
                                 });
                     })
                     .showSnackbar();
    }

    public void showSignDialog(CalendarItem calendarItem) {
        View alertView = getActivity().getLayoutInflater().inflate(R.layout.sign_dialog_view, null);
        AlertDialog signDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.sign_dialog_title)
                .setPositiveButton(R.string.accept,
                        (dialog, whichButton) -> mPresenter.onSignLogsheetButtonClicked(
                                calendarItem))
                .setNegativeButton(R.string.decline, null)
                .create();
        signDialog.setView(alertView);
        signDialog.show();
    }

    @Override
    public void setEventLogs(List<EventLogModel> eventLogs) {
        mAdapter.setEventLogs(eventLogs);
    }

    @Override
    public void setTripInfo(TripInfoModel tripInfo) {
        mAdapter.setTripInfo(tripInfo);
    }

    @Override
    public void setLogSheetHeaders(List<LogSheetHeader> logs) {
        mAdapter.setLogSheetHeaders(logs);
    }

    @Override
    public void goToAddEventScreen(CalendarItem day) {
        Intent addEventIntent = new Intent(mContext, EditEventActivity.class);
        addEventIntent.putExtra(DAY_TIME_EXTRA, day.getTimestamp());
        startActivityForResult(addEventIntent, REQUEST_CODE_ADD_EVENT);
    }

    @Override
    public void goToEditEventScreen(EventLogModel event) {
        Intent editEventIntent = new Intent(mContext, EditEventActivity.class);
        editEventIntent.putExtra(OLD_ELD_EVENT_EXTRA, event.getEvent());
        startActivityForResult(editEventIntent, REQUEST_CODE_EDIT_EVENT);
    }

    @Override
    public void goToEditTripInfoScreen() {
        //TODO: go to edit trip info screen
        Toast.makeText(mContext, "Go to edit trip info screen", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void eventAdded() {
        showNotificationSnackBar(getString(R.string.event_added));
        CalendarItem item = mAdapter.getCurrentItem();
        mPresenter.setEventsForDay(item.getCalendar());
        mNavigateView.setResetTime(0);
    }

    @Override
    public void eventUpdated() {
        showNotificationSnackBar(getString(R.string.event_updated));
        CalendarItem item = mAdapter.getCurrentItem();
        mPresenter.setEventsForDay(item.getCalendar());
        mNavigateView.setResetTime(0);
    }

    @Override
    public void dutyUpdated() {
        if (mAdapter != null) {
            CalendarItem item = mAdapter.getCurrentItem();
            if (item.isCurrentDay(System.currentTimeMillis())) {
                mPresenter.setEventsForDay(item.getCalendar());
            }
        }
    }

    @Override
    public void showError(RetrofitException exception) {
        showNotificationSnackBar(NetworkUtils.getErrorMessage(exception, mContext).toString());
    }

    @Override
    public void showError(Error error) {
        showNotificationSnackBar(getString(error.getStringId()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD_EVENT: {
                if (resultCode == RESULT_OK) {
                    ELDEvent newEvent = data.getParcelableExtra(NEW_ELD_EVENT_EXTRA);
                    mPresenter.onEventAdded(newEvent);
                }
                break;
            }
            case REQUEST_CODE_EDIT_EVENT: {
                if (resultCode == RESULT_OK) {
                    ELDEvent updatedEvent = data.getParcelableExtra(NEW_ELD_EVENT_EXTRA);
                    mPresenter.onEventChanged(updatedEvent);
                }
                break;
            }
            default: {
                break;
            }
        }
    }
}
