package com.bsmwireless.screens.selectasset;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.models.Vehicle;
import com.bsmwireless.screens.barcode.BarcodeScannerActivity;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.help.HelpActivity;
import com.bsmwireless.screens.login.LoginActivity;
import com.bsmwireless.screens.selectasset.dagger.DaggerSelectAssetComponent;
import com.bsmwireless.screens.selectasset.dagger.SelectAssetModule;
import com.bsmwireless.widgets.common.RxSearchView;

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

    @BindView(R.id.select_asset_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.select_asset_search_view)
    SearchView mSearchView;

    @BindView(R.id.select_asset_search_list)
    RecyclerView mSearchRecyclerView;

    @BindView(R.id.select_asset_last_list)
    RecyclerView mLastRecyclerView;

    @BindView(R.id.select_asset_previous_text)
    TextView mPreviousAssetsTextView;

    @BindView(R.id.select_asset_search_card)
    CardView mSearchCardView;

    @Inject
    SelectAssetPresenter mPresenter;

    private Unbinder mUnbinder;

    private SelectAssetAdapter mSearchAdapter;
    private SelectAssetAdapter mLastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerSelectAssetComponent.builder().appComponent(App.getComponent()).selectAssetModule(new SelectAssetModule(this)).build().inject(this);

        setContentView(R.layout.activity_select_asset);
        mUnbinder = ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RxSearchView.queryTextChanges(mSearchView).debounce(Constants.DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> mPresenter.onSearchTextChanged(text));

        //set search list
        mSearchAdapter = new SelectAssetAdapter(view -> {
            int position = mSearchRecyclerView.getChildAdapterPosition(view);
            mPresenter.onVehicleListItemClicked(mSearchAdapter.getItem(position));
        });

        LinearLayoutManager searchManager = new LinearLayoutManager(this);
        searchManager.setOrientation(LinearLayoutManager.VERTICAL);

        mSearchRecyclerView.bringToFront();
        mSearchRecyclerView.setHasFixedSize(true);
        mSearchRecyclerView.setLayoutManager(searchManager);
        mSearchRecyclerView.setAdapter(mSearchAdapter);

        //set last list
        mLastAdapter = new SelectAssetAdapter(view -> {
            int position = mLastRecyclerView.getChildAdapterPosition(view);
            mPresenter.onVehicleListItemClicked(mLastAdapter.getItem(position));
        });

        LinearLayoutManager lastManager = new LinearLayoutManager(this);
        lastManager.setOrientation(LinearLayoutManager.VERTICAL);

        mLastRecyclerView.setHasFixedSize(true);
        mLastRecyclerView.setLayoutManager(lastManager);
        mLastRecyclerView.setAdapter(mLastAdapter);

        mPresenter.onCreated();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: wait for UI
        //getMenuInflater().inflate(R.menu.menu_select_asset, menu);
        //new Handler().post(() -> mScanView = findViewById(R.id.action_select_barcode_scan));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_help:
                Intent intent = new Intent(this, HelpActivity.class);

                //TODO: use corrected help widget with translated strings when UI is ready
                /*ArrayList<HelpView.HelpModel> list = new ArrayList<>();

                list.add(new HelpView.HelpModel(mSearchBox, "Or enter asset", HelpView.ArrowType.CLOCKWISE, HelpView.PositionType.TOP, GravityDrawable.GravityType.END));
                list.add(new HelpView.HelpModel(mSelectOptionsButton, "Click to view options", HelpView.ArrowType.CLOCKWISE, HelpView.PositionType.TOP, GravityDrawable.GravityType.END));
                list.add(new HelpView.HelpModel(mNotInVehicleButton, "Click in case of no vehicle selected", HelpView.ArrowType.STRAIGHT, HelpView.PositionType.BOTTOM, GravityDrawable.GravityType.CENTER));
                list.add(new HelpView.HelpModel(mScanView, "Please use QR code to select asset", HelpView.ArrowType.CLOCKWISE, HelpView.PositionType.RIGHT, GravityDrawable.GravityType.START));

                intent.putExtra(HelpActivity.HELP_MODEL_EXTRA, list);

                startActivity(intent);*/
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.select_asset_scan_qr_code_button)
    void onScanQRCodeClicked() {
        startActivityForResult(new Intent(this, BarcodeScannerActivity.class), BARCODE_REQUEST_CODE);
    }

    @OnClick(R.id.select_asset_not_in_vehicle_button)
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
            mSearchView.setQuery(barcodeId, true);
        }
    }

    @Override
    public void setVehicleList(List<Vehicle> vehicles, String searchText) {
        mSearchAdapter.setSearchList(vehicles, searchText);
    }

    @Override
    public void setLastVehicleList(@Nullable List<Vehicle> vehicles) {
        mPreviousAssetsTextView.setText(R.string.select_asset_previous_assets);
        mLastAdapter.setSearchList(vehicles, null);
    }

    @Override
    public void setEmptyList() {
        mSearchCardView.setVisibility(View.GONE);
        mSearchAdapter.setSearchList(null, null);
    }

    @Override
    public void goToHomeScreen() {
        Toast.makeText(this, "Go to main screen", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage() {
        mSearchCardView.setVisibility(View.VISIBLE);
        mSearchAdapter.setHint(getString(R.string.select_asset_characters));
    }

    @Override
    public void showEmptyListMessage() {
        mSearchCardView.setVisibility(View.VISIBLE);
        mSearchAdapter.setHint(getString(R.string.select_asset_find_nothing));
    }

    @Override
    public void showEmptyLastListMessage() {
        mPreviousAssetsTextView.setText(R.string.select_asset_no_previous_assets);
    }

    @Override
    public void onBackPressed() {
        mPresenter.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
        super.onBackPressed();
    }
}
