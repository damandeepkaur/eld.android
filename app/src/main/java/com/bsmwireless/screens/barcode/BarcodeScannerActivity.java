package com.bsmwireless.screens.barcode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.common.BaseFragment;
import com.google.zxing.Result;

import app.bsmuniversal.com.R;

public class BarcodeScannerActivity extends BaseActivity implements BarcodeResultHandler {

    private static final String TAG = BarcodeScannerActivity.class.getSimpleName();

    public static final String BARCODE_UUID = "barcode_uuid";
    public static final String BARCODE_TYPE = "barcode_type";

    BarcodeScannerFragment mBarcodeScannerFragment = new BarcodeScannerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        open(mBarcodeScannerFragment, false);
    }

    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent();
        intent.putExtra(BARCODE_UUID, result.getText());
        intent.putExtra(BARCODE_TYPE, result.getBarcodeFormat().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onFinish() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void open(BaseFragment fragment, boolean useBackStack) {
        //TODO: check for current and doesn't replace the same fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (useBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.barcode_content, fragment).commit();
    }
}
