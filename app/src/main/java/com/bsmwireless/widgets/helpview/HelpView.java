package com.bsmwireless.widgets.helpview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bsmwireless.widgets.common.FontTextView;

import app.bsmuniversal.com.R;

public final class HelpView extends RelativeLayout {

    public enum ArrowType {
        CLOCKWISE(R.drawable.ic_arrow_clockwise),
        STRAIGHT(R.drawable.ic_arrow_straight),
        COUNTERCLOCKWISE(R.drawable.ic_arrow_counterclockwise);

        private int mId;

        ArrowType(int id) {
            mId = id;
        }
    }

    public enum PositionType {
        TOP(0),
        RIGHT(90),
        BOTTOM(180),
        LEFT(270);

        private int mRotation;

        PositionType(int rotation) {
            mRotation = rotation;
        }
    }

    public HelpView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HelpView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HelpView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_help, null);
    }

    public void addView(HelpModel model) {
        FontTextView view = new FontTextView(new ContextThemeWrapper(getContext(), R.style.FontTextViewStyle));
        view.setText(model.getMessage());

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setPosition(view, model);
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        addView(view);
    }

    public void clearView() {
        removeAllViews();
    }

    private void setPosition(TextView target, HelpModel model) {
        Drawable drawable = AppCompatResources.getDrawable(getContext(), model.getArrow().mId);

        //incorrect resource
        if (drawable == null) {
            return;
        }

        //drawable size
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();

        //target size
        int targetWidth = target.getWidth();
        int targetHeight = target.getHeight();

        //target shift according to anchor center
        int shiftHeight = 0;
        int shiftWidth = 0;

        GravityDrawable gravityDrawable = new GravityDrawable(drawable, model.getGravity(), model.getPosition().mRotation);

        switch (model.getPosition()) {
            case LEFT:
                target.setCompoundDrawablesWithIntrinsicBounds(gravityDrawable, null, null, null);
                break;

            case TOP:
                target.setCompoundDrawablesWithIntrinsicBounds(null, gravityDrawable, null, null);
                break;

            case RIGHT:
                target.setCompoundDrawablesWithIntrinsicBounds(null, null, gravityDrawable, null);
                shiftWidth -= targetWidth;
                break;

            case BOTTOM:
                target.setCompoundDrawablesWithIntrinsicBounds(null, null, null, gravityDrawable);
                shiftHeight -= (targetHeight + drawableHeight);
                break;
        }

        if (model.getPosition().mRotation % 180 == 0) {
            shiftWidth += getGravityShift(targetWidth, drawableWidth, model.getGravity());
            shiftWidth += getArrowShift(drawableWidth, model.getArrow());
        } else {
            shiftHeight += getGravityShift(targetHeight, drawableHeight, model.getGravity());
            shiftHeight += getArrowShift(drawableHeight, model.getArrow());
        }

        setPosition(target, model.getX() + shiftWidth, model.getY() + shiftHeight);
    }

    private int getGravityShift(int targetSize, int drawableSize, GravityDrawable.GravityType gravity) {
        switch (gravity) {
            case CENTER:
                return -targetSize / 2 + drawableSize / 2;

            case END:
                return -targetSize + drawableSize;

            default:
                return 0;
        }
    }

    private int getArrowShift(int drawableSize, ArrowType arrow) {
        switch (arrow) {
            case CLOCKWISE:
                return -drawableSize;

            case STRAIGHT:
                return -drawableSize / 2;

            default:
                return 0;
        }
    }

    private void setPosition(TextView view, int x, int y) {
        view.setX(x);
        view.setY(y);
    }

    public static final class HelpModel implements Parcelable {
        private int mX;
        private int mY;
        private String mMessage;
        private HelpView.ArrowType mArrow;
        private PositionType mPosition;
        private GravityDrawable.GravityType mGravity;

        public HelpModel() {
        }

        public HelpModel(View view, String message, ArrowType arrow, PositionType position, GravityDrawable.GravityType gravity) {
            if (view != null) {
                Rect rect = new Rect();
                view.getGlobalVisibleRect(rect);

                mX = rect.centerX();
                mY = rect.centerY();
            }

            mMessage = message;

            mArrow = arrow;
            mPosition = position;
            mGravity = gravity;
        }

        public int getX() {
            return mX;
        }

        public void setX(int x) {
            mX = x;
        }

        public int getY() {
            return mY;
        }

        public void setY(int y) {
            mY = y;
        }

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            mMessage = message;
        }

        public HelpView.ArrowType getArrow() {
            return mArrow;
        }

        public void setArrow(HelpView.ArrowType arrow) {
            mArrow = arrow;
        }

        public PositionType getPosition() {
            return mPosition;
        }

        public void setPosition(PositionType position) {
            mPosition = position;
        }

        public GravityDrawable.GravityType getGravity() {
            return mGravity;
        }

        public void setGravity(GravityDrawable.GravityType gravity) {
            mGravity = gravity;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mX);
            dest.writeInt(mY);
            dest.writeString(mMessage);
            dest.writeInt(mArrow == null ? -1 : mArrow.ordinal());
            dest.writeInt(mPosition == null ? -1 : mPosition.ordinal());
            dest.writeInt(mGravity == null ? -1 : mGravity.ordinal());
        }

        protected HelpModel(Parcel in) {
            mX = in.readInt();
            mY = in.readInt();
            mMessage = in.readString();
            int tmpMArrowType = in.readInt();
            mArrow = tmpMArrowType == -1 ? null : ArrowType.values()[tmpMArrowType];
            int tmpMPositionType = in.readInt();
            mPosition = tmpMPositionType == -1 ? null : PositionType.values()[tmpMPositionType];
            int tmpMGravity = in.readInt();
            mGravity = tmpMGravity == -1 ? null : GravityDrawable.GravityType.values()[tmpMGravity];
        }

        public static final Creator<HelpModel> CREATOR = new Creator<HelpModel>() {
            @Override
            public HelpModel createFromParcel(Parcel source) {
                return new HelpModel(source);
            }

            @Override
            public HelpModel[] newArray(int size) {
                return new HelpModel[size];
            }
        };
    }
}
