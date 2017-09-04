package com.bsmwireless.screens.switchdriver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsmwireless.data.storage.users.UserEntity;

import java.util.List;

import app.bsmuniversal.com.R;

public class CoDriverAdapter extends ArrayAdapter<SwitchDriverDialog.UserModel> {

    private int mSelectedPosition;

    public CoDriverAdapter(@NonNull Context context, List<SwitchDriverDialog.UserModel> users) {
        super(context, R.layout.co_driver_item, users);
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
            convertView = inflater.inflate(R.layout.co_driver_item, parent, false);
            viewHolder.mCoDriverName = (TextView) convertView.findViewById(R.id.co_driver_name);
            viewHolder.mCoDriverDutyStatus = (ImageView) convertView.findViewById(R.id.duty_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mCoDriverName.setText(user.getFirstName() + " " + user.getLastName());
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
        TextView mCoDriverName;
        ImageView mCoDriverDutyStatus;
    }
}
