package com.bsmwireless.screens.roadside;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.bsmuniversal.com.R;

public class RoadsideAdapter extends RecyclerView.Adapter<RoadsideAdapter.ViewHolder> {
    private List<String> mItems;
    private int mColumnCount = 1;
    private int mRowCount = 1;
    private boolean mShowGrid;

    private List<Integer> mTitleRowIndexes;

    public RoadsideAdapter(int columnCount, List<String> items, @NonNull List<Integer> titleIndexes, boolean showGrid) {
        mColumnCount = columnCount;
        mItems = items;
        mShowGrid = showGrid;

        mTitleRowIndexes = titleIndexes;
    }

    public final void setData(List<String> items) {
        mItems = items;
        mRowCount = mItems.size() / mColumnCount;
        notifyDataSetChanged();
    }

    @Override
    public final RoadsideAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new TextView(new ContextThemeWrapper(parent.getContext(), R.style.GridItem)));
    }

    @Override
    public final void onBindViewHolder(RoadsideAdapter.ViewHolder holder, int position) {
        holder.bind(mItems.get(getPosition(position)), mTitleRowIndexes.contains(position % mRowCount), mShowGrid);
    }

    @Override
    public final int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    private int getPosition(int position) {
        return position / mRowCount + position % mRowCount * mColumnCount;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private int mTitleSize;
        private int mTextSize;
        private int mTitleColor;
        private int mTextColor;

        ViewHolder(TextView itemView) {
            super(itemView);
            mTextView = itemView;
            mTitleSize = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.grid_title_size);
            mTextSize = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.grid_text_size);
            mTitleColor = ContextCompat.getColor(itemView.getContext(), R.color.black_5);
            mTextColor = Color.WHITE;
        }

        public final void bind(String data, boolean isTitle, boolean showGrid) {
            mTextView.setText(data);

            if (isTitle) {
                mTextView.setBackgroundColor(mTitleColor);
                mTextView.setTypeface(null, Typeface.BOLD);
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);
            } else {
                if (showGrid) {
                    mTextView.setBackgroundResource(R.drawable.grid_item_background);
                } else {
                    mTextView.setBackgroundColor(mTextColor);
                }
                mTextView.setTypeface(null, Typeface.NORMAL);
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            }
        }
    }
}
