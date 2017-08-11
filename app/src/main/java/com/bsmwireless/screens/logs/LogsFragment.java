package com.bsmwireless.screens.logs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.logs.LogsAdapter.OnLogsTitleStateChangeListener;
import com.bsmwireless.screens.logs.dagger.DaggerLogsComponent;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.screens.logs.dagger.LogsModule;
import com.bsmwireless.screens.navigation.NavigateView;
import com.bsmwireless.widgets.logs.LogsTitleView;

import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
            showSnackBar();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        DaggerLogsComponent.builder().appComponent(App.getComponent()).logsModule(new LogsModule(this)).build().inject(this);

        mAdapter = new LogsAdapter(mContext, mPresenter, new OnLogsTitleStateChangeListener() {
            @Override
            public void show(LogsTitleView.Type expandedType) {
                showSnackBar(expandedType);
            }

            @Override
            public void hide() {
                mNavigateView.getSnackBar().hideSnackbar();
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
                        .setPositiveLabel(mContext.getString(R.string.add_event),
                                v -> mPresenter.onAddEventClicked())
                        .showSnackbar();
                break;
            case TRIP_INFO:
                mNavigateView.getSnackBar()
                        .setPositiveLabel(mContext.getString(R.string.edit),
                                v -> mPresenter.onEditTripInfoClicked())
                        .showSnackbar();
                break;
        }
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
}
