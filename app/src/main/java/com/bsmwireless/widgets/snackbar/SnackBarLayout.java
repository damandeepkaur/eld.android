package com.bsmwireless.widgets.snackbar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SnackBarLayout extends LinearLayout {

    private View mRootView;

    private Unbinder mUnbinder;

    private BottomSheetBehavior mBottomSheet;

    private BottomSheetBehavior.BottomSheetCallback mListener;

    @BindView(R.id.negative_action)
    Button mNegativeAction;
    @BindView(R.id.positive_action)
    Button mPosAction;

    public SnackBarLayout(Context context) {
        super(context);
        init(context);
    }

    public SnackBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SnackBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mRootView = inflate(context, R.layout.snackbar_layout, this);
        setFocusableInTouchMode(true);
        setFocusable(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mUnbinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mBottomSheet = BottomSheetBehavior.from(this);
        mBottomSheet.setHideable(true);
        mBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheet.setBottomSheetCallback(mListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        clearAnimation();
        mUnbinder.unbind();
        super.onDetachedFromWindow();
    }

    public SnackBarLayout hideSnackbar() {
        mBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        return this;
    }

    public SnackBarLayout showSnackbar() {
        mBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        requestFocus();
        return this;
    }

    public SnackBarLayout setPositiveLabel(String label, OnClickListener listener) {
        mPosAction.setText(label);
        mPosAction.setVisibility(View.VISIBLE);
        mPosAction.setOnClickListener(listener);
        return this;
    }

    public SnackBarLayout setNegativeLabel(String label, OnClickListener listener) {
        mNegativeAction.setText(label);
        mNegativeAction.setVisibility(VISIBLE);
        mNegativeAction.setOnClickListener(listener);
        return this;
    }

    public SnackBarLayout setBottomSheetCallback(BottomSheetBehavior.BottomSheetCallback callback) {
        mListener = callback;
        return this;
    }
}
