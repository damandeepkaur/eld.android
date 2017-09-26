package com.bsmwireless.screens.switchdriver;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReassignEventAdapter extends RecyclerView.Adapter<ReassignEventAdapter.ReassignViewHolder> {

    private static final int HEADERS_COUNT = 2;

    private int mSelectedPosition = -1;

    private List<SwitchDriverDialog.UserModel> mItems = Collections.emptyList();

    private Context mContext;

    private View.OnClickListener mListener;

    private SwitchDriverPresenter mPresenter;

    private static final int DRIVER_HEADER = 0;
    private static final int DRIVER = 1;
    private static final int CO_DRIVER_HEADER = 2;

    public ReassignEventAdapter(Context context, List<SwitchDriverDialog.UserModel> items, SwitchDriverPresenter presenter) {
        mContext = context;
        mItems = items;
        mPresenter = presenter;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0: {
                return DRIVER_HEADER;
            }
            case 2: {
                return CO_DRIVER_HEADER;
            }
            default: {
                return DRIVER;
            }
        }
    }

    @Override
    public ReassignViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case DRIVER_HEADER: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reassign_event_header_layout, parent, false);
                break;
            }
            case CO_DRIVER_HEADER: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reassign_event_header_layout, parent, false);
                break;
            }
            case DRIVER: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_out_co_driver_item, parent, false);
                break;
            }
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_out_co_driver_item, parent, false);
                break;
        }
        return new ReassignViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ReassignViewHolder holder, int position) {
        int viewType = holder.mViewType;
        switch (viewType) {
            case DRIVER_HEADER:
            case CO_DRIVER_HEADER: {
                if (holder.mHeaderTextView != null) {
                    holder.itemView.setOnClickListener(null);
                    holder.mHeaderTextView.setText(mContext.getString(viewType == DRIVER_HEADER ? R.string.driver : R.string.switch_driver_co_drivers));
                }
                break;
            }
            case DRIVER: {
                SwitchDriverDialog.UserModel user = mItems.get(position < HEADERS_COUNT ? position - 1 : position - HEADERS_COUNT);
                if (holder.mCoDriverRB != null && holder.mCoDriverDutyStatus != null) {
                    holder.mCoDriverRB.setText(user.getUser().getFirstName() + " " + user.getUser().getLastName());
                    holder.mCoDriverDutyStatus.setImageResource(user.getDutyType().getIcon());
                    if (user.getUser().getId() == mPresenter.getCurrentUserId()) {
                        holder.itemView.setOnClickListener(null);
                        holder.mCoDriverRB.setEnabled(false);
                        holder.mCoDriverRB.setChecked(false);
                    } else {
                        if (mSelectedPosition == -1) mSelectedPosition = position;
                        holder.itemView.setOnClickListener(mListener);
                        holder.mCoDriverRB.setEnabled(true);
                        holder.mCoDriverRB.setChecked(mSelectedPosition == position);
                    }
                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() + HEADERS_COUNT : 0;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    @Nullable
    public SwitchDriverDialog.UserModel getItem(int position) {
        return mItems != null && (position < mItems.size() + HEADERS_COUNT) && position > 0 ?
                mItems.get(position < HEADERS_COUNT ? position - 1 : position - HEADERS_COUNT) :
                null;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mListener = listener;
    }

    static class ReassignViewHolder extends RecyclerView.ViewHolder {

        public int mViewType;

        @Nullable
        @BindView(R.id.header_label)
        TextView mHeaderTextView;
        @Nullable
        @BindView(R.id.co_driver_radio_button)
        RadioButton mCoDriverRB;
        @Nullable
        @BindView(R.id.duty_icon)
        ImageView mCoDriverDutyStatus;

        public ReassignViewHolder(View itemView, int viewType) {
            super(itemView);
            mViewType = viewType;
            ButterKnife.bind(this, itemView);
        }
    }
}
