package com.bsmwireless.screens.carrieredit.fragments.unassigned;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsmwireless.screens.carrieredit.CarrierEditView;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public final class UnassignedEventsFragment extends BaseFragment implements UnassignedEventsView {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.snackbar)
    SnackBarLayout mSnackBarLayout;

    @Inject
    UnassignedEventsPresenter mPresenter;
    private UnassignedEventsAdapter mAdapter;
    private int mDriverId = -1;
    private String mVehicleName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.v("onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_carrier_edits, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Timber.v("onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        ((CarrierEditView) getActivity()).getComponent().inject(this);
        mPresenter.setView(this);
        mAdapter = new UnassignedEventsAdapter(getActivity(), mPresenter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        if (!TextUtils.isEmpty(mVehicleName)) {
            mAdapter.setVehicleName(mVehicleName);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        if (mDriverId != -1) {
            mAdapter.setDriverId(mDriverId);
        }
        mPresenter.fetchEldEvents();
    }

    @Override
    public void onDestroy() {
        Timber.v("onDestroy: ");
        super.onDestroy();
        mPresenter.dispose();
    }

    @Override
    public void setEvents(List<EventLogModel> events) {
        Timber.v("setEvents: ");
        mAdapter.setEvents(events);
    }

    @Override
    public void removeEvent(int position) {
        mAdapter.removeEvent(position);
    }

    @Override
    public void setVehicleName(String vehicleName) {
        if (mRecyclerView == null || mAdapter == null) {
            mVehicleName = vehicleName;
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.setVehicleName(vehicleName);
        }
    }

    @Override
    public void setDriverId(int driverId) {
        if (mAdapter == null) {
            mDriverId = driverId;
        } else {
            mAdapter.setDriverId(driverId);
        }
    }

    @Override
    public void showConnectionError() {
        mSnackBarLayout
                .setMessage(getString(R.string.error_network))
                .setHideableOnTimeout(SnackBarLayout.DURATION_LONG)
                .showSnackbar();
    }
}
