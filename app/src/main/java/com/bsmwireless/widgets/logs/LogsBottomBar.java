package com.bsmwireless.widgets.logs;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import app.bsmuniversal.com.R;

public class LogsBottomBar extends FrameLayout {
    private final int ANIM_DURATION = getResources().getInteger(android.R.integer.config_shortAnimTime);

    public enum Type {
        ADD_EVENT,
        EDIT
    }

    private View mRootView;

    private OnClickListener mAddEventClickListener;
    private OnClickListener mEditClickListener;

    Button mActionButton;
    Type mType = Type.ADD_EVENT;

    public LogsBottomBar(Context context) {
        super(context);
        init(context);
    }

    public LogsBottomBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LogsBottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mRootView = inflate(context, R.layout.logs_bottom_bar, this);
        mActionButton = (Button) findViewById(R.id.action);
        mActionButton.setOnClickListener(v -> {
            if (mType == Type.ADD_EVENT) {
                if (mAddEventClickListener != null) {
                    mAddEventClickListener.onClick(v);
                }
            } else {
                if (mEditClickListener != null) {
                    mEditClickListener.onClick(v);
                }
            }
        });
    }

    public void show(Type type) {
        mType = type;
        if (type == Type.ADD_EVENT) {
            mActionButton.setText(R.string.add_event);
        } else {
            mActionButton.setText(R.string.edit);
        }
        setVisibility(View.VISIBLE);
        animate().translationY(-getHeight());
        animate().translationY(0).setDuration(ANIM_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(View.VISIBLE);
            }
        });
    }

    public void hide() {
        setVisibility(View.VISIBLE);
        animate().translationY(getHeight()).setDuration(ANIM_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animate().cancel();
    }

    public void setAddEventClickListener(OnClickListener listener) {
        mAddEventClickListener = listener;
    }

    public void setEditClickListener(OnClickListener listener) {
        mEditClickListener = listener;
    }
}
