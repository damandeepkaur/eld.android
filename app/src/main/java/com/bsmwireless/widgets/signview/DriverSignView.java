package com.bsmwireless.widgets.signview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.R;


public class DriverSignView extends View implements View.OnTouchListener {

    private static final int LINE_WIDTH_DP = 12;
    private static final int BORDER_WIDTH_DP = 2;
    private static final int DOTTED_LINE_WIDTH_DP = 10;

    private Paint mPaint;
    private Paint mBorderPaint;

    private Bitmap mBitmap;
    private Path mCurrentPath;

    private int mWidth;
    private int mHeight;

    private float mPrevX;
    private float mPrevY;

    private List<Point> mDatas;

    private boolean mIsEditing;

    public DriverSignView(Context context) {
        super(context);
        init(context);
    }

    public DriverSignView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DriverSignView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(convertPixelsToDp(LINE_WIDTH_DP, context));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setFilterBitmap(true);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(ContextCompat.getColor(context, R.color.signature_border));
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(convertPixelsToDp(BORDER_WIDTH_DP, context));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mWidth = right - left;
        mHeight = bottom - top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        }
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }

        canvas.drawBitmap(mBitmap, 0, 0, mPaint);

        if (mIsEditing) {
            drawBorder(canvas);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mCurrentPath = new Path();
                mCurrentPath.moveTo(x, y);

                mDatas.add(new Point((int) x, (int) y));

                mPrevX = x;
                mPrevY = y;

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mCurrentPath.quadTo(mPrevX, mPrevY, x, y);

                mDatas.add(new Point((int) x, (int) y));

                mPrevX = x;
                mPrevY = y;

                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                mCurrentPath.quadTo(mPrevX, mPrevY, x, y);
                mDatas.add(new Point(-1, -1));

                break;
            }
        }

        if (mBitmap != null) {
            Canvas canvas = new Canvas(mBitmap);
            canvas.drawPath(mCurrentPath, mPaint);
        }

        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }

        invalidate();

        return true;
    }

    public String getSignatureString() {
        int xShift = Integer.MAX_VALUE;
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).x > -1 && mDatas.get(i).x < xShift) {
                xShift = mDatas.get(i).x;
            }
        }

        StringBuilder rtnSign = new StringBuilder();
        for (int i = 1; i < mDatas.size(); i++) {
            if (mDatas.get(i - 1).x < 0) {
                continue;
            }

            if (i > 1) {
                rtnSign.append(";");
            }

            rtnSign.append(mDatas.get(i - 1).x - xShift)
                   .append(",")
                   .append(mDatas.get(i - 1).y)
                   .append(";")
                   .append(mDatas.get(i).x - xShift)
                   .append(",")
                   .append(mDatas.get(i).y);
        }

        return rtnSign.toString();
    }

    public void setSignatureString(String signature) {
        String[] signPoints = signature.split(";");

        if (signPoints.length > 1) {
            mDatas = new ArrayList<>();
            String[] curPoint;
            for (int i = 1; i < signPoints.length; i = i + 2) {
                if (signPoints[i - 1].length() > 0) {
                    curPoint = signPoints[i - 1].split(",");
                    mDatas.add(new Point(Integer.parseInt(curPoint [0]), Integer.parseInt(curPoint [1])));
                }
                if (signPoints[i].length() > 0) {
                    curPoint = signPoints [i].split(",");
                    if (curPoint.length > 1) {
                        mDatas.add(new Point(Integer.parseInt(curPoint[0]), Integer.parseInt(curPoint[1])));
                    } else {
                        mDatas.add(new Point(Integer.parseInt(curPoint[0]), 0));
                    }
                }
            }
            drawSignature();
        }
    }

    public void setEditable(boolean editable) {
        mIsEditing = editable;
        setOnTouchListener(mIsEditing ? this : null);
        invalidate();
    }

    public void clear() {
        if (mBitmap != null) {
            Canvas canvas = new Canvas(mBitmap);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        mDatas.clear();
        invalidate();
    }

    private void drawSignature() {
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(mBitmap);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Path path = new Path();

        for (int i = 1; i < mDatas.size(); i++) {
            if (mDatas.get(i).x < 0 || mDatas.get(i).y < 0 || mDatas.get(i - 1).x < 0 || mDatas.get(i - 1).y < 0) {
                path = new Path();
                continue;
            }

            path.moveTo(mDatas.get(i - 1).x, mDatas.get(i - 1).y);
            path.quadTo(mDatas.get(i - 1).x,
                    mDatas.get(i - 1).y,
                    mDatas.get(i).x,
                    mDatas.get(i).y);

            canvas.drawPath(path, mPaint);
        }

        invalidate();
    }

    private void drawBorder(Canvas canvas) {
        mBorderPaint.setPathEffect(null);
        canvas.drawLine(0, 0, mWidth, 0, mBorderPaint);
        canvas.drawLine(0, 0, 0, mHeight, mBorderPaint);
        canvas.drawLine(mWidth, mHeight, mWidth, 0, mBorderPaint);
        canvas.drawLine(mWidth, mHeight, 0, mHeight, mBorderPaint);

        mBorderPaint.setPathEffect(new DashPathEffect(new float[] { convertPixelsToDp(DOTTED_LINE_WIDTH_DP, getContext()), convertPixelsToDp(DOTTED_LINE_WIDTH_DP, getContext()) }, 0));
        Path path = new Path();
        path.moveTo(0, 2 * mHeight / 3);
        path.quadTo(0, 2 * mHeight / 3, mWidth, 2 * mHeight / 3);
        canvas.drawPath(path, mBorderPaint);
    }

    private float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
