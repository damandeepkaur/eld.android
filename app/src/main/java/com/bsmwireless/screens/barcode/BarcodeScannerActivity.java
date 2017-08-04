package com.bsmwireless.screens.barcode;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.common.BasePermissionActivity;
import com.google.zxing.Result;

import app.bsmuniversal.com.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeScannerActivity extends BasePermissionActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = BarcodeScannerActivity.class.getSimpleName();

    public static final String BARCODE_UUID = "barcode_uuid";
    public static final String BARCODE_TYPE = "barcode_type";
    public static final String IS_PERMISSION_ERROR = "is_permission_error";

    @Override
    protected String[] getDesiredPermissions() {
        return new String[] { Manifest.permission.CAMERA };
    }

    @Override
    protected void onPermissionDenied() {
        Intent intent = new Intent();
        intent.putExtra(IS_PERMISSION_ERROR, true);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    protected void onPermissionGranted() {
        open(new BarcodeScannerFragment());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
    }

    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent();
        intent.putExtra(BARCODE_UUID, result.getText());
        intent.putExtra(BARCODE_TYPE, result.getBarcodeFormat().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void open(BaseFragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.barcode_content, fragment)
                .commit();
    }
}
