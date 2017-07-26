package com.bsmwireless.widgets.graphview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.bsmwireless.common.App;
import com.bsmwireless.data.storage.FontCache;
import com.bsmwireless.widgets.alerts.DutyType;
import com.bsmwireless.widgets.common.VerticalTextView;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HOSGraphLabelView extends RelativeLayout {

    private Unbinder mUnbinder;

    @Inject
    FontCache mFontCache;

    private View mRootView;

    @BindView(R.id.label_textview)
    VerticalTextView mLabelTV;

    private int mType;
    private boolean mShowLabel;

    public HOSGraphLabelView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public HOSGraphLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public HOSGraphLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        App.getComponent().inject(this);

        mRootView = inflate(context, R.layout.hos_graph_label_vew, this);

        mLabelTV.setTypeface(mFontCache.get(FontCache.BEBAS_NEUE));

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HOSGraphLabelView, defStyleAttr, 0);
        try {
            mType = typedArray.getInteger(R.styleable.HOSGraphLabelView_duty, 0);
            mShowLabel = typedArray.getBoolean(R.styleable.HOSGraphLabelView_showLabel, true);

            mRootView.setBackgroundColor(ContextCompat.getColor(getContext(), DutyType.getColorById(mType)));

            mLabelTV.setText(getResources().getString(DutyType.getNameById(mType)));
            mLabelTV.setVisibility(mShowLabel ? VISIBLE : GONE);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mUnbinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    protected void onDetachedFromWindow() {
        mUnbinder.unbind();
        super.onDetachedFromWindow();
    }
}
