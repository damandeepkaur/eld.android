package com.bsmwireless.widgets.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bsmwireless.models.LogSheetHeader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CalendarLayout extends LinearLayout {

    private static final int DEFAULT_DAYS_COUNT = 30;

    private View mRootView;

    private Unbinder mUnbinder;

    private CalendarAdapter mAdapter;

    private int mDaysCount;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.left)
    Button mLeftButton;
    @BindView(R.id.right)
    Button mRightButton;

    public CalendarLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CalendarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CalendarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mRootView = inflate(context, R.layout.calendar_layout, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarLayout, defStyleAttr, 0);
        mDaysCount = typedArray.getInt(R.styleable.CalendarLayout_daysCount, DEFAULT_DAYS_COUNT);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mUnbinder = ButterKnife.bind(this, mRootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, true));
        mAdapter = new CalendarAdapter(getContext(), getItems());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    int firstPosition = layoutManager.findFirstVisibleItemPosition();
                    int lastPosition = layoutManager.findLastVisibleItemPosition();
                    mRightButton.setEnabled(firstPosition != 0);
                    mLeftButton.setEnabled(lastPosition != (mAdapter.getItemCount() - 1));
                }
            }
        });
        mRecyclerView.smoothScrollToPosition(0);
        mRightButton.setEnabled(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        mUnbinder.unbind();
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.left)
    void onLeftClicked() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();
        int newPosition = lastPosition + (lastPosition - firstPosition) - 1;
        mRecyclerView.smoothScrollToPosition(newPosition < mAdapter.getItemCount() ? newPosition : mAdapter.getItemCount() - 1);
    }

    @OnClick(R.id.right)
    void onRightClicked() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();
        int newPosition = firstPosition - (lastPosition - firstPosition) + 1;
        mRecyclerView.smoothScrollToPosition(newPosition > 0 ? newPosition : 0);
    }

    public void setLogs(List<LogSheetHeader> logs) {
        if (mAdapter != null) {
            mAdapter.updateLogs(logs);
        }
    }

    private List<CalendarItem> getItems() {
        Calendar calendar = Calendar.getInstance();
        List<CalendarItem> logs = new ArrayList<>();
        long dayMS = CalendarItem.ONE_DAY_MS;
        for (int i = 0; i < (mDaysCount > 0 ? mDaysCount : DEFAULT_DAYS_COUNT); i++) {
            Long time = calendar.getTime().getTime();
            CalendarItem item = new CalendarItem(time, null);
            logs.add(item);
            time -= dayMS;
            calendar.setTime(new Date(time));
        }
        return logs;
    }
}
