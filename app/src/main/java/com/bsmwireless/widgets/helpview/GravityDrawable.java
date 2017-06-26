package com.bsmwireless.widgets.helpview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;

public class GravityDrawable extends Drawable {

    public enum GravityType {
        START,
        CENTER,
        END
    }

    private final Drawable mDrawable;
    private final GravityType mGravityType;
    private int mRotation;

    public GravityDrawable(@NonNull Drawable drawable, @NonNull GravityType gravity, int rotation) {
        mDrawable = drawable;
        mGravityType = gravity;
        mRotation = rotation;
    }

    @Override
    public int getIntrinsicWidth() {
        return mDrawable.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mDrawable.getIntrinsicHeight();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Bitmap bitmap = getBitmapFromDrawable(mDrawable);

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        float px = bitmapWidth / 2;
        float py = bitmapHeight / 2;

        if (mRotation % 180 == 0) {
            px += getGravityTranslation(canvas.getWidth(), bitmapWidth, mGravityType);
        } else {
            py += getGravityTranslation(canvas.getHeight(), bitmapHeight, mGravityType);
        }

        Matrix matrix = new Matrix();
        matrix.postTranslate(-bitmapWidth / 2, -bitmapHeight / 2);
        matrix.postRotate(mRotation);
        matrix.postTranslate(px, py);

        canvas.drawBitmap(bitmap, matrix, new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG));

        bitmap.recycle();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mDrawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return mDrawable.getOpacity();
    }

    private int getGravityTranslation(int canvasSize, int bitmapSize, GravityType gravity) {
        switch (gravity) {
            case START:
                return -canvasSize / 2 + bitmapSize / 2;

            case END:
                return canvasSize / 2 - bitmapSize / 2;

            default:
                return 0;
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
