package com.bsmwireless.screens.switchdriver;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.bsmwireless.models.ELDEvent;
import com.bsmwireless.screens.switchdriver.dagger.DaggerSwitchDriverComponent;
import com.bsmwireless.screens.switchdriver.dagger.SwitchDriverModule;
import com.bsmwireless.widgets.alerts.DutyType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public final class SwitchDriverDialog implements SwitchDriverView, DriverDialog {

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
    @BindView(R.id.users_list)
    RecyclerView mReassignList;

    @Nullable
    private LogOutCoDriverAdapter mLogOutCoDriverAdapter;

    @Nullable
    private ReassignEventAdapter mReassignEventAdapter;

    @Nullable
    private ELDEvent mELDEvent;

    public enum SwitchDriverStatus {
        SWITCH_DRIVER,
        ADD_CO_DRIVER,
        LOG_OUT,
        DRIVER_SEAT,
        REASSIGN_EVENT
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
    public void showReassignEventDialog(ELDEvent event) {
        mELDEvent = event;
        show(SwitchDriverStatus.REASSIGN_EVENT);
    }

    @Override
    public void show(SwitchDriverStatus status) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        switch (status) {
            case ADD_CO_DRIVER: {
                mPresenter.onAddCoDriverDialog();
                break;
            }
            case LOG_OUT: {
                mPresenter.onLogoutDialog();
                break;
            }
            case DRIVER_SEAT: {
                mPresenter.onDriverSeatDialog();
                break;
            }
            case REASSIGN_EVENT: {
                mPresenter.onReassignDialog();
                break;
            }
            case SWITCH_DRIVER:
            default: {
                mPresenter.onSwitchDriverDialog();
                break;
            }
        }
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
            CoDriverAdapter adapter = new CoDriverAdapter(mContext, coDrivers, mPresenter);
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
    public void setUsersForReassignDialog(List<UserModel> users) {
        if (mReassignList != null) {
            mReassignEventAdapter = new ReassignEventAdapter(mContext, users, mPresenter);
            mReassignList.setAdapter(mReassignEventAdapter);
            mReassignList.setLayoutManager(new LinearLayoutManager(mContext));
            mReassignEventAdapter.setOnClickListener(view -> {
                int position = mReassignList.getChildAdapterPosition(view);
                mReassignEventAdapter.setSelectedPosition(position);
            });
        }
    }

    @Override
    public void coDriverLoggedIn() {
        show(SwitchDriverStatus.SWITCH_DRIVER);
    }

    @Override
    public void coDriverLoggedOut() {
        show(SwitchDriverStatus.SWITCH_DRIVER);
    }

    @Override
    public void eventReassigned() {
        Toast.makeText(mContext, mContext.getString(R.string.switch_driver_event_reassigned), Toast.LENGTH_SHORT).show();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void showError(Error error) {
        // TODO show error
        Toast.makeText(mContext, mContext.getString(error.getStringId()), Toast.LENGTH_SHORT).show();
        switch (error) {
            case ERROR_LOGIN_CO_DRIVER:
            case ERROR_LOGOUT_CO_DRIVER: {
                show(SwitchDriverStatus.SWITCH_DRIVER);
                break;
            }
        }
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
        }
    }

    @Override
    public void hideProgress() {
        if (mProgressBar != null) {
            mProgressBar.setIndeterminate(false);
        }
    }

    @Override
    public void createSwitchDriverDialog() {
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
        showDialog(switchDriverDialog);
    }

    @Override
    public void createAddCoDriverDialog() {
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
        showDialog(addCoDriverDialog);

    }

    @Override
    public void createLogOutCoDriverDialog() {
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
        showDialog(logOutCoDriverDialog);

    }

    @Override
    public void createDriverSeatDialog() {
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

        driverSeatDialog.setOnShowListener(dialog -> mPresenter.onDriverSeatDialogCreated());
        showDialog(driverSeatDialog);
    }

    @Override
    public void createReassignDialog() {
        View view = View.inflate(mContext, R.layout.reassign_event_layout, null);
        ButterKnife.bind(this,view);

        AlertDialog reassignDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.switch_driver_reassign_event)
                .setView(view)
                .setPositiveButton(R.string.switch_driver_confirm_driver_seat, null)
                .setNegativeButton(R.string.switch_driver_cancel, (dialog, which) -> dialog.dismiss())
                .setOnDismissListener(dialog -> mPresenter.onDestroy())
                .setCancelable(true)
                .create();

        reassignDialog.setOnShowListener(dialog -> {
            reassignDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setOnClickListener(v -> {
                                    if (mReassignEventAdapter != null && mELDEvent != null) {
                                        UserModel user = mReassignEventAdapter.getItem(
                                                mReassignEventAdapter.getSelectedPosition()
                                        );
                                        if (user != null) {
                                            mPresenter.reassignEvent(mELDEvent, user.getUser());
                                        }
                                    }
                                });
            mPresenter.onReassignEventDialogCreated();
        });
        showDialog(reassignDialog);
    }

    @Override
    public void createSwitchOnlyDialog() {
        View view = View.inflate(mContext, R.layout.switch_driver_layout, null);
        ButterKnife.bind(this, view);

        if (mDriverSeat != null) {
            mDriverSeat.setVisibility(View.GONE);
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
                .setNegativeButton(R.string.switch_driver_cancel, null)
                .setOnDismissListener(dialog -> mPresenter.onDestroy())
                .setCancelable(true)
                .create();

        switchDriverDialog.setOnShowListener(dialog -> mPresenter.onSwitchDriverCreated());
        showDialog(switchDriverDialog);
    }

    private void showDialog(AlertDialog dialog) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = dialog;
        mDialog.show();
    }

    public static final class UserModel {
        private UserEntity mUser;
        private DutyType mDutyType;

        public UserModel(UserEntity user) {
            mUser = user;
            mDutyType = DutyType.OFF_DUTY;
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

        public static List<UserModel> fromEntity(List<UserEntity> users) {
            List<UserModel> models = new ArrayList<>(users.size());
            for (UserEntity user: users) {
                models.add(new UserModel(user));
            }
            return models;
        }
    }
}
