package com.bsmwireless.widgets.signview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SignatureLayout extends LinearLayout {

    public final int ANIMATION_DURATION = getResources().getInteger(android.R.integer.config_shortAnimTime);

    private View mRootView;

    private Unbinder mUnbinder;

    @BindView(R.id.driver_sign)
    DriverSignView mDriverSignView;

    @BindView(R.id.change_button)
    Button mChangeButton;

    private OnSaveSignatureListener mListener;

    public interface OnSaveSignatureListener {
        void onChangeClicked();
    }

    public SignatureLayout(Context context) {
        super(context);
        init(context);
    }

    public SignatureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SignatureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mRootView = inflate(context, R.layout.signature_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mUnbinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    protected void onDetachedFromWindow() {
        mChangeButton.clearAnimation();
        mUnbinder.unbind();
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.change_button)
    public void onChangeClicked() {
        mListener.onChangeClicked();
    }

    public void setImageData(String data) {
        mDriverSignView.setSignatureString(data);
    }

    public String getImageData() {
        return mDriverSignView.getSignatureString();
    }

    public void setOnSaveListener(OnSaveSignatureListener listener) {
        mListener = listener;
    }

    public void clear() {
        mDriverSignView.clear();
    }

    public void setEditable(boolean isEditable) {
        if (isEditable) {
            hideEditButton();
        } else {
            showEditButton();
        }
        mDriverSignView.setEditable(isEditable);
    }

    public boolean isEditable() {
        return mDriverSignView.isEditing();
    }

    private void hideEditButton() {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(ANIMATION_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mChangeButton.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        mChangeButton.startAnimation(animation);
    }

    private void showEditButton() {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(ANIMATION_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mChangeButton.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        mChangeButton.startAnimation(animation);
    }
}
