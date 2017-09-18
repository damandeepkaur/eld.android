package com.bsmwireless.screens.switchdriver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.bsmwireless.data.storage.users.UserEntity;

import java.util.List;

import app.bsmuniversal.com.R;

public final class LogOutCoDriverAdapter extends ArrayAdapter<SwitchDriverDialog.UserModel> {

    private int mSelectedPosition;

    public LogOutCoDriverAdapter(@NonNull Context context, List<SwitchDriverDialog.UserModel> users) {
        super(context, R.layout.log_out_co_driver_item, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SwitchDriverDialog.UserModel userModel = getItem(position);
        UserEntity user = userModel.getUser();

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.log_out_co_driver_item, parent, false);
            viewHolder.mCoDriverRB = (RadioButton) convertView.findViewById(R.id.co_driver_radio_button);
            viewHolder.mCoDriverDutyStatus = (ImageView) convertView.findViewById(R.id.duty_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mCoDriverRB.setText(user.getFirstName() + " " + user.getLastName());
        viewHolder.mCoDriverRB.setChecked(mSelectedPosition == position);
        viewHolder.mCoDriverDutyStatus.setImageResource(userModel.getDutyType().getIcon());

        return convertView;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    private static class ViewHolder {
        RadioButton mCoDriverRB;
        ImageView mCoDriverDutyStatus;
    }
}
