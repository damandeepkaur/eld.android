package com.bsmwireless.screens.switchdriver;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.NetworkUtils;
import com.bsmwireless.data.network.RetrofitException;
import com.bsmwireless.data.storage.users.UserEntity;
import com.bsmwireless.screens.switchdriver.dagger.DaggerSwitchDriverComponent;
import com.bsmwireless.screens.switchdriver.dagger.SwitchDriverModule;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SwitchDriverDialog implements SwitchDriverView, DriverDialog {

    private Context mContext;

    private AlertDialog mDialog;

    private SwitchDriverStatus mStatus = SwitchDriverStatus.SWITCH_DRIVER;

    @Inject
    SwitchDriverPresenter mPresenter;

    @Nullable
    @BindView(R.id.driver_info_layout)
    View mDriverInfoLayout;
    @Nullable
    @BindView(R.id.driver_name)
    TextView mDriverName;
    @Nullable
    @BindView(R.id.driver_status)
    ImageView mDriverStatus;
    @Nullable
    @BindView(R.id.driver_seat)
    AppCompatButton mDriverSeat;
    @Nullable
    @BindView(R.id.username)
    TextInputEditText mUserName;
    @Nullable
    @BindView(R.id.password)
    TextInputEditText mPassword;
    @Nullable
    @BindView(R.id.co_drivers_list)
    ListView mCoDrivers;
    @Nullable
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Nullable
    private LogOutCoDriverAdapter mLogOutCoDriverAdapter;

    public enum SwitchDriverStatus {
        SWITCH_DRIVER,
        ADD_CO_DRIVER,
        LOG_OUT,
        DRIVER_SEAT
    }

    public SwitchDriverDialog(Context context) {
        mContext = context;
        DaggerSwitchDriverComponent.builder().appComponent(App.getComponent()).switchDriverModule(new SwitchDriverModule(this)).build().inject(this);
    }

    @Override
    public void show() {
        show(mStatus);
    }

    @Override
    public void show(SwitchDriverStatus status) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        switch (status) {
            case SWITCH_DRIVER: {
                mDialog = createSwitchDriverDialog();
                break;
            }
            case ADD_CO_DRIVER: {
                mDialog = createAddCoDriverDialog();
                break;
            }
            case LOG_OUT: {
                mDialog = createLogOutCoDriverDialog();
                break;
            }
            case DRIVER_SEAT: {
                mDialog = createDriverSeatDialog();
                break;
            }
            default: {
                mDialog = createSwitchDriverDialog();
                break;
            }
        }
        mDialog.show();
    }

    @Override
    public void setDriverInfo(UserModel driver) {
        if (mDriverName != null) {
            mDriverName.setText(driver.getUser().getFirstName() + " " + driver.getUser().getLastName());
        }
        if (mDriverStatus != null) {
            mDriverStatus.setImageResource(driver.getDutyType().getIcon());
        }
    }

    @Override
    public void setCoDriversForSwitchDialog(List<UserModel> coDrivers) {
        if (mCoDrivers != null) {
            CoDriverAdapter adapter = new CoDriverAdapter(mContext, coDrivers);
            mCoDrivers.setAdapter(adapter);
            mCoDrivers.setOnItemClickListener((parent, view, position, id) -> {
                UserModel user = adapter.getItem(position);
                mPresenter.setCurrentUser(user.getUser());
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void setCoDriversForLogOutDialog(List<UserModel> coDrivers) {
        if (mCoDrivers != null) {
            mLogOutCoDriverAdapter = new LogOutCoDriverAdapter(mContext, coDrivers);
            mCoDrivers.setAdapter(mLogOutCoDriverAdapter);
            mCoDrivers.setOnItemClickListener((parent, view, position, id) -> mLogOutCoDriverAdapter.setSelectedPosition(position));
        }
    }

    @Override
    public void setCoDriversForDriverSeatDialog(List<UserModel> coDrivers) {
        if (mCoDrivers != null) {
            mLogOutCoDriverAdapter = new LogOutCoDriverAdapter(mContext, coDrivers);
            mCoDrivers.setAdapter(mLogOutCoDriverAdapter);
            mCoDrivers.setOnItemClickListener((parent, view, position, id) -> mLogOutCoDriverAdapter.setSelectedPosition(position));
        }
    }

    @Override
    public void coDriverLoggedIn() {
        show(SwitchDriverStatus.SWITCH_DRIVER);
    }

    @Override
    public void loginError() {
        show(SwitchDriverStatus.SWITCH_DRIVER);
    }

    @Override
    public void coDriverLoggedOut() {
        show(SwitchDriverStatus.SWITCH_DRIVER);
    }

    @Override
    public void logoutError() {
        show(SwitchDriverStatus.SWITCH_DRIVER);
    }

    @Override
    public void showError(Error error) {
        // TODO show error
        Toast.makeText(mContext, mContext.getString(error.getStringId()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(RetrofitException error) {
        // TODO show error
        Toast.makeText(mContext, NetworkUtils.getErrorMessage(error, mContext), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        if (mProgressBar != null) {
            mProgressBar.setIndeterminate(true);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        if (mProgressBar != null) {
            mProgressBar.setIndeterminate(false);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private AlertDialog createSwitchDriverDialog() {
        View view = View.inflate(mContext, R.layout.switch_driver_layout, null);
        ButterKnife.bind(this, view);

        if (mDriverSeat != null) {
            mDriverSeat.setOnClickListener(v -> show(SwitchDriverStatus.DRIVER_SEAT));
        }

        if (mDriverInfoLayout != null) {
            mDriverInfoLayout.setOnClickListener(v -> {
                mPresenter.setCurrentUser(null);
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            });
        }

        AlertDialog switchDriverDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.switch_driver)
                .setView(view)
                .setPositiveButton(R.string.switch_driver_add_co_driver, (dialog, which) -> show(SwitchDriverStatus.ADD_CO_DRIVER))
                .setNegativeButton(R.string.switch_driver_log_out, (dialog, which) -> show(SwitchDriverStatus.LOG_OUT))
                .setOnDismissListener(dialog -> mPresenter.onDestroy())
                .setCancelable(true)
                .create();

        switchDriverDialog.setOnShowListener(dialog -> mPresenter.onSwitchDriverCreated());

        return switchDriverDialog;
    }

    private AlertDialog createAddCoDriverDialog() {
        View view = View.inflate(mContext, R.layout.add_co_driver_layout, null);
        ButterKnife.bind(this,view);

        AlertDialog addCoDriverDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.switch_driver_add_co_driver)
                .setView(view)
                .setPositiveButton(R.string.switch_driver_log_in, null)
                .setNegativeButton(R.string.switch_driver_cancel, (dialog, which) -> dialog.dismiss())
                .setOnDismissListener(dialog -> mPresenter.onDestroy())
                .setCancelable(true)
                .create();

        addCoDriverDialog.setOnShowListener(dialog -> {
            addCoDriverDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                             .setOnClickListener(v -> {
                                 if (mUserName != null && mPassword != null) {
                                     mPresenter.login(mUserName.getText().toString(),
                                             mPassword.getText().toString());
                                 }
                             });
            mPresenter.onAddCoDriverCreated();
        });

        return addCoDriverDialog;
    }

    private AlertDialog createLogOutCoDriverDialog() {
        View view = View.inflate(mContext, R.layout.switch_driver_layout, null);
        ButterKnife.bind(this,view);

        if (mDriverSeat != null) {
            mDriverSeat.setVisibility(View.GONE);
        }

        AlertDialog logOutCoDriverDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.switch_driver_co_driver_log_out)
                .setView(view)
                .setPositiveButton(R.string.switch_driver_log_out, null)
                .setNegativeButton(R.string.switch_driver_cancel, (dialog, which) -> dialog.dismiss())
                .setOnDismissListener(dialog -> mPresenter.onDestroy())
                .setCancelable(true)
                .create();

        logOutCoDriverDialog.setOnShowListener(dialog -> {
            logOutCoDriverDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                             .setOnClickListener(v -> {
                                 if (mLogOutCoDriverAdapter != null && mLogOutCoDriverAdapter.getCount() > 0) {
                                     mPresenter.logout(mLogOutCoDriverAdapter.getItem(mLogOutCoDriverAdapter.getSelectedPosition()).getUser());
                                 }
                             });
            mPresenter.onLogOutCoDriverCreated();
        });

        return logOutCoDriverDialog;
    }

    private AlertDialog createDriverSeatDialog() {
        View view = View.inflate(mContext, R.layout.switch_driver_layout, null);
        ButterKnife.bind(this,view);

        if (mDriverSeat != null) {
            mDriverSeat.setVisibility(View.GONE);
        }

        AlertDialog driverSeatDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.switch_driver_driver_seat)
                .setView(view)
                .setPositiveButton(R.string.switch_driver_confirm_driver_seat, (dialog, which) -> {
                    if (mLogOutCoDriverAdapter != null && mLogOutCoDriverAdapter.getCount() > 0) {
                        mPresenter.setCurrentDriver(mLogOutCoDriverAdapter.getItem(mLogOutCoDriverAdapter.getSelectedPosition()).getUser());
                    }
                })
                .setNegativeButton(R.string.switch_driver_cancel, (dialog, which) -> dialog.dismiss())
                .setOnDismissListener(dialog -> mPresenter.onDestroy())
                .setCancelable(true)
                .create();

        driverSeatDialog.setOnShowListener(dialog -> {
            mPresenter.onDriverSeatDialogCreated();
        });

        return driverSeatDialog;
    }

    public static class UserModel {
        private UserEntity mUser;
        private DutyType mDutyType;

        public UserModel(UserEntity user) {
            mUser = user;
        }

        public UserEntity getUser() {
            return mUser;
        }

        public void setUser(UserEntity user) {
            mUser = user;
        }

        public DutyType getDutyType() {
            return mDutyType;
        }

        public void setDutyType(DutyType dutyType) {
            mDutyType = dutyType;
        }
    }
}
