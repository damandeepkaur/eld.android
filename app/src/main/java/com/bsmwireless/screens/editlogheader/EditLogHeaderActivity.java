package com.bsmwireless.screens.editlogheader;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.bsmwireless.common.App;
import com.bsmwireless.common.utils.ListConverter;
import com.bsmwireless.screens.common.menu.BaseMenuActivity;
import com.bsmwireless.screens.common.menu.BaseMenuPresenter;
import com.bsmwireless.screens.editlogheader.dagger.DaggerEditLogHeaderComponent;
import com.bsmwireless.screens.editlogheader.dagger.EditLogHeaderModule;
import com.bsmwireless.screens.logs.LogHeaderModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditLogHeaderActivity extends BaseMenuActivity implements EditLogHeaderView {

    public final static String OLD_LOG_HEADER_EXTRA = "old_log_header_extra";
    public final static String NEW_LOG_HEADER_EXTRA = "new_log_header_extra";

    @Inject
    EditLogHeaderPresenter mPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.carrier_name)
    TextInputEditText mCarrierName;

    @BindView(R.id.home_terminal_name)
    TextInputEditText mHomeTerminalName;

    @BindView(R.id.home_terminal_address)
    TextInputEditText mHomeTerminalAddress;

    @BindView(R.id.trailers)
    TextInputEditText mTrailers;

    @BindView(R.id.shipping_id)
    TextInputEditText mShippingId;

    @BindView(R.id.exemptions)
    LinearLayout mExemptionList;

    private List<ExemptionModel> mExemptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_log_header);
        DaggerEditLogHeaderComponent.builder().appComponent(App.getComponent()).editLogHeaderModule(new EditLogHeaderModule(this)).build().inject(this);
        mUnbinder = ButterKnife.bind(this);

        initToolbar();

        LogHeaderModel logHeaderModel = null;
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(OLD_LOG_HEADER_EXTRA)) {
                logHeaderModel = intent.getParcelableExtra(OLD_LOG_HEADER_EXTRA);
            }
        }

        mPresenter.onViewCreated(logHeaderModel);
    }


    @OnClick(R.id.save_log_header)
    void saveLogHeader() {
        mPresenter.onSaveLogHeaderButtonClicked();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected BaseMenuPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setLogHeaderModel(LogHeaderModel logHeaderModel) {
        if (logHeaderModel != null) {
            mCarrierName.setText(logHeaderModel.getCarrierName());
            mHomeTerminalName.setText(logHeaderModel.getHomeTerminalName());
            mHomeTerminalAddress.setText(logHeaderModel.getHomeTerminalAddress());
            mTrailers.setText(logHeaderModel.getTrailers());
            mShippingId.setText(logHeaderModel.getShippingId());

            String allExemptions = logHeaderModel.getAllExemptions();
            String selectedExemptions = logHeaderModel.getSelectedExemptions();

            List<String> allExemptionsList = ListConverter.toStringList(allExemptions);
            List<String> selectedExemptionsList = ListConverter.toStringList(selectedExemptions);
            List<ExemptionModel> exemptions = new ArrayList<>();
            for (String name : allExemptionsList) {
                if (!ExemptionModel.NONE.equals(name)) {
                    ExemptionModel exemptionModel = new ExemptionModel();
                    exemptionModel.setExemptionName(name);
                    exemptionModel.setSelected(selectedExemptionsList.contains(name));
                    exemptions.add(exemptionModel);
                }
            }
            setupExemptions(exemptions);
        }
    }

    private void setupExemptions(List<ExemptionModel> exemptions) {
        mExemptions = exemptions;

        LinearLayout list = (LinearLayout) findViewById(R.id.exemptions);
        for (int i = 0; i < exemptions.size(); i++) {
            ExemptionModel exemption = exemptions.get(i);
            if (!ExemptionModel.NONE.equals(exemption.getName())) {
                CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(R.layout.exemption_check_box_item, null);
                checkBox.setText(exemption.getName());
                checkBox.setChecked(exemption.isSelected());
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    int position = (int) buttonView.getTag();
                    mExemptions.get(position).setSelected(isChecked);
                });
                checkBox.setTag(i);
                list.addView(checkBox);
            }
        }
    }

    @Override
    public void saveLogHeader(LogHeaderModel logHeaderModel) {
        Intent result = new Intent();
        result.putExtra(NEW_LOG_HEADER_EXTRA, logHeaderModel);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public LogHeaderModel getLogHeader(LogHeaderModel logHeaderModel) {

        logHeaderModel.setCarrierName(mCarrierName.getText().toString());
        logHeaderModel.setHomeTerminalName(mHomeTerminalName.getText().toString());
        logHeaderModel.setHomeTerminalAddress(mHomeTerminalAddress.getText().toString());
        logHeaderModel.setTrailers(mTrailers.getText().toString());
        logHeaderModel.setShippingId(mShippingId.getText().toString());

        ArrayList<String> selectedExemptions = new ArrayList<>();
        for (ExemptionModel exemption : mExemptions) {
            if (exemption.isSelected()) {
                selectedExemptions.add(exemption.getName());
            }
        }
        if (!selectedExemptions.isEmpty()) {
            logHeaderModel.setSelectedExemptions(ListConverter.stringListToString(selectedExemptions));
        } else {
            logHeaderModel.setSelectedExemptions(ExemptionModel.NONE);
        }

        return logHeaderModel;
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public class ExemptionModel {
        public static final String NONE = "NONE";

        private String exemptionName;
        private boolean isSelected;

        public String getName() {
            return exemptionName;
        }

        public void setExemptionName(String exemptionName) {
            this.exemptionName = exemptionName;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

    }
}
