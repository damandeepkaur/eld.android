package com.bsmwireless.widgets.signview;

import android.content.Context;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.bsmwireless.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.R;

import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;


public class DriverSignView extends View implements View.OnTouchListener {

    private static final int LINE_WIDTH_DP = 12;
    private static final int BORDER_WIDTH_DP = 4;
    private static final int DOTTED_LINE_WIDTH_DP = 20;

    private Paint mPaint;
    private Paint mBorderPaint;

    private Bitmap mBitmap;
    private Path mCurrentPath;

    private int mWidth;
    private int mHeight;

    private float mPrevX;
    private float mPrevY;
    private List<Point> mData;

    private boolean mIsEditing;
    private boolean mIsDataWasSetted;

    private CompositeDisposable mDisposables;

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
        mPaint.setStrokeWidth(ViewUtils.convertPixelsToDp(LINE_WIDTH_DP, context));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setFilterBitmap(true);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(ContextCompat.getColor(context, R.color.signature_border));
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(ViewUtils.convertPixelsToDp(BORDER_WIDTH_DP, context));

        mDisposables = new CompositeDisposable();
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
        if (mData == null) {
            mData = new ArrayList<>();
        }

        if (mIsDataWasSetted) {
            drawSignatureFromData(mBitmap);
            mIsDataWasSetted = false;
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

                mData.add(new Point((int) x, (int) y));

                mPrevX = x;
                mPrevY = y;

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mCurrentPath.quadTo(mPrevX, mPrevY, x, y);

                mData.add(new Point((int) x, (int) y));

                mPrevX = x;
                mPrevY = y;

                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                mCurrentPath.quadTo(mPrevX, mPrevY, x, y);
                mData.add(new Point(-1, -1));
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

    @Override
    protected void onDetachedFromWindow() {
        mDisposables.dispose();
        super.onDetachedFromWindow();
    }

    public String getSignature() {
        return DriverSignView.pointsToString(mData);
    }

    public void setSignature(String signature) {
        mData = DriverSignView.stringToPoints(signature);
        mIsDataWasSetted = true;
        invalidate();
    }

    public void setEditable(boolean editable) {
        mIsEditing = editable;
        setOnTouchListener(mIsEditing ? this : null);
        invalidate();
    }

    public boolean isEditing() {
        return mIsEditing;
    }

    public void clear() {
        if (mBitmap != null) {
            Canvas canvas = new Canvas(mBitmap);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        mData.clear();
        invalidate();
    }

    private void drawBorder(Canvas canvas) {
        mBorderPaint.setPathEffect(null);
        canvas.drawLine(0, 0, mWidth, 0, mBorderPaint);
        canvas.drawLine(0, 0, 0, mHeight, mBorderPaint);
        canvas.drawLine(mWidth, mHeight, mWidth, 0, mBorderPaint);
        canvas.drawLine(mWidth, mHeight, 0, mHeight, mBorderPaint);

        mBorderPaint.setPathEffect(new DashPathEffect(new float[] { ViewUtils.convertPixelsToDp(DOTTED_LINE_WIDTH_DP, getContext()), ViewUtils.convertPixelsToDp(DOTTED_LINE_WIDTH_DP, getContext()) }, 0));
        Path path = new Path();
        path.moveTo(0, 2 * mHeight / 3);
        path.quadTo(0, 2 * mHeight / 3, mWidth, 2 * mHeight / 3);
        canvas.drawPath(path, mBorderPaint);
    }

    private void drawSignatureFromData(Bitmap bitmap) {
        Disposable disposable = PublishSubject.create((ObservableOnSubscribe<Bitmap>) e -> {
            Path path = new Path();

            Bitmap bmp = Bitmap.createBitmap(bitmap);

            Canvas canvas = new Canvas(bmp);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            for (int i = 2; i < mData.size(); i+=2) {
                if ((mData.get(i).x < 0 || mData.get(i).y < 0) ||
                        (mData.get(i - 1).x < 0 || mData.get(i - 1).y < 0) ||
                        (mData.get(i - 2).x < 0 || mData.get(i - 2).y < 0)) {
                    path = new Path();
                    continue;
                }

                path.moveTo(mData.get(i - 2).x, mData.get(i - 2).y);
                path.cubicTo(mData.get(i - 2).x,
                        mData.get(i - 2).y,
                        mData.get(i - 1).x,
                        mData.get(i - 1).y,
                        mData.get(i).x,
                        mData.get(i).y);

                canvas.drawPath(path, mPaint);
            }

            e.onNext(bmp);
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(bmp -> {
                if (mBitmap != null) {
                    mBitmap.recycle();
                }
                mBitmap = bmp;
                invalidate();
            });
        mDisposables.add(disposable);
    }

    public static List<Point> stringToPoints(String signature) {
        String[] signPoints = signature.split(";");
        List<Point> data = new ArrayList<>();

        if (signPoints.length > 1) {
            data = new ArrayList<>();
            String[] curPoint;
            for (int i = 1; i < signPoints.length; i = i + 2) {
                if (signPoints[i - 1].length() > 0) {
                    curPoint = signPoints[i - 1].split(",");
                    data.add(new Point(Integer.parseInt(curPoint[0]), Integer
                            .parseInt(curPoint[1])));
                }
                if (signPoints[i].length() > 0) {
                    curPoint = signPoints[i].split(",");
                    if (curPoint.length > 1) {
                        data.add(new Point(Integer.parseInt(curPoint[0]), Integer
                                .parseInt(curPoint[1])));
                    } else {
                        data.add(new Point(Integer.parseInt(curPoint[0]), 0));
                    }
                }
            }
        }

        return data;
    }

    public static String pointsToString(List<Point> data) {
        int xShift = Integer.MAX_VALUE;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).x > -1 && data.get(i).x < xShift) {
                xShift = data.get(i).x;
            }
        }

        StringBuilder rtnSign = new StringBuilder();
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i - 1).x < 0) {
                continue;
            }

            if (i > 1) {
                rtnSign.append(";");
            }

            rtnSign.append(data.get(i - 1).x - xShift)
                   .append(",")
                   .append(data.get(i - 1).y)
                   .append(";")
                   .append(data.get(i).x - xShift)
                   .append(",")
                   .append(data.get(i).y);
        }

        return rtnSign.toString();
    }
}
