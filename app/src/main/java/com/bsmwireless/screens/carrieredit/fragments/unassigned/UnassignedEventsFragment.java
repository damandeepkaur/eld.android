package com.bsmwireless.screens.carrieredit.fragments.unassigned;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsmwireless.screens.carrieredit.CarrierEditView;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.logs.dagger.EventLogModel;

import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public final class UnassignedEventsFragment extends BaseFragment implements UnassignedEventsView {

    @BindView(R.id.unassigned_recycler)
    RecyclerView mRecyclerView;

    @Inject
    UnassignedEventsPresenter mPresenter;
    private UnassignedEventsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.v("onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_unassigned_events, container, false);
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
        mAdapter.setVehicleName(vehicleName);
    }

    @Override
    public void setDriverId(int driverId) {

    }
}
