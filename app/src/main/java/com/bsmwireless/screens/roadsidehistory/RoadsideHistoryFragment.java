package com.bsmwireless.screens.roadsidehistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsmwireless.screens.common.BaseFragment;

import app.bsmuniversal.com.R;

public final class RoadsideHistoryFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roadside_history, container, false);
    }
}
