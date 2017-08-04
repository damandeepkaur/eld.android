package com.bsmwireless.widgets.logs.graphview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.bsmwireless.common.utils.ViewUtils;
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.R;

public class ELDGraphView extends View {

    private final static int SEC_IN_MIN = 60;
    private final static int MS_IN_SEC = 1000;

    private final int GRID_WIDTH_DP = 1;
    private final int LINE_WIDTH_DP = 3;
    private final int mDutyStatusCount = 4;
    private final int mHoursCount = 24;
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
    private Paint mBarPaint;

    private List<ELDEvent> mLogs;
    private Bitmap mBitmap;

    public ELDGraphView(Context context) {
        super(context);
        init();
    }

    public ELDGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ELDGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
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
        mGridPaint.setStrokeWidth(ViewUtils.convertPixelsToDp(GRID_WIDTH_DP, getContext()));

        mHeaderPaint = new Paint();
        mHeaderPaint.setAntiAlias(true);
        mHeaderPaint.setDither(true);
        mHeaderPaint.setColor(ContextCompat.getColor(getContext(), R.color.graph_header_paint_color));
        mHeaderPaint.setSubpixelText(true);
        mHeaderPaint.setTextSize(mTextSize);
        mHeaderPaint.setFakeBoldText(true);

        mBarPaint = new Paint();
        mBarPaint.setAntiAlias(true);
        mBarPaint.setColor(ContextCompat.getColor(getContext(), R.color.offduty_light));
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setStrokeWidth(ViewUtils.convertDpToPixels(LINE_WIDTH_DP, getContext()));

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

        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        } else {
            Bitmap bitmap = getDrawingCache();
            if (mBitmap != null) mBitmap.recycle();
            mBitmap = bitmap.copy(bitmap.getConfig(), true);
            Canvas bitmapCanvas = new Canvas(mBitmap);
            drawGridBackground(bitmapCanvas);
            drawLog(mLogs, bitmapCanvas);
        }
    }

    public void setLogs(final List<ELDEvent> logs) {
        mLogs = logs;
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

    private void drawLog(List<ELDEvent> logData, Canvas canvas) {

        if (logData == null || logData.size() == 0) {
            return;
        }

        float gridUnit = mGraphWidth / (mHoursCount * SEC_IN_MIN);

        int firstTypeId = DutyType.values()[0].getId();

        float x1, x2, y1, y2;
        x1 = mGraphLeft;
        y1 = mGraphTop + (logData.get(0).getEventType() - firstTypeId) * mSegmentHeight + mSegmentHeight / 2;

        for (int i = 1; i < logData.size(); i++) {
            ELDEvent event = logData.get(i);
            ELDEvent prevEvent = logData.get(i - 1);

            Long logDate = event.getEventTime();
            Long prevLogDate = prevEvent.getEventTime();

            long timeStamp = (logDate - prevLogDate) / (SEC_IN_MIN * MS_IN_SEC);

            x2 = x1 + timeStamp * gridUnit;
            y2 = mGraphTop + (event.getEventType() - firstTypeId) * mSegmentHeight + mSegmentHeight / 2;

            mBarPaint.setColor(ContextCompat.getColor(getContext(), DutyType.getColorById(event.getEventType())));
            mBarPaint.setStrokeWidth(ViewUtils.convertDpToPixels(LINE_WIDTH_DP, getContext()));

            canvas.drawLine(x1, y1, x2, y1, mBarPaint);
            if (y1 != y2) {
                mBarPaint.setColor(ContextCompat.getColor(getContext(), R.color.offduty_light));
                mBarPaint.setStrokeWidth(ViewUtils.convertDpToPixels(GRID_WIDTH_DP, getContext()));

                canvas.drawLine(x2, y1, x2, y2, mBarPaint);
            }

            x1 = x2;
            y1 = y2;
        }
    }
}
