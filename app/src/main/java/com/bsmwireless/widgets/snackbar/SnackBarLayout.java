package com.bsmwireless.widgets.snackbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SnackBarLayout extends RelativeLayout {
    public static final int DURATION_SHORT = 1000;
    public static final int DURATION_LONG = 5000;
    public static final int DURATION_INFINITE = 0;

    private View mRootView;

    private Unbinder mUnbinder;

    private BottomSheetBehavior mBottomSheet;

    private BottomSheetBehavior.BottomSheetCallback mListener;

    @BindView(R.id.snackbar_layout)
    View mLayout;

    @BindView(R.id.negative_action)
    Button mNegativeAction;

    @BindView(R.id.positive_action)
    Button mPosAction;

    @BindView(R.id.message)
    TextView mMessage;

    private int mTextColor;
    private int mBackgroundColor;
    private int mDuration = DURATION_INFINITE;

    private Handler mHandler = new Handler();
    private Runnable mHideTask = this::hideSnackbar;

    public SnackBarLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SnackBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SnackBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mRootView = inflate(context, R.layout.snackbar_layout, this);
        setFocusableInTouchMode(true);
        setFocusable(true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SnackBarLayout, defStyleAttr, 0);
        mTextColor = typedArray.getColor(R.styleable.SnackBarLayout_sbl_text_color, Color.BLACK);
        mBackgroundColor = typedArray.getColor(R.styleable.SnackBarLayout_sbl_background_color, Color.WHITE);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mUnbinder = ButterKnife.bind(this, mRootView);

        mMessage.setTextColor(mTextColor);
        mLayout.setBackgroundColor(mBackgroundColor);
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
        mHandler.removeCallbacks(mHideTask);
        mUnbinder.unbind();
        super.onDetachedFromWindow();
    }

    public SnackBarLayout setHideableOnTimeout(int timeout) {
        mDuration = timeout;
        return this;
    }

    public SnackBarLayout setHideableOnFocusLost(boolean hideable) {
        setOnFocusChangeListener(!hideable ? null : (v, hasFocus) -> {
            if (!hasFocus) {
                hideSnackbar();
            }
        });

        return this;
    }

    public SnackBarLayout hideSnackbar() {
        mBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        mHandler.removeCallbacks(mHideTask);
        return this;
    }

    public SnackBarLayout showSnackbar() {
        mBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        requestFocus();

        if (mDuration > 0) {
            mHandler.postDelayed(mHideTask, mDuration);
        }
        return this;
    }

    public SnackBarLayout setMessage(CharSequence message) {
        mMessage.setText(message);
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
