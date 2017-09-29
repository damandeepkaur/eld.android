package com.bsmwireless.screens.carrieredit.fragments.edited;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsmwireless.screens.carrieredit.CarrierEditActivity;
import com.bsmwireless.screens.carrieredit.CarrierEditView;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.logs.LogsAdapter;
import com.bsmwireless.screens.logs.LogsPresenter;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.logs.LogsTitleView;
import com.bsmwireless.widgets.logs.WrapLinearLayoutManager;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by osminin on 22.09.2017.
 */

public final class EditedEventsFragment extends BaseFragment implements EditedEventsView {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Inject
    EditedEventsPresenter mPresenter;

    private EditedEventsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edited_events, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((CarrierEditView) getActivity()).getComponent().inject(this);
        mPresenter.setView(this);
        mAdapter = new EditedEventsAdapter(mContext);

        mRecyclerView.setLayoutManager(new WrapLinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.fetchEldEvents();
    }

    @Override
    public void setEvents(List<EventLogModel> events) {
        Timber.v("setEvents: ");
        mAdapter.setEvents(events);
    }
}
