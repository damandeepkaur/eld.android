package com.bsmwireless.screens.selectasset;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsmwireless.models.Vehicle;

import java.util.List;
import java.util.Locale;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

final class SelectAssetAdapter extends RecyclerView.Adapter<SelectAssetAdapter.Holder> {
    private List<Vehicle> mSearchList;
    private String mMessage;
    private String mSearchText;

    private View.OnClickListener mListener;

    private ItemType mCurrentType = ItemType.SEARCH;

    private enum ItemType {
        HINT,
        SEARCH
    }

    SelectAssetAdapter(@NonNull View.OnClickListener listener) {
        mListener = listener;
    }

    void setHint(@Nullable String message) {
        mCurrentType = ItemType.HINT;
        mMessage = message;

        notifyDataSetChanged();
    }

    void setSearchList(@Nullable List<Vehicle> list, @Nullable String searchText) {
        mCurrentType = ItemType.SEARCH;
        mSearchList = list;
        mSearchText = searchText;

        notifyDataSetChanged();
    }

    @Override
    public SelectAssetAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_select_asset_item, parent, false);
        view.setOnClickListener(mListener);

        return new SelectAssetAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(SelectAssetAdapter.Holder holder, int position) {
        ItemType type = ItemType.values()[getItemViewType(position)];

        switch (type) {
            case HINT:
                holder.setHint(mMessage);
                break;
            case SEARCH:
                holder.setSearch(mSearchList.get(position), mSearchText);
                break;
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;

        switch (mCurrentType) {
            case HINT:
                size = mMessage == null ? 0 : 1;
                break;

            case SEARCH:
                size = mSearchList == null ? 0 : mSearchList.size();
                break;
        }

        return size;
    }

    @Override
    public int getItemViewType(int position) {
        return mCurrentType.ordinal();
    }

    Vehicle getItem(int position) {
        return mCurrentType == ItemType.SEARCH && mSearchList != null && position < mSearchList.size() ? mSearchList.get(position) : null;
    }
    
    List<Vehicle> getItems() {
        return mSearchList;
    }

    static final class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.select_asset_item)
        TextView mTextView;

        private int mHintTextColor;
        private int mHintColor;
        private int mSearchTextColor;
        private int mSearchColor;
        private int mHintIcon;

        Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            mHintColor = ContextCompat.getColor(view.getContext(), R.color.hint);
            mHintTextColor = ContextCompat.getColor(view.getContext(), R.color.hint_text);
            mSearchColor = Color.WHITE;
            mSearchTextColor = Color.BLACK;
            mHintIcon = R.drawable.ic_info;
        }

        void setSearch(Vehicle vehicle, String searchWord) {
            String text = String.format(Locale.getDefault(), "%s [%d]", vehicle.getName(), vehicle.getBoxId());
            Spannable spannable = new SpannableString(text);

            if (searchWord != null) {
                int start = text.indexOf(searchWord);
                int end = start + searchWord.length();

                if (start > 0 && end < text.length()) {
                    spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            mTextView.setText(spannable);
            mTextView.setTextColor(mSearchTextColor);
            mTextView.setBackgroundColor(mSearchColor);
            mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        void setHint(String hint) {
            mTextView.setText(hint);
            mTextView.setTextColor(mHintTextColor);
            mTextView.setBackgroundColor(mHintColor);
            mTextView.setCompoundDrawablesWithIntrinsicBounds(mHintIcon, 0, 0, 0);
        }
    }
}