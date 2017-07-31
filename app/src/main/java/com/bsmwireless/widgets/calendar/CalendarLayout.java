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

public class CalendarLayout extends LinearLayout implements View.OnClickListener{

    private static final int DEFAULT_DAYS_COUNT = 30;

    private View mRootView;

    private Unbinder mUnbinder;

    private CalendarAdapter mAdapter;

    private int mDaysCount;

    private LinearLayoutManager mLayoutManager;

    private OnItemSelectListener mListener;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.left)
    Button mLeftButton;
    @BindView(R.id.right)
    Button mRightButton;

    public interface OnItemSelectListener {
        void onItemSelected(CalendarItem log);
    }

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

        mLayoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CalendarAdapter(getContext(), getItems(), this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = mLayoutManager.findLastVisibleItemPosition();
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

    @Override
    public void onClick(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        mAdapter.setSelectedItem(position);
        if (mListener != null) {
            CalendarItem item = mAdapter.getItemByPosition(position);
            mListener.onItemSelected(item);
        }
    }

    @OnClick(R.id.left)
    void onLeftClicked() {
        int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastPosition = mLayoutManager.findLastVisibleItemPosition();
        int newPosition = lastPosition + (lastPosition - firstPosition) - 1;
        mRecyclerView.smoothScrollToPosition(newPosition < mAdapter.getItemCount() ? newPosition : mAdapter.getItemCount() - 1);
    }

    @OnClick(R.id.right)
    void onRightClicked() {
        int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastPosition = mLayoutManager.findLastVisibleItemPosition();
        int newPosition = firstPosition - (lastPosition - firstPosition) + 1;
        mRecyclerView.smoothScrollToPosition(newPosition > 0 ? newPosition : 0);
    }

    public void setLogs(List<LogSheetHeader> logs) {
        if (mAdapter != null) {
            mAdapter.updateLogs(logs);
        }
    }

    public void setOnItemSelectedListener(OnItemSelectListener listener) {
        mListener = listener;
    }

    private List<CalendarItem> getItems() {
        Calendar calendar = Calendar.getInstance();
        List<CalendarItem> logs = new ArrayList<>();
        long dayMS = CalendarItem.ONE_DAY_MS;
        int itemsCount = mDaysCount > 0 ? mDaysCount : DEFAULT_DAYS_COUNT;
        for (int i = 0; i < itemsCount; i++) {
            Long time = calendar.getTime().getTime();
            CalendarItem item = new CalendarItem(time, null);
            logs.add(item);
            time -= dayMS;
            calendar.setTime(new Date(time));
        }
        return logs;
    }
}
