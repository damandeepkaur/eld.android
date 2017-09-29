package com.bsmwireless.screens.logs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.editevent.EditEventActivity;
import com.bsmwireless.screens.editlogheader.EditLogHeaderActivity;
import com.bsmwireless.screens.logs.LogsAdapter.OnLogsStateChangeListener;
import com.bsmwireless.screens.logs.dagger.DaggerLogsComponent;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.screens.logs.dagger.LogsModule;
import com.bsmwireless.screens.navigation.NavigateView;
import com.bsmwireless.widgets.logs.LogsTitleView;
import com.bsmwireless.widgets.logs.WrapLinearLayoutManager;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.bsmwireless.screens.editevent.EditEventActivity.DAY_TIME_EXTRA;
import static com.bsmwireless.screens.editevent.EditEventActivity.NEW_ELD_EVENT_EXTRA;
import static com.bsmwireless.screens.editevent.EditEventActivity.OLD_ELD_EVENT_EXTRA;
import static com.bsmwireless.screens.editlogheader.EditLogHeaderActivity.NEW_LOG_HEADER_EXTRA;
import static com.bsmwireless.screens.editlogheader.EditLogHeaderActivity.OLD_LOG_HEADER_EXTRA;

public final class LogsFragment extends BaseFragment implements LogsView {

    private static final int REQUEST_CODE_EDIT_EVENT = 101;
    private static final int REQUEST_CODE_ADD_EVENT = 102;
    private static final int REQUEST_CODE_EDIT_LOG_HEADER = 103;

    @Inject
    LogsPresenter mPresenter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.add_event)
    FloatingActionButton mAddEventFloatingActionButton;

    @BindView(R.id.edit_log_header)
    FloatingActionButton mEditLogHeaderFloatingActionButton;

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
            showTitleButton();
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
                showTitleButton(expandedType);
            }

            @Override
            public void hideTitle(LogsTitleView.Type expandedType) {
                hideTitleButton(expandedType);
            }

            @Override
            public void onSignButtonClicked(CalendarItem calendarItem) {
                showSignDialog(calendarItem);
            }
        });

        mRecyclerView.setLayoutManager(new WrapLinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter.onViewCreated();
        return view;
    }

    @OnClick(R.id.edit_log_header)
    void editLogHeader() {
        mPresenter.onEditLogHeaderClicked(mAdapter.getLogHeaderModel());
    }

    @OnClick(R.id.add_event)
    void addEvents() {
        mPresenter.onAddEventClicked(mAdapter.getCurrentItem());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mPresenter.onDestroy();
    }

    public void showTitleButton() {
        LogsTitleView.Type expandedType = mAdapter.getExpandedLogsTitle();
        if (expandedType != null) showTitleButton(expandedType);
    }

    public void showNotificationSnackBar(String message) {
        mNavigateView.getSnackBar()
                .setOnReadyListener(snackBar -> snackBar.reset()
                        .setMessage(message)
                        .setHideableOnTimeout(SnackBarLayout.DURATION_LONG))
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
    public CalendarItem getSelectedDay() {
        return mAdapter.getCurrentItem();
    }

    @Override
    public void setEventLogs(List<EventLogModel> eventLogs) {
        mAdapter.setEventLogs(eventLogs);
    }

    @Override
    public void updateGraph(GraphModel graphModel) {
        mAdapter.updateGraph(graphModel);
    }

    @Override
    public void setLogHeader(LogHeaderModel logHeader) {
        mAdapter.setLogHeader(logHeader);
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
    public void goToEditLogHeaderScreen(LogHeaderModel logHeaderModel) {
        Intent intent = new Intent(mContext, EditLogHeaderActivity.class);
        intent.putExtra(OLD_LOG_HEADER_EXTRA, logHeaderModel);
        startActivityForResult(intent, REQUEST_CODE_EDIT_LOG_HEADER);
    }

    @Override
    public void eventAdded() {
        showNotificationSnackBar(getString(R.string.event_added));
        CalendarItem item = mAdapter.getCurrentItem();
        mPresenter.updateDataForDay(item.getLogDay());
        mNavigateView.setResetTime(0);
    }

    @Override
    public void eventUpdated() {
        showNotificationSnackBar(getString(R.string.event_updated));
        CalendarItem item = mAdapter.getCurrentItem();
        mPresenter.updateDataForDay(item.getLogDay());
        mNavigateView.setResetTime(0);
    }

    @Override
    public void dutyUpdated() {
        //update from db
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
    public void showTitleButton(LogsTitleView.Type expandedType) {
        switch (expandedType) {
            case EVENTS:
                if (mEditLogHeaderFloatingActionButton.isShown()) {
                    mEditLogHeaderFloatingActionButton.hide();
                }
                mAddEventFloatingActionButton.show();
                break;
            case LOG_HEADER:
                if (mAddEventFloatingActionButton.isShown()) {
                    mAddEventFloatingActionButton.hide();
                }
                mEditLogHeaderFloatingActionButton.show();
                break;
            default:
                break;
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mAdapter != null) {
            mPresenter.updateDataForDay(mAdapter.getCurrentItem().getLogDay());
        }
    }

    @Override
    public void hideTitleButton(LogsTitleView.Type expandedType) {
        switch (expandedType) {
            case EVENTS:
                mAddEventFloatingActionButton.hide();
                break;
            case LOG_HEADER:
                mEditLogHeaderFloatingActionButton.hide();
                break;
            default:
                break;
        }
    }

    @Override
    public void showReassignDialog(ELDEvent event) {
        mNavigateView.showReassignDialog(event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD_EVENT: {
                if (resultCode == RESULT_OK) {
                    List<ELDEvent> newEvents = data.getParcelableArrayListExtra(NEW_ELD_EVENT_EXTRA);
                    mPresenter.onEventAdded(newEvents);
                }
                break;
            }
            case REQUEST_CODE_EDIT_EVENT: {
                if (resultCode == RESULT_OK) {
                    List<ELDEvent> updatedEvents = data.getParcelableArrayListExtra(NEW_ELD_EVENT_EXTRA);
                    mPresenter.onEventChanged(updatedEvents);
                }
                break;
            }
            case REQUEST_CODE_EDIT_LOG_HEADER: {
                if (resultCode == RESULT_OK) {
                    LogHeaderModel logHeaderModel = data.getParcelableExtra(NEW_LOG_HEADER_EXTRA);
                    mPresenter.onLogHeaderChanged(logHeaderModel);
                }
                break;
            }
            default: {
                break;
            }
        }
    }
}

