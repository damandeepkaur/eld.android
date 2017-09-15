package com.bsmwireless.widgets.logs.graphview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.bsmwireless.common.utils.ViewUtils;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.bsmuniversal.com.R;

public class ELDGraphView extends View {

    private final static int SEC_IN_MIN = 60;
    private final static int MS_IN_MIN = 60 * 1000;
    private final static int MS_IN_DAY = 24 * 60 * MS_IN_MIN;
    private static final int DOTTED_LINE_WIDTH_DP = 1;

    private final float GRID_WIDTH_DP = 0.3f;
    private final float VERTICAL_LINE_WIDTH_DP = 1f;
    private final float LINE_WIDTH_DP = 3f;
    private final int mDutyStatusCount = 4;
    private final int mHoursCount = 24;
    private boolean invalidateLogsData;
    private float mTopOffset;
    private float mLeftOffset;
    private float mRightOffset;
    private float mBottomOffset;
    private float mTextSize;
    private float mGraphHeight;
    private float mGraphWidth;
    private float mGraphLeft;
    private float mGraphTop;
    private float mSegmentHeight;
    private Paint mGridPaint;
    private Paint mHeaderPaint;
    private Paint mHorizontalLinesPaint;
    private Paint mVerticalLinesPaint;
    private List<DrawableLog> mLogs;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private long mStartDayUnixTimeInMs;

    public ELDGraphView(Context context) {
        super(context);
        init(context);
    }

    public ELDGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ELDGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mLogs = new ArrayList<>();

        mTopOffset = getResources().getDimensionPixelSize(R.dimen.graph_view_top_offset);
        mLeftOffset = getResources().getDimensionPixelSize(R.dimen.graph_view_left_offset);
        mRightOffset = getResources().getDimensionPixelSize(R.dimen.graph_view_right_offset);
        mBottomOffset = getResources().getDimensionPixelSize(R.dimen.graph_view_bottom_offset);
        mTextSize = getResources().getDimension(R.dimen.text_size_smallest);

        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(true);
        mGridPaint.setColor(ContextCompat.getColor(getContext(), R.color.graph_grid_paint_color));
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setStrokeWidth(ViewUtils.convertDpToPixels(GRID_WIDTH_DP, getContext()));

        mHeaderPaint = new Paint();
        mHeaderPaint.setAntiAlias(true);
        mHeaderPaint.setDither(true);
        mHeaderPaint.setColor(ContextCompat.getColor(getContext(), R.color.graph_header_paint_color));
        mHeaderPaint.setSubpixelText(true);
        mHeaderPaint.setTextSize(mTextSize);
        mHeaderPaint.setFakeBoldText(true);

        mHorizontalLinesPaint = new Paint();
        mHorizontalLinesPaint.setAntiAlias(true);
        mHorizontalLinesPaint.setColor(ContextCompat.getColor(context, DutyType.OFF_DUTY.getColor()));
        mHorizontalLinesPaint.setStyle(Paint.Style.STROKE);
        mHorizontalLinesPaint.setStrokeWidth(ViewUtils.convertDpToPixels(LINE_WIDTH_DP, getContext()));

        mVerticalLinesPaint = new Paint();
        mVerticalLinesPaint.setAntiAlias(true);
        mVerticalLinesPaint.setColor(ContextCompat.getColor(context, DutyType.OFF_DUTY.getColor()));
        mVerticalLinesPaint.setStyle(Paint.Style.STROKE);
        mVerticalLinesPaint.setStrokeWidth(ViewUtils.convertDpToPixels(VERTICAL_LINE_WIDTH_DP, getContext()));

        mBitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);


        invalidateLogsData = true;

        setDrawingCacheEnabled(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        float width = right - left;
        float height = bottom - top;

        mGraphWidth = (width - (mLeftOffset + mRightOffset));
        mGraphHeight = (height - (mTopOffset + mBottomOffset));

        mGraphLeft = mLeftOffset;
        mGraphTop = mTopOffset;

        mSegmentHeight = mGraphHeight / mDutyStatusCount;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (invalidateLogsData) {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            mBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), conf);
            Canvas bitmapCanvas = new Canvas(mBitmap);
            drawGridBackground(bitmapCanvas);
            drawLog(mLogs, bitmapCanvas);
            invalidateLogsData = false;
        }

        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        }
    }

    public void setLogs(final List<DrawableLog> logs, long startDayIime) {
        mLogs = logs;
        mStartDayUnixTimeInMs = startDayIime;
        invalidateLogsData = true;
        invalidate();
    }

    private void drawGridBackground(Canvas canvas) {
        float xDelta = mGraphWidth / mHoursCount;
        for (int i = 0; i <= mHoursCount; i++) {
            float tickX = mGraphLeft + (i * xDelta);

            String headerStr;

            if (i <= mHoursCount) {
                switch (i) {
                    case 0: {
                        headerStr = "AM";
                        break;
                    }
                    case 12: {
                        headerStr = "N";
                        break;
                    }
                    case 24: {
                        headerStr = "PM";
                        break;
                    }
                    default: {
                        headerStr = String.valueOf(i % 12);
                        break;
                    }
                }

                float lineWidth = mHeaderPaint.measureText(headerStr, 0, headerStr.length());
                canvas.drawText(headerStr, tickX - lineWidth / 2, mGraphTop + mGraphHeight + mTextSize, mHeaderPaint);
            }

            canvas.drawLine(tickX, mGraphTop, tickX, mGraphTop + mGraphHeight, mGridPaint);
        }
    }

    private void drawLog(List<DrawableLog> logData, Canvas canvas) {

        if (logData == null || logData.size() == 0) {
            return;
        }

        float gridUnit = mGraphWidth / (mHoursCount * SEC_IN_MIN);

        float x1, x2, y1, y2;

        DrawableLog firstEvent = logData.get(0);
        long firstLogDayTime = (firstEvent.getEventTime() - mStartDayUnixTimeInMs) / MS_IN_MIN;
        x1 = mGraphLeft + firstLogDayTime * gridUnit;
        y1 = mGraphTop + firstEvent.getEventCode() * mSegmentHeight + mSegmentHeight / 2;
        int color;

        for (int i = 1; i < logData.size(); i++) {
            DrawableLog event = logData.get(i);
            DrawableLog prevEvent = logData.get(i - 1);

            Long logDate = event.getEventTime();
            Long prevLogDate = prevEvent.getEventTime();

            long timeStamp = (logDate - prevLogDate) / MS_IN_MIN;

            x2 = x1 + timeStamp * gridUnit;
            y2 = mGraphTop + event.getEventCode() * mSegmentHeight + mSegmentHeight / 2;

            color = ContextCompat.getColor(getContext(), prevEvent.getEventDutyType().getColor());
            mHorizontalLinesPaint.setColor(color);

            if (prevEvent.isSpecialStatus()) {
                mHorizontalLinesPaint.setPathEffect(
                        new DashPathEffect(
                                new float[]{ViewUtils.convertDpToPixels(DOTTED_LINE_WIDTH_DP,
                                        getContext()),
                                        ViewUtils.convertDpToPixels(DOTTED_LINE_WIDTH_DP, getContext())
                                }, 0));
            } else {
                mHorizontalLinesPaint.setPathEffect(null);
            }

            canvas.drawLine(x1, y1, x2, y1, mHorizontalLinesPaint);

            if (y1 != y2) {
                canvas.drawLine(x2, y1, x2, y2, mVerticalLinesPaint);
            }

            x1 = x2;
            y1 = y2;
        }

        DrawableLog log = logData.get(logData.size() - 1);
        color = ContextCompat.getColor(getContext(), log.getEventDutyType().getColor());
        mHorizontalLinesPaint.setColor(color);

        if (log.isSpecialStatus()) {
            mHorizontalLinesPaint.setPathEffect(
                    new DashPathEffect(
                            new float[]{ViewUtils.convertDpToPixels(DOTTED_LINE_WIDTH_DP,
                                    getContext()),
                                    ViewUtils.convertDpToPixels(DOTTED_LINE_WIDTH_DP, getContext())
                            }, 0));
        } else {
            mHorizontalLinesPaint.setPathEffect(null);
        }

        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (mStartDayUnixTimeInMs + MS_IN_DAY < Calendar.getInstance().getTimeInMillis()) {
            x2 = mGraphWidth + mGraphLeft;
        } else {
            long timeStamp = (currentTime - log.getEventTime()) / MS_IN_MIN;
            x2 = x1 + timeStamp * gridUnit;
        }

        canvas.drawLine(x1, y1, x2, y1, mHorizontalLinesPaint);
    }
}
