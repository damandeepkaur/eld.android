package com.bsmwireless.screens.barcode;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsmwireless.screens.common.BasePermissionFragment;
import com.google.zxing.Result;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeScannerFragment extends BasePermissionFragment implements ZXingScannerView.ResultHandler {

    private static final String TAG = BarcodeScannerFragment.class.getSimpleName();

    ZXingScannerView mScannerView;

    public BarcodeScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getContext()) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomBarcodeFinderView(context);
            }
        };
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Activity activity = getActivity();
        if (activity instanceof BarcodeResultHandler) {
            ((BarcodeResultHandler) activity).handleResult(result);
        }
    }

    @Override
    protected String[] getDesiredPermissions() {
        return new String[] { Manifest.permission.CAMERA };
    }

    @Override
    protected void onPermissionDenied() {
        Activity activity = getActivity();
        if (activity instanceof BarcodeResultHandler) {
            ((BarcodeResultHandler) activity).onFinish();
        }
    }

    @Override
    protected void onPermissionGranted() {

    }
}
