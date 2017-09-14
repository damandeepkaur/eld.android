package com.bsmwireless.screens.lockscreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


public class RetainFragment extends Fragment {

    private Object mComponent;

    public static RetainFragment createFragment(){
        return new RetainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void saveComponent(Object object) {
        mComponent = object;
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent() {
        return (T) mComponent;
    }
}
