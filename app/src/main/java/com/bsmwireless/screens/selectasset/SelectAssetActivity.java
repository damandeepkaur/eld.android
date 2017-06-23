package com.bsmwireless.screens.selectasset;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.bsmwireless.screens.help.HelpActivity;
import com.bsmwireless.screens.selectasset.dagger.DaggerSelectAssetComponent;
import com.bsmwireless.screens.selectasset.dagger.SelectAssetModule;
import com.bsmwireless.widgets.GravityDrawable;
import com.bsmwireless.widgets.HelpView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.bsmwireless.screens.barcode.BarcodeScannerActivity.BARCODE_TYPE;
import static com.bsmwireless.screens.barcode.BarcodeScannerActivity.BARCODE_UUID;

public class SelectAssetActivity extends AppCompatActivity implements SelectAssetView {

    private static final int BARCODE_REQUEST_CODE = 101;

    @BindView(R.id.txt_search_veh_name)
    EditText searchBox;

    @BindView(R.id.radio_group)
    View radioGroup;

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

    private int mSelectedSearchProperty;

    private boolean isTrailer = false;

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

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.onSearchTextChanged(mSelectedSearchProperty, s.toString(), isTrailer, false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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
                //intent.putExtra(HelpActivity.HELP_IMAGE_ID_TEG, R.drawable.select_vehicle_phone);

                //TODO: use corrected help widget with translated strings when UI is ready
                ArrayList<HelpView.HelpModel> list = new ArrayList<>();

                list.add(new HelpView.HelpModel(searchBox, "Or enter asset", HelpView.ArrowType.CLOCKWISE, HelpView.PositionType.TOP, GravityDrawable.GravityType.END));
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

    @OnClick(R.id.txt_search_veh_name)
    void onSearchBoxClicked() {
        radioGroup.setVisibility((radioGroup.getVisibility() == VISIBLE) ? GONE : VISIBLE);
    }

    @OnClick({R.id.radio_sap, R.id.radio_legacy, R.id.radio_serial,
            R.id.radio_description, R.id.radio_license_plate, R.id.radio_box_id})
    void onPropertySelected(View view) {
        switch (view.getId()) {
            case R.id.radio_sap:
                mSelectedSearchProperty = 0;
                break;
            case R.id.radio_legacy:
                mSelectedSearchProperty = 1;
                break;
            case R.id.radio_serial:
                mSelectedSearchProperty = 2;
                break;
            case R.id.radio_description:
                mSelectedSearchProperty = 3;
                break;
            case R.id.radio_license_plate:
                mSelectedSearchProperty = 4;
                break;
            case R.id.radio_box_id:
                mSelectedSearchProperty = 5;
                break;
            default:
                mSelectedSearchProperty = 0;
                break;
        }
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
            searchBox.setText(barcodeId);
            mPresenter.onSearchTextChanged(mSelectedSearchProperty, barcodeId, isTrailer, true);
        }
    }

    @Override
    public void setVehicleList(List<Vehicle> vehicles) {
        String[] vehiclesArray = new String[vehicles.size()];
        int i = 0;
        for (Vehicle vehicle : vehicles) {
            vehiclesArray[i++] = vehicle.getProvince() + " [" + vehicle.getBoxId() + "]";
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
    }
}
