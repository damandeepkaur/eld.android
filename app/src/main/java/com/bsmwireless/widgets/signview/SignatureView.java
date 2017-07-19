package com.bsmwireless.widgets.signview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

import app.bsmuniversal.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SignatureView extends LinearLayout {

    private View mRootView;

    private Unbinder mUnbinder;

    @BindView(R.id.driver_sign)
    DriverSignView mDriverSignView;

    @BindView(R.id.change_button)
    Button mChangeButton;

    @BindView(R.id.control_buttons)
    LinearLayout mControlButtons;

    private OnSaveListener mListener;

    public interface OnSaveListener {
        void onSaveClicked(String data);
    }

    public SignatureView(Context context) {
        super(context);
        init(context);
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mUnbinder.unbind();
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
            mListener.onSaveClicked(mDriverSignView.getSignatureString());
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

    public void setOnSaveListener(OnSaveListener listener) {
        mListener = listener;
    }

    private void hideEditButton() {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mChangeButton.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mChangeButton.startAnimation(animation);
    }

    private void showEditButton() {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mChangeButton.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mChangeButton.startAnimation(animation);
    }

    private void hideControlButtons() {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mControlButtons.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mControlButtons.startAnimation(animation);
    }

    private void showControlButtons() {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mControlButtons.setVisibility(VISIBLE);
                mControlButtons.requestFocus();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mControlButtons.startAnimation(animation);
    }
}
