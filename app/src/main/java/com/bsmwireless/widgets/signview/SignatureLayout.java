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

    private static final int ANIMATION_DURATION = 500;

    private View mRootView;

    private Unbinder mUnbinder;

    @BindView(R.id.driver_sign)
    DriverSignView mDriverSignView;

    @BindView(R.id.change_button)
    Button mChangeButton;

    @BindView(R.id.control_buttons)
    LinearLayout mControlButtons;

    private OnSaveSignatureListener mListener;

    private List<Animation> mStartedAnimations;

    public interface OnSaveSignatureListener {
        void onSaveSignatureClicked(String data);
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
        mStartedAnimations = new ArrayList<>();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mUnbinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    protected void onDetachedFromWindow() {
        mUnbinder.unbind();
        if (mStartedAnimations.size() > 0) {
            mStartedAnimations.forEach(Animation::cancel);
        }
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.change_button)
    public void onChangeClicked() {
        hideEditButton();
        showControlButtons();

        mDriverSignView.setEditable(true);
    }

    @OnClick(R.id.ok_button)
    public void onSaveClicked() {
        hideControlButtons();
        showEditButton();

        if (mListener != null) {
            mListener.onSaveSignatureClicked(mDriverSignView.getSignatureString());
        }

        mDriverSignView.setEditable(false);
    }

    @OnClick(R.id.clear_button)
    public void onClearClicked() {
        mDriverSignView.clear();
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

    private void hideEditButton() {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(ANIMATION_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mChangeButton.setVisibility(GONE);
                mStartedAnimations.remove(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mChangeButton.startAnimation(animation);
        mStartedAnimations.add(animation);
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
            public void onAnimationEnd(Animation animation) {
                mStartedAnimations.remove(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mChangeButton.startAnimation(animation);
        mStartedAnimations.add(animation);
    }

    private void hideControlButtons() {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(ANIMATION_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mControlButtons.setVisibility(GONE);
                mStartedAnimations.remove(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mControlButtons.startAnimation(animation);
        mStartedAnimations.add(animation);
    }

    private void showControlButtons() {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(ANIMATION_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mControlButtons.setVisibility(VISIBLE);
                mControlButtons.requestFocus();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mStartedAnimations.remove(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mControlButtons.startAnimation(animation);
        mStartedAnimations.add(animation);
    }
}
