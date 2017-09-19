package com.bsmwireless.screens.common;

import android.content.Context;
import android.support.v4.app.Fragment;

import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    protected Unbinder mUnbinder;
    protected Context mContext;

    @SuppressWarnings("DesignForExtension")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @SuppressWarnings("DesignForExtension")
    @Override
    public void onDestroyView() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroyView();
    }

}
