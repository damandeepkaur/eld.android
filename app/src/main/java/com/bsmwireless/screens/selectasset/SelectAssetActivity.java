package com.bsmwireless.screens.selectasset;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bsmwireless.common.App;
import com.bsmwireless.common.Constants;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.common.utils.ViewUtils;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.models.Vehicle;
import com.bsmwireless.screens.barcode.BarcodeScannerActivity;
import com.bsmwireless.screens.common.BaseActivity;
import com.bsmwireless.screens.navigation.NavigationActivity;
import com.bsmwireless.screens.selectasset.dagger.DaggerSelectAssetComponent;
import com.bsmwireless.screens.selectasset.dagger.SelectAssetModule;
import com.bsmwireless.widgets.common.RxSearchView;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @BindView(R.id.select_asset_progress_bar_container)
    FrameLayout mSelectAssetProgressBarContainer;

    @BindView(R.id.select_asset_progress_bar)
    ProgressBar mSelectAssetProgressBar;

    @BindView(R.id.select_asset_search_list)
    RecyclerView mSearchRecyclerView;

    @BindView(R.id.select_asset_last_list)
    RecyclerView mLastRecyclerView;

    @BindView(R.id.select_asset_previous_text)
    TextView mPreviousAssetsTextView;

    @BindView(R.id.select_asset_search_card)
    CardView mSearchCardView;

    @BindView(R.id.select_asset_snackbar)
    SnackBarLayout mSnackBarLayout;

    @BindView(R.id.select_asset_scan_qr_code_button)
    AppCompatButton mSelectAssetScanQrCodeButton;

    @BindView(R.id.select_asset_not_in_vehicle_button)
    AppCompatButton mSelectAssetNotInVehicleButton;

    @Inject
    SelectAssetPresenter mPresenter;

    private SelectAssetAdapter mSearchAdapter;
    private SelectAssetAdapter mLastAdapter;

    private AlertDialog mAlertDialog;

    private boolean mIsBoxIdScanned = false;

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
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_green_24dp);
            actionBar.setTitle(R.string.select_asset_title);
        }

        RxSearchView.queryTextChanges(mSearchView).debounce(Constants.DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> mPresenter.onSearchTextChanged(text));

        //set search list
        mSearchAdapter = new SelectAssetAdapter(view -> {
            int position = mSearchRecyclerView.getChildAdapterPosition(view);
            mPresenter.onVehicleListItemClicked(mSearchAdapter.getItem(position));
            mIsBoxIdScanned = false;
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

        mSnackBarLayout
                .setHideableOnTimeout(SnackBarLayout.DURATION_LONG)
                .setHideableOnFocusLost(true);

        hideProgress();

        mPresenter.onViewCreated();
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
                /*Intent intent = new Intent(this, HelpActivity.class);

                //TODO: use corrected help widget with translated strings when UI is ready
                ArrayList<HelpView.HelpModel> list = new ArrayList<>();

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
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
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
            mSearchView.setQuery(barcodeId, false);
            mIsBoxIdScanned = true;

        } else if (data != null && data.getBooleanExtra(BarcodeScannerActivity.IS_PERMISSION_ERROR, false)) {
            showErrorMessage(Error.ERROR_PERMISSION);
        }
    }

    @Override
    public void setVehicleList(List<Vehicle> vehicles, String searchText) {
        if (!mIsBoxIdScanned) {
            mSearchCardView.setVisibility(View.VISIBLE);
            mSearchAdapter.setSearchList(vehicles, searchText);
        } else {
            mPresenter.onVehicleListItemClicked(vehicles.get(0));
        }
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
        startActivity(new Intent(this, NavigationActivity.class));
        finish();
    }

    @Override
    public void showSearchErrorMessage() {
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
    public void showConfirmationDialog() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }

        mAlertDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.select_asset_dialog_title)
                .setMessage(R.string.select_asset_information_no_selected_assets)
                .setPositiveButton(R.string.select_asset_continue, (dialog, which) -> {})
                .setNegativeButton(R.string.select_asset_close, (dialog, which) -> onActionDone())
                .show();
    }

    @Override
    public void onActionDone() {
        finish();
    }

    @Override
    public void showProgress() {
        if (mSelectAssetProgressBar != null) {
            mSelectAssetProgressBarContainer.setVisibility(View.VISIBLE);
            mSelectAssetProgressBar.setIndeterminate(true);

            mSearchRecyclerView.setEnabled(false);
            mLastRecyclerView.setEnabled(false);

            mSelectAssetScanQrCodeButton.setEnabled(false);
            mSelectAssetNotInVehicleButton.setEnabled(false);
        }
    }

    @Override
    public void hideProgress() {
        if (mSelectAssetProgressBar != null) {
            mSelectAssetProgressBarContainer.setVisibility(View.GONE);
            mSelectAssetProgressBar.setIndeterminate(false);

            mSearchRecyclerView.setEnabled(true);
            mLastRecyclerView.setEnabled(true);

            mSelectAssetScanQrCodeButton.setEnabled(true);
            mSelectAssetNotInVehicleButton.setEnabled(true);
        }
    }

    @Override
    public void showErrorMessage(Error error) {
        ViewUtils.hideSoftKeyboard(this);

        int id;
        switch (error) {
            case ERROR_PERMISSION:
                id = R.string.barcode_scanner_error;
                break;

            case ERROR_BLACKBOX:
                id = R.string.error_pair;
                break;

            default:
                id = R.string.error_unexpected;
                break;
        }

        mSnackBarLayout
                .setMessage(getString(id))
                .showSnackbar();
    }

    @Override
    public void onBackPressed() {
        mPresenter.onBackButtonPressed();
        if (this.isFinishing()) {
            super.onBackPressed();
        }
    }

    @Override
    public void showErrorMessage(RetrofitException error) {
        ViewUtils.hideSoftKeyboard(this);

        mSnackBarLayout
                .setMessage(NetworkUtils.getErrorMessage(error, this))
                .showSnackbar();
    }
}
