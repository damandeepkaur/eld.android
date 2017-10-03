package com.bsmwireless.screens.transfer;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.RadioGroup;

import com.bsmwireless.common.App;
import com.bsmwireless.screens.common.menu.BaseMenuActivity;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.transfer.dagger.DaggerTransferComponent;
import com.bsmwireless.screens.transfer.dagger.TransferModule;
import com.bsmwireless.widgets.snackbar.SnackBarLayout;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class TransferActivity extends BaseMenuActivity implements TransferView, TextWatcher {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.transfer_comment_layout)
    TextInputLayout mCommentLayout;

    @BindView(R.id.transfer_comment_text)
    TextInputEditText mCommentText;

    @BindView(R.id.transfer_method)
    RadioGroup mTransferGroup;

    @BindView(R.id.transfer_snackbar)
    SnackBarLayout mSnackBarLayout;

    @Inject
    TransferPresenter mPresenter;

    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerTransferComponent.builder().appComponent(App.getComponent()).transferModule(new TransferModule(this)).build().inject(this);

        setContentView(R.layout.activity_transfer);
        mUnbinder = ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        mCommentText.removeTextChangedListener(this);
        mSnackBarLayout.reset().hideSnackbar();
        mDialog.dismiss();
        super.onDestroy();
    }

    @Override
    protected BaseMenuPresenter getPresenter() {
        return mPresenter;
    }

    private void init() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.transfer_title);
        }

        mCommentText.addTextChangedListener(this);

        mDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.transfer_dialog_title)
                .setMessage(R.string.transfer_dialog_message)
                .setPositiveButton(R.string.transfer_dialog_positive, (dialogInterface, i) -> mPresenter.onConfirmClick(getComment(), getTransferMethod().getCode()))
                .setNegativeButton(R.string.transfer_dialog_negative, null)
                .create();
    }

    private TransferMethod getTransferMethod() {
        return mTransferGroup.getCheckedRadioButtonId() == R.id.transfer_email ? TransferMethod.EMAIL : TransferMethod.WEB;
    }

    private String getComment() {
        return mCommentText.getText().toString();
    }

    @Override
    public void showReportDialog() {
        mDialog.show();
    }

    @OnClick(R.id.transfer_button)
    public void onSendClick() {
        mPresenter.onSendClick(getComment());
    }

    @Override
    public void showError(Error error) {
        mCommentLayout.setError(getString(error.getStringId()));
    }

    @Override
    public void showTransferError(Error error) {
        mSnackBarLayout
                .setMessage(getString(error.getStringId()))
                .setHideableOnTimeout(SnackBarLayout.DURATION_LONG)
                .showSnackbar();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mCommentLayout.setError(null);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
