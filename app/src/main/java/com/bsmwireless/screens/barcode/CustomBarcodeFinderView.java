package com.bsmwireless.screens.barcode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import me.dm7.barcodescanner.core.ViewFinderView;

public final class CustomBarcodeFinderView extends ViewFinderView {

    public final Paint PAINT = new Paint();

    private static final int[] SCANNER_ALPHA = new int[]{0, 64, 128, 192, 255, 192, 128, 64};
    private int scannerAlpha;

    public CustomBarcodeFinderView(Context context) {
        super(context);
        init();
    }

    public CustomBarcodeFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        PAINT.setColor(Color.WHITE);
        PAINT.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawViewFinderMask(canvas);
        drawLaser(canvas);
    }

    public void drawViewFinderMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Rect framingRect = this.getFramingRect();
        canvas.drawRect(0.0F, 0.0F, (float)width, (float)framingRect.top, this.mFinderMaskPaint);
        canvas.drawRect(0.0F, (float)(framingRect.bottom + 1), (float)width, (float)height, this.mFinderMaskPaint);
    }

    public void drawLaser(Canvas canvas) {
        Rect framingRect = this.getFramingRect();
        int width = canvas.getWidth();
        this.mLaserPaint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
        this.scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = framingRect.height() / 2 + framingRect.top;
        canvas.drawRect(0, (float)(middle - 1), (float)width, (float)(middle + 2), this.mLaserPaint);
        this.postInvalidateDelayed(80L, framingRect.left - 10, framingRect.top - 10, framingRect.right + 10, framingRect.bottom + 10);
    }
}
