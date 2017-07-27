package com.bsmwireless.screens.barcode;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsmwireless.screens.common.BaseFragment;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeScannerFragment extends BaseFragment {

    private static final String TAG = BarcodeScannerFragment.class.getSimpleName();

    private ZXingScannerView mScannerView;
    private ZXingScannerView.ResultHandler mHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ZXingScannerView.ResultHandler) {
            mHandler = (ZXingScannerView.ResultHandler) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ZXingScannerView.ResultHandler");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getContext()) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomBarcodeFinderView(context);
            }
        };
        return mScannerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mScannerView.startCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.resumeCameraPreview(mHandler);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCameraPreview();
    }

    @Override
    public void onStop() {
        super.onStop();
        mScannerView.stopCamera();
    }
}
