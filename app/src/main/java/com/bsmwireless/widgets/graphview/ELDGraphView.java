package com.bsmwireless.widgets.graphview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.ViewUtils;
import com.bsmwireless.data.storage.FontCache;
import com.bsmwireless.models.ELDEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;

public class ELDGraphView extends View {

    private final int GRID_WIDTH_DP = 1;
    private final int LINE_WIDTH_DP = 3;

    @Inject
    FontCache mFontCache;

    private final int mDutyStatusCount = 4;
    private final int mHoursCount = 24;

    private float mTopOffset;
    private float mLeftOffset;
    private float mRightOffset;
    private float mBottomOffset;
    private float mTextSize;
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

    private List<ELDEvent> mLogs;

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

    public void setLogs(List<ELDEvent> logs) {
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
        mTextSize = getResources().getDimension(R.dimen.text_size_extra_smaller);

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
        mHeaderPaint.setTypeface(mFontCache.get(FontCache.SANS_SERIF));
        mHeaderPaint.setTextSize(mTextSize);
        mHeaderPaint.setFakeBoldText(true);

        mBarPaint = new Paint();
        mBarPaint.setAntiAlias(true);
        mBarPaint.setColor(ContextCompat.getColor(getContext(), R.color.offduty_light));
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setStrokeWidth(ViewUtils.convertDpToPixels(LINE_WIDTH_DP, getContext()));
    }

    private void drawGridBackground(Canvas canvas) {
        float xDelta = mGraphWidth / mHoursCount;
        for(int i = 0; i <= mHoursCount; i++) {
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

            canvas.drawLine (tickX, mGraphTop, tickX, mGraphTop + mGraphHeight, mGridPaint);
        }
    }

    private void drawLog(List<ELDEvent> logData, Canvas canvas) {
        if (logData == null || logData.size() == 0) {
            return;
        }

        float gridUnit = mGraphWidth / (mHoursCount * 60);

        int firstTypeId = DutyType.values()[0].getTypeId();

        float x1, x2, y1, y2;
        x1 = mGraphLeft;
        y1 = mGraphTop + (logData.get(0).getEventType() - firstTypeId) * mSegmentHeight + mSegmentHeight / 2;

        for(int i = 1; i < logData.size(); i++) {
            ELDEvent event = logData.get(i);
            ELDEvent prevEvent = logData.get(i - 1);

            Long logDate = event.getEventTime();
            Long prevLogDate = prevEvent.getEventTime();

            long timeStamp = (logDate - prevLogDate) / (60 * 1000);

            x2 = x1 + timeStamp * gridUnit;
            y2 = mGraphTop + (event.getEventType() - firstTypeId) * mSegmentHeight + mSegmentHeight / 2;

            mBarPaint.setColor(ContextCompat.getColor(getContext(), DutyType.getTypeColorById(event.getEventType())));
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
