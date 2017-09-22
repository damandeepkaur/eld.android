package com.bsmwireless.screens.carrieredit.fragments.edited;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsmwireless.screens.carrieredit.CarrierEditActivity;
import com.bsmwireless.screens.carrieredit.CarrierEditView;
import com.bsmwireless.screens.common.BaseFragment;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.ButterKnife;

/**
 * Created by osminin on 22.09.2017.
 */

public final class EditedEventsFragment extends BaseFragment implements EditedEventsView {

    @Inject
    EditedEventsPresenter mPresenter;

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
    }
}
