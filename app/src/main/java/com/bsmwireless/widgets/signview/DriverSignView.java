package com.bsmwireless.widgets.signview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.R;


public class DriverSignView extends View implements View.OnTouchListener {

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
        mPaint.setStrokeWidth(6);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(ContextCompat.getColor(context, R.color.signature_border));
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(1);
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
            }
            case MotionEvent.ACTION_MOVE: {
                mCurrentPath.quadTo(mPrevX, mPrevY, x, y);

                mDatas.add(new Point((int) x, (int) y));

                mPrevX = x;
                mPrevY = y;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                mCurrentPath.quadTo(mPrevX, mPrevY, x, y);
                mDatas.add(new Point(-1, -1));
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

        String rtnSign = "";
        for (int i = 1; i < mDatas.size(); i++) {
            if (mDatas.get(i - 1).x < 0) {
                continue;
            }

            if (i > 1) {
                rtnSign += ";" + (mDatas.get(i - 1).x - xShift) + "," + mDatas.get(i - 1).y + ";" + (mDatas.get(i).x - xShift) + "," + mDatas.get(i).y;
            } else {
                rtnSign += (mDatas.get(i - 1).x - xShift) + "," + mDatas.get(i - 1).y + ";" + (mDatas.get(i).x - xShift) + "," + mDatas.get(i).y;
            }
        }
        return rtnSign;
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
        mBitmap.recycle();
        mBitmap = null;
        mDatas.clear();
        invalidate();
    }

    private void drawSignature() {
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Path path = new Path();
        for (int i = 1; i < mDatas.size(); i++) {
            if (mDatas.get(i).x < 0 || mDatas.get(i).y < 0 || mDatas.get(i - 1).x < 0 || mDatas.get(i - 1).y < 0) {
                Canvas canvas = new Canvas(mBitmap);
                canvas.drawPath(path, mPaint);
                path = new Path();
                continue;
            }
            path.quadTo(mDatas.get(i - 1).x,
                    mDatas.get(i - 1).y,
                    mDatas.get(i).x,
                    mDatas.get(i).y);
        }
        invalidate();
    }

    private void drawBorder(Canvas canvas) {
        mBorderPaint.setPathEffect(null);
        canvas.drawLine(0, 0, mWidth, 0, mBorderPaint);
        canvas.drawLine(0, 0, 0, mHeight, mBorderPaint);
        canvas.drawLine(mWidth, mHeight, mWidth, 0, mBorderPaint);
        canvas.drawLine(mWidth, mHeight, 0, mHeight, mBorderPaint);

        mBorderPaint.setPathEffect(new DashPathEffect(new float[] { 10, 10 }, 0));
        Path path = new Path();
        path.moveTo(0, 2 * mHeight / 3);
        path.quadTo(0, 2 * mHeight / 3, mWidth, 2 * mHeight / 3);
        canvas.drawPath(path, mBorderPaint);
    }
}
