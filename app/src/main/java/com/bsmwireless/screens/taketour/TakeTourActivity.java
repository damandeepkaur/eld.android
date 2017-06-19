package com.bsmwireless.screens.taketour;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TakeTourActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_tour);
        ButterKnife.bind(this);

        ImageAdapter adapter = new ImageAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(-1);
        viewPager.setCurrentItem(0);
        viewPager.setClipToPadding(false);
    }

    @OnClick(R.id.close_tour)
    void closeTour() {
        finish();
    }


    public class ImageAdapter extends PagerAdapter {

        private Context mContext;

        private int[] tourImages = new int[]{
                R.drawable.tour1,
                R.drawable.tour2,
                R.drawable.tour3,
                R.drawable.tour4,
                R.drawable.tour5,
                R.drawable.tour6,
                R.drawable.tour7,
                R.drawable.tour8,
                R.drawable.tour9,
                R.drawable.tour10,
                R.drawable.tour11
        };

        public ImageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View itemView = inflater.inflate(R.layout.take_tour_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imgtour);
            imageView.setImageResource(tourImages[position]);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return tourImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
