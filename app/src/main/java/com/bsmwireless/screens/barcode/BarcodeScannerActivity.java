package com.bsmwireless.screens.barcode;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bsmwireless.screens.common.BaseFragment;
import com.bsmwireless.screens.common.BasePermissionActivity;
import com.google.zxing.Result;

import app.bsmuniversal.com.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeScannerActivity extends BasePermissionActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = BarcodeScannerActivity.class.getSimpleName();

    public static final String BARCODE_UUID = "barcode_uuid";
    public static final String BARCODE_TYPE = "barcode_type";

    private BarcodeScannerFragment mBarcodeScannerFragment = null;

    @Override
    protected String[] getDesiredPermissions() {
        return new String[] { Manifest.permission.CAMERA };
    }

    @Override
    protected void onPermissionDenied() {
        Toast.makeText(this, R.string.barcode_scanner_error, Toast.LENGTH_SHORT).show();

        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onPermissionGranted() {
        mBarcodeScannerFragment = new BarcodeScannerFragment();
        open(mBarcodeScannerFragment);
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
