package com.bsmwireless.widgets.logs;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LogsTitleView extends FrameLayout {

    @BindView(R.id.list_item_title)
    TextView mTitle;
    @BindView(R.id.list_item_arrow)
    ImageView mArrow;
    @BindView(R.id.below_divider)
    View mBelowDivider;

    private Unbinder mUnbinder;
    private View mRootView;
    private boolean isCollapsed = true;
    private Type mType = Type.EVENTS;

    public LogsTitleView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LogsTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LogsTitleView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mRootView = inflate(context, R.layout.logs_list_item_title, this);
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public void expand() {
        new Handler().postDelayed(() -> mArrow.animate().rotation(-180).setDuration(200).start(), 100);
        mBelowDivider.setVisibility(INVISIBLE);
        isCollapsed = false;
    }

    public void collapse() {
        if (isAttachedToWindow()) {
            mArrow.setRotation(0);
            mBelowDivider.setVisibility(VISIBLE);
        }
        isCollapsed = true;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mUnbinder = ButterKnife.bind(this, mRootView);
        if (mType == Type.EVENTS) {
            mTitle.setText(R.string.logs_events);
        } else {
            mTitle.setText(R.string.logs_trip_info);
        }

        mArrow.setRotation(isCollapsed ? 0 : -180);
        mBelowDivider.setVisibility(isCollapsed ? VISIBLE : INVISIBLE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mUnbinder.unbind();
    }

    public enum Type {
        EVENTS,
        TRIP_INFO
    }
}
