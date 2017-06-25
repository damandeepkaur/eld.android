package com.bsmwireless.widgets.graphview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.bsmwireless.common.App;
import com.bsmwireless.data.storage.FontCache;
import com.bsmwireless.models.DriverLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;

public class HOSGraphView extends View {

    @Inject
    FontCache mFontCache;

    private final int mDutyStatusCount = 4;
    private final int mHoursCount = 24;
    private final int mSegmentsPerHour = 4;

    private float mTopOffset;
    private float mLeftOffset;
    private float mRightOffset;
    private float mBottomOffset;
    private float mWidth;
    private float mHeight;
    private float mGraphHeight;
    private float mGraphWidth;
    private float mGraphLeft;
    private float mGraphTop;
    private float mSegmentHeight;

    private Paint mGridPaint;
    private Paint mHeaderPaint;
    private Paint mBarPaint;

    private List<DriverLog> mLogs;

    public HOSGraphView(Context context) {
        super(context);
        init();
    }

    public HOSGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HOSGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mWidth = right - left;
        mHeight = bottom - top;

        mGraphWidth = (mWidth - (mLeftOffset + mRightOffset));
        mGraphHeight = (mHeight - (mTopOffset + mBottomOffset));

        mGraphLeft = mLeftOffset;
        mGraphTop = mTopOffset;

        mSegmentHeight = mGraphHeight / mDutyStatusCount;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawGridBackground(canvas);

        drawLog(mLogs, canvas);
    }

    public void setLogs(List<DriverLog> logs) {
        mLogs = new ArrayList<>(logs);
        invalidate();
    }

    private void init() {
        App.getComponent().inject(this);

        mLogs = new ArrayList<>();

        mTopOffset = getResources().getDimensionPixelSize(R.dimen.graph_view_top_offset);
        mLeftOffset = getResources().getDimensionPixelSize(R.dimen.graph_view_left_offset);
        mRightOffset = getResources().getDimensionPixelSize(R.dimen.graph_view_right_offset);
        mBottomOffset = getResources().getDimensionPixelSize(R.dimen.graph_view_bottom_offset);

        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(true);
        mGridPaint.setColor(ContextCompat.getColor(getContext(), R.color.graph_grid_paint_color));
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setStrokeWidth(1);

        mHeaderPaint = new Paint();
        mHeaderPaint.setAntiAlias(true);
        mHeaderPaint.setDither(true);
        mHeaderPaint.setColor(ContextCompat.getColor(getContext(), R.color.graph_header_paint_color));
        mHeaderPaint.setSubpixelText(true);
        mHeaderPaint.setTypeface(mFontCache.get(FontCache.BEBAS_NEUE));
        mHeaderPaint.setTextSize(getResources().getDimension(R.dimen.textview_smaller));
        mHeaderPaint.setFakeBoldText(true);

        mBarPaint = new Paint();
        mBarPaint.setAntiAlias(true);
        mBarPaint.setColor(ContextCompat.getColor(getContext(), R.color.graph_bar_paint_color));
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setStrokeWidth(4);
    }

    private void drawGridBackground(Canvas canvas)
    {
        // paint the interior of the Box
        for (int i = 0; i <= mDutyStatusCount; i++)
        {
            canvas.drawLine(mGraphLeft, mGraphTop + (i * mSegmentHeight), mGraphLeft + mGraphWidth, mGraphTop + (i * mSegmentHeight), mGridPaint);
        }

        // draw the tick lines
        float xDelta = mGraphWidth / (mHoursCount * mSegmentsPerHour);
        for (int i = 0; i <= mHoursCount * mSegmentsPerHour; i++)
        {
            float tickX = mGraphLeft + (i * xDelta);
            float tickHeight = mSegmentHeight / 4;
            float delta = 0;
            if (i % 2 == 0)
                tickHeight = mSegmentHeight / 2;
            if (i % mSegmentsPerHour == 0) {
                tickHeight = mSegmentHeight;
                delta = 4;
                // draw header text
                int j = i / 4;
                if (j < mHoursCount) {
                    canvas.drawText(String.format("%02d", j), tickX - 8, mGraphTop - mTopOffset / 3, mHeaderPaint);
                }
            }

            canvas.drawLine (tickX, mGraphTop - delta, tickX, mGraphTop + tickHeight, mGridPaint);
            canvas.drawLine (tickX, mGraphTop + mSegmentHeight, tickX, mGraphTop + mSegmentHeight + tickHeight, mGridPaint);
            canvas.drawLine (tickX, mGraphTop + mSegmentHeight * 3, tickX, mGraphTop + mSegmentHeight * 3 - tickHeight, mGridPaint);
            canvas.drawLine (tickX, mGraphTop + mSegmentHeight * 4 + delta, tickX, mGraphTop + mSegmentHeight * 4 - tickHeight, mGridPaint);
        }
    }

    private void drawLog(List<DriverLog> logdata, Canvas canvas)
    {
        if (logdata == null || logdata.size() == 0) {
            return;
        }

        //grid pixel size per minute gWidth/(24*60)
        float gridunit = mGraphWidth / (mHoursCount * 60);

        float x1, x2, y1, y2;
        x1 = mGraphLeft;
        y1 = mGraphTop + (logdata.get(0).getType() - 101) * mSegmentHeight + mSegmentHeight / 2;

        for (int i = 1; i < logdata.size(); i++)
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                Date logDate = format.parse(logdata.get(i).getLogTime());
                Date prevLogDate = format.parse(logdata.get(i - 1).getLogTime());

                long ts = (logDate.getTime() - prevLogDate.getTime()) / (60 * 1000);

                x2 = x1 + ts * gridunit;
                y2 = mGraphTop + (logdata.get(i).getType() - 101) * mSegmentHeight + mSegmentHeight / 2;

                //horizantal log line
                canvas.drawLine(x1, y1, x2, y1, mBarPaint);

                //vertical log line
                canvas.drawLine(x2, y1, x2, y2, mBarPaint);

                x1 = x2;
                y1 = y2;

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
