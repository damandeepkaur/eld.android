package com.bsmwireless.screens.selectasset;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.models.Vehicle;
import com.bsmwireless.screens.barcode.BarcodeScannerActivity;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.driverprofile.DriverProfileActivity;
import com.bsmwireless.screens.help.HelpActivity;
import com.bsmwireless.screens.selectasset.dagger.DaggerSelectAssetComponent;
import com.bsmwireless.screens.selectasset.dagger.SelectAssetModule;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.bsmwireless.widgets.helpview.GravityDrawable;
import com.bsmwireless.widgets.helpview.HelpView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.bsmwireless.screens.barcode.BarcodeScannerActivity.BARCODE_TYPE;
import static com.bsmwireless.screens.barcode.BarcodeScannerActivity.BARCODE_UUID;

public class SelectAssetActivity extends BaseActivity implements SelectAssetView {

    private static final int BARCODE_REQUEST_CODE = 101;
    private static final int DEBOUNCE_TIMEOUT = 500;

    @BindView(R.id.txt_search_veh_name)
    EditText mSearchBox;

    @BindView(R.id.list_view_vehicles)
    ListView mVehiclesList;

    @BindView(R.id.select_options)
    View mSelectOptionsButton;

    @BindView(R.id.not_in_vehicle_button)
    View mNotInVehicleButton;

    View mScanView;

    @Inject
    SelectAssetPresenter mPresenter;

    private Unbinder mUnbinder;

    private boolean mIsBarcodeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerSelectAssetComponent.builder().appComponent(App.getComponent())
                .selectAssetModule(new SelectAssetModule(this)).build().inject(this);

        setContentView(R.layout.activity_select_asset);
        mUnbinder = ButterKnife.bind(this);

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        RxTextView.textChanges(mSearchBox).debounce(DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> {
                    mPresenter.onSearchTextChanged(text);
                    if (mIsBarcodeResult) mIsBarcodeResult = false;
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_asset, menu);

        new Handler().post(() -> mScanView = findViewById(R.id.action_select_barcode_scan));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Intent intent = new Intent(this, HelpActivity.class);

                //TODO: use corrected help widget with translated strings when UI is ready
                ArrayList<HelpView.HelpModel> list = new ArrayList<>();

                list.add(new HelpView.HelpModel(mSearchBox, "Or enter asset", HelpView.ArrowType.CLOCKWISE, HelpView.PositionType.TOP, GravityDrawable.GravityType.END));
                list.add(new HelpView.HelpModel(mSelectOptionsButton, "Click to view options", HelpView.ArrowType.CLOCKWISE, HelpView.PositionType.TOP, GravityDrawable.GravityType.END));
                list.add(new HelpView.HelpModel(mNotInVehicleButton, "Click in case of no vehicle selected", HelpView.ArrowType.STRAIGHT, HelpView.PositionType.BOTTOM, GravityDrawable.GravityType.CENTER));
                list.add(new HelpView.HelpModel(mScanView, "Please use QR code to select asset", HelpView.ArrowType.CLOCKWISE, HelpView.PositionType.RIGHT, GravityDrawable.GravityType.START));

                intent.putExtra(HelpActivity.HELP_MODEL_EXTRA, list);

                startActivity(intent);
                break;
            case R.id.action_select_barcode_scan:
                startActivityForResult(new Intent(this, BarcodeScannerActivity.class), BARCODE_REQUEST_CODE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.cancel_button)
    void onCancelButtonClicked() {
        mPresenter.onCancelButtonPressed();
    }

    @OnClick(R.id.not_in_vehicle_button)
    void onNotInVehicleClicked() {
        mPresenter.onNotInVehicleButtonClicked();
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
            String barcodeId = data.getStringExtra(BARCODE_UUID);
            String type = data.getStringExtra(BARCODE_TYPE);
            Timber.v(barcodeId + " type:" + type);
            mIsBarcodeResult = true;
            mSearchBox.setText(barcodeId);
        }
    }

    @Override
    public void setVehicleList(List<Vehicle> vehicles) {
        String[] vehiclesArray = new String[vehicles.size()];
        int i = 0;
        for (Vehicle vehicle : vehicles) {
            vehiclesArray[i++] = vehicle.getName() + " [" + vehicle.getBoxId() + "]";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.sellect_asset_item, vehiclesArray);
        mVehiclesList.setAdapter(adapter);
        mVehiclesList.setOnItemClickListener((parent, view, position, id) -> {
            Vehicle vehicle = vehicles.get(position);
            mPresenter.onVehicleListItemClicked(vehicle);
        });

        mVehiclesList.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyList() {
        mVehiclesList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void goToMainScreen() {
        Toast.makeText(this, "Go to main screen", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, DriverProfileActivity.class));
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(this, "Search keyword must contain minimum 3 characters", Toast.LENGTH_LONG).show();
    }
}
