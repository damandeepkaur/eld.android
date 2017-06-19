package com.bsmwireless.screens.barcode;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public interface BarcodeResultHandler extends ZXingScannerView.ResultHandler {
    void onFinish();
}
