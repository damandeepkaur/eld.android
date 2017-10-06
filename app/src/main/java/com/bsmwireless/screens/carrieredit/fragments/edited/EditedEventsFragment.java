package com.bsmwireless.screens.carrieredit.fragments.edited;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsmwireless.models.LogSheetHeader;
import com.bsmwireless.screens.carrieredit.CarrierEditView;
import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.logs.GraphModel;
import com.bsmwireless.screens.logs.LogHeaderModel;
import com.bsmwireless.screens.logs.dagger.EventLogModel;
import com.bsmwireless.widgets.logs.WrapLinearLayoutManager;
import com.bsmwireless.widgets.logs.calendar.CalendarItem;

import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public final class EditedEventsFragment extends BaseFragment implements EditedEventsView {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Inject
    EditedEventsPresenter mPresenter;

    private EditedEventsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.v("onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_edited_events, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.v("onActivityCreated: ");
        ((CarrierEditView) getActivity()).getComponent().inject(this);
        mPresenter.setView(this);
        mAdapter = new EditedEventsAdapter(mContext, mRecyclerView, mPresenter);

        mRecyclerView.setLayoutManager(new WrapLinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setEvents(List<EventLogModel> events) {
        Timber.v("setEvents: ");
        mAdapter.setEvents(events);
    }

    @Override
    public void setLogSheetHeaders(List<LogSheetHeader> logs) {
        Timber.v("setLogSheetHeaders: ");
        mAdapter.setLogSheetHeaders(logs);
    }

    @Override
    public void updateGraph(GraphModel graphModel) {
        Timber.v("updateGraph: ");
        mAdapter.updateGraph(graphModel);
    }

    @Override
    public void setLogHeader(LogHeaderModel logHeader) {
        Timber.v("setLogHeader: ");
        mAdapter.setLogHeader(logHeader);
    }

    @Override
    public void updateCalendarItems(List<CalendarItem> calendarItems) {
        Timber.v("updateCalendarItems: ");
        mAdapter.updateCalendarItems(calendarItems);
    }

    @Override
    public CalendarItem getSelectedDay() {
        return mAdapter.getCurrentItem();
    }

    @Override
    public void onDestroy() {
        mPresenter.destroy();
        super.onDestroy();
        Timber.v("onDestroy: ");
    }
}
