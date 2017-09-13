package com.bsmwireless.screens.diagnostic;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bsmwireless.common.App;
import com.bsmwireless.models.ELDEvent;

import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;


public class MalfunctionDialog extends DialogFragment implements DiagnosticView{

    private RecyclerView mRecyclerView;
    private DiagnosticEventsAdapter mAdapter;

    @Inject
    DiagnosticPresenter mPresenter;

    public static MalfunctionDialog newInstance(){
        return new MalfunctionDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getComponent().diagnosticBuilder()
                .view(this)
                .dialogType(DiagnosticPresenter.EventType.MALFUNCTION)
                .build().inject(this);
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new DiagnosticEventsAdapter(getActivity().getLayoutInflater());
        mRecyclerView.setAdapter(mAdapter);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPresenter.onCreated();
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.malfunction_events_title)
                .setView(mRecyclerView)
                .create();
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDestroyed();
        super.onDestroyView();
    }

    @Override
    public void showNoEvents() {

    }

    @Override
    public void showEvents(List<ELDEvent> events, String timezone) {
        mAdapter.setTimeZone(timezone);
        mAdapter.setItems(events);
        mAdapter.notifyDataSetChanged();
    }
}
