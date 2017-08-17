package com.bsmwireless.widgets.snackbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
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

import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

public class SnackBarLayout extends RelativeLayout {
    public static final int DURATION_SHORT = 1000;
    public static final int DURATION_LONG = 5000;
    public static final int DURATION_INFINITE = 0;
    public static final int DURATION_REOPEN = 500;

    private View mRootView;

    private Unbinder mUnbinder;

    private BottomSheetBehavior mBottomSheet;

    private OnCloseListener mCloseListener;

    private PreShowListener mPreShowListener;

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

    private boolean mIsHideableOnTouch = true;

    private Handler mHandler = new Handler();
    private Runnable mHideTask = this::hideSnackbar;
    private Runnable mReopenTask = this::showSnackbar;

    private int mCurrentState = STATE_HIDDEN;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
                case STATE_HIDDEN: {
                    if (mCloseListener != null) {
                        mCloseListener.onClose(SnackBarLayout.this);
                    }
                    break;
                }
                case STATE_EXPANDED: {
                    if (mCloseListener != null) {
                        mCloseListener.onOpen(SnackBarLayout.this);
                    }
                    break;
                }
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

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
        mBottomSheet.setState(mCurrentState);
        mBottomSheet.setBottomSheetCallback(mBottomSheetCallback);
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

    public SnackBarLayout setHideableOnTouch(boolean hideable) {
        mIsHideableOnTouch = hideable;
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

    @Override
    public boolean isShown() {
        return mIsHideableOnTouch && super.isShown();
    }

    public SnackBarLayout hideSnackbar() {
        mCurrentState = STATE_HIDDEN;

        if (mBottomSheet != null) {
            mBottomSheet.setState(mCurrentState);
        }

        mHandler.removeCallbacks(mHideTask);
        return this;
    }

    public SnackBarLayout showSnackbar() {
        if (mCurrentState == STATE_EXPANDED) {
            hideSnackbar();
            mHandler.postDelayed(mReopenTask, DURATION_REOPEN);
        } else {
            mCurrentState = STATE_EXPANDED;

            if (mPreShowListener != null) {
                mPreShowListener.onPreShow(this);
            }

            if (mBottomSheet != null) {
                mBottomSheet.setState(mCurrentState);
                requestFocus();
            }

            if (mDuration > 0) {
                mHandler.postDelayed(mHideTask, mDuration);
            }

            mHandler.removeCallbacks(mReopenTask);
        }
        return this;
    }

    public SnackBarLayout setMessage(CharSequence message) {
        mMessage.setText(message);
        mMessage.setVisibility(VISIBLE);
        return this;
    }

    public SnackBarLayout setPositiveLabel(String label, OnClickListener listener) {
        mPosAction.setText(label);
        mPosAction.setVisibility(VISIBLE);
        mPosAction.setOnClickListener(listener);
        return this;
    }

    public SnackBarLayout setNegativeLabel(String label, OnClickListener listener) {
        mNegativeAction.setText(label);
        mNegativeAction.setVisibility(VISIBLE);
        mNegativeAction.setOnClickListener(listener);
        return this;
    }

    public SnackBarLayout setOnCloseListener(OnCloseListener listener) {
        mCloseListener = listener;
        return this;
    }

    public SnackBarLayout reset() {
        mPosAction.setVisibility(GONE);
        mPosAction.setOnClickListener(null);
        mNegativeAction.setVisibility(GONE);
        mNegativeAction.setOnClickListener(null);
        mMessage.setVisibility(GONE);
        mDuration = DURATION_INFINITE;
        mHandler.removeCallbacksAndMessages(null);
        mCloseListener = null;
        mPreShowListener = null;

        return this;
    }

    public SnackBarLayout setPreShowListener(PreShowListener preShowListener) {
        mPreShowListener = preShowListener;
        return this;
    }

    public interface OnCloseListener {
        void onClose(SnackBarLayout snackBar);
        void onOpen(SnackBarLayout snackBar);
    }

    public interface PreShowListener {
        void onPreShow(SnackBarLayout snackBar);
    }
}
