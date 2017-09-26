package com.bsmwireless.widgets.logs;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import timber.log.Timber;

public final class WrapLinearLayoutManager extends LinearLayoutManager {

    public WrapLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //workaround for issue with updating RecyclerView from different threads
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Timber.e(e);
        }
    }
}
