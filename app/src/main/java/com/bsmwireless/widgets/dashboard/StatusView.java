package com.bsmwireless.widgets.dashboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StatusView extends CardView {
    @BindView(R.id.status_line)
    View mLine;

    @BindView(R.id.status_text)
    TextView mText;

    private Unbinder mUnbinder;

    public StatusView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public StatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public StatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        View rootView = inflate(context, R.layout.view_status, this);
        mUnbinder = ButterKnife.bind(rootView, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusView, defStyleAttr, 0);
        int color = typedArray.getColor(R.styleable.StatusView_sv_color, Color.WHITE);

        mText.setTextColor(color);
        mText.setText(typedArray.getString(R.styleable.StatusView_sv_text));
        mText.setBackgroundColor(typedArray.getColor(R.styleable.StatusView_sv_background_color, Color.WHITE));

        mLine.setBackgroundColor(color);
        mLine.setVisibility(typedArray.getBoolean(R.styleable.StatusView_sv_line_enabled, false) ? VISIBLE : INVISIBLE);

        typedArray.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mUnbinder.unbind();
    }
}
