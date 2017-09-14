package com.bsmwireless.screens.lockscreen;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsmwireless.widgets.alerts.DutyType;

import app.bsmuniversal.com.R;


public class PromtDialog extends DialogFragment {

    public static final String DIALOG_TYPE = "DIALOG_TYPE";

    private PromtDialogListener mPromtDialogListener;

    public interface PromtDialogListener {
        void onDutyStatusSelected(DutyType dutyType);
    }

    public enum DialogType {
        IGNITION_OFF, DISCONNECTED
    }

    public static PromtDialog newInstance(DialogType dialogType) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(DIALOG_TYPE, dialogType);
        PromtDialog dialog = new PromtDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DialogType dialogType = (DialogType) getArguments().getSerializable(DIALOG_TYPE);

        return new AlertDialog.Builder(getContext())
                .setTitle(getTitle(dialogType))
                .setMessage(getMessage(dialogType))
                .setView(createButtons(dialogType))
                .create();

    }

    public void setPromtDialogListener(PromtDialogListener promtDialogListener) {
        mPromtDialogListener = promtDialogListener;
    }

    private String getTitle(DialogType dialogType) {
        String title;
        switch (dialogType) {

            case IGNITION_OFF:
                title = getString(R.string.lock_screen_ignition_off_dialog_title);
                break;

            case DISCONNECTED:
                title = getString(R.string.lock_screen_disconnected_dialog_title);
                break;

            default:
                throw new IllegalArgumentException("Unknown dialog type: " + dialogType);
        }
        return title;
    }

    private String getMessage(DialogType dialogType) {
        String message;
        switch (dialogType) {

            case IGNITION_OFF:
                message = getString(R.string.lock_screen_ignition_off_dialog_message);
                break;

            case DISCONNECTED:
                message = getString(R.string.lock_screen_disconnected_dialog_message);
                break;

            default:
                throw new IllegalArgumentException("Unknown dialog type: " + dialogType);
        }
        return message;
    }

    private View createButtons(DialogType dialogType) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        View onDuty = createButton(linearLayout,
                DutyType.ON_DUTY, getString(R.string.lock_screen_promt_dialog_on_duty));
        linearLayout.addView(onDuty);
        View offDuty = createButton(linearLayout,
                DutyType.OFF_DUTY, getString(R.string.lock_screen_promt_dialog_on_duty));
        linearLayout.addView(offDuty);

        if (dialogType == DialogType.DISCONNECTED) {
            View driving = createButton(linearLayout,
                    DutyType.DRIVING, getString(R.string.lock_screen_promt_dialog_on_duty));
            linearLayout.addView(driving);
        }

        return linearLayout;
    }

    private TextView createButton(ViewGroup parent, DutyType dutyType, String title) {
        TextView view = (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.view_item_dashboard, parent, false);
        view.setCompoundDrawablesWithIntrinsicBounds(dutyType.getIcon(), 0, 0, 0);
        view.setText(title);
        view.setOnClickListener(unused -> handleClick(dutyType));
        return view;
    }

    void handleClick(DutyType dutyType) {
        if (mPromtDialogListener != null) {
            mPromtDialogListener.onDutyStatusSelected(dutyType);
        }
    }


}
