package com.bsmwireless.widgets.snackbar;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import app.bsmuniversal.com.R;

public class SnackBarBehavior extends AppBarLayout.ScrollingViewBehavior {
    private final float OFFSET;

    public SnackBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        OFFSET = context.getResources().getDimensionPixelOffset(R.dimen.driver_profile_shadow_size);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency) || dependency instanceof SnackBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            return super.onDependentViewChanged(parent, child, dependency);
        }

        int translationY = (int) -Math.max(0, parent.getHeight() - dependency.getY() - OFFSET);

        child.setPadding(0, 0, 0, -translationY);
        if (child instanceof ScrollView) {
            child.scrollBy(0, -translationY);
        }

        return true;
    }
}
