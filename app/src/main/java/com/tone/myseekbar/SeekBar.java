package com.tone.myseekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Created by zhaotong on 2016/12/28.
 */

public class SeekBar extends View {

    private int mMax = 100;
    private int mProgress = 0;
    private int mSecondaryProgress = 0;
    private int mProgressBgHeight = 2;
    private int mProgressHeight = 2;
    private int mThumbRadius = 6;
    private int mBarHeight = 12;
    private int mTextSize = 12;

    private SeekBarDrawable mSeekBarDrawable;
    private float mDownX;
    private float mTouchSlop;
    private boolean mIsDragging;
    private Rect mTempRect = new Rect();

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public SeekBar(Context context) {
        super(context);
    }

    public SeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet set) {
        final Context context = getContext();
        setFocusable(true);
        setWillNotDraw(false);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mSeekBarDrawable = new SeekBarDrawable(Color.parseColor("#e91e63"),Color.parseColor("#bebebe"),Color.parseColor("#ffffff"),Color.parseColor("#ffffff"));
        mSeekBarDrawable.setCallback(this);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mProgressHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mProgressHeight, dm);
        mThumbRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mThumbRadius, dm);
        mBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBarHeight, dm);
        mProgressBgHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mProgressBgHeight, dm);
        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, dm);

        setProgressHeight(mProgressHeight);
        setBarHeight(mBarHeight);
        setProgressBgHeight(mProgressBgHeight);
        setThumbRadius(mThumbRadius);
        setTextSize(mTextSize);

        setMax(mMax);
        setProgress(mProgress);
        setSecondaryProgress(mSecondaryProgress);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = mSeekBarDrawable.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(widthSize, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSeekBarDrawable.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mSeekBarDrawable.draw(canvas);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who==mSeekBarDrawable ||super.verifyDrawable(who);
    }


        @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
            mSeekBarDrawable.start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        int actionMasked = event.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                startDragging(event, isInScrollingContainer());
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsDragging) {
                    updateDragging(event);
                } else {
                    final float x = event.getX();
                    if (Math.abs(x - mDownX) > mTouchSlop) {
                        startDragging(event, false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                break;
        }
        return true;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    public int getSecondaryProgress() {
        return mSecondaryProgress;
    }

    public void setSecondaryProgress(int mSecondaryProgress) {
        this.mSecondaryProgress = mSecondaryProgress;
        mSeekBarDrawable.setSecondaryProgress(mSecondaryProgress);
        invalidate();
    }

    public int getProgressBgHeight() {
        return mProgressBgHeight;
    }

    public void setProgressBgHeight(int mProgressBgHeight) {
        this.mProgressBgHeight = mProgressBgHeight;
        mSeekBarDrawable.setProgressBgHeight(mProgressBgHeight);
        invalidate();
    }

    public int getProgressHeight() {
        return mProgressHeight;
    }

    public void setProgressHeight(int mProgressHeight) {
        this.mProgressHeight = mProgressHeight;
        mSeekBarDrawable.setProgressHeight(mProgressHeight);
        invalidate();
    }

    public int getThumbRadius() {
        return mThumbRadius;
    }

    public void setThumbRadius(int mThumbRadius) {
        this.mThumbRadius = mThumbRadius;
        mSeekBarDrawable.setThumbRadius(mThumbRadius);
        invalidate();
    }

    public int getBarHeight() {
        return mBarHeight;
    }

    public void setBarHeight(int mBarHeight) {
        this.mBarHeight = mBarHeight;
        mSeekBarDrawable.setBarHeight(mBarHeight);
        invalidate();
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        mSeekBarDrawable.setTextSize(mTextSize);
        invalidate();
    }

    protected void onStopTrackingTouch() {
        mIsDragging = false;
        setPressed(false);
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    private boolean isInScrollingContainer() {
        ViewParent p = getParent();
        while (p != null && p instanceof ViewGroup) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    private void startDragging(MotionEvent ev, boolean ignoreTrackIfInScrollContainer) {
        final Rect bounds = mTempRect;
        mSeekBarDrawable.getTouchBounds(bounds);
        boolean isDragging = (bounds.contains((int) ev.getX(), (int) ev.getY()));
        if (!isDragging && !ignoreTrackIfInScrollContainer) {
            isDragging = true;
            updateDragging(ev);
        }
        if (isDragging) {
            onStartTrackingTouch();
        }
    }

    protected void onStartTrackingTouch() {
        mIsDragging = true;
        setPressed(true);
        attemptClaimDrag();
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    private void attemptClaimDrag() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    private void updateDragging(MotionEvent ev) {
        int left = getPaddingLeft();
        int right = getWidth() - getPaddingRight();

        int posX = (int) ev.getX();
        if (posX < left) {
            posX = left;
        } else if (posX > right) {
            posX = right;
        }

        int available = right - left;
        float scale = (float) (posX - left) / (float) available;

        int progress = Math.round(scale * mMax);
        setProgress(progress, true, scale);
    }



    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        setProgress(progress, false, -1);
    }

    private void setProgress(int value, boolean fromUser, float scale) {
        value = Math.min(mMax, value);
        if (mProgress != value) {
            mProgress = value;
            onProgressChanged(value, fromUser);
        }
        mSeekBarDrawable.setProgress(value);
        invalidate();
    }

    protected void onProgressChanged(int value, boolean fromUser) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, value, fromUser);
        }
    }

    public void setMax(int max) {
        mMax = max;
        if (mMax <= 0) {
            mMax = 100;
        }
        if (mProgress > mMax) {
            mProgress = mMax;
            setProgress(mProgress);
        }
        mSeekBarDrawable.setMax(mMax);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        BarState state = new BarState(superState);
        state.progress = getProgress();
        state.max = mMax;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(BarState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        BarState barState = (BarState) state;
        setMax(barState.max);
        setProgress(barState.progress);
        super.onRestoreInstanceState(barState.getSuperState());
    }

    static class BarState extends BaseSavedState {
        public static final Creator<BarState> CREATOR =
                new Creator<BarState>() {

                    @Override
                    public BarState[] newArray(int size) {
                        return new BarState[size];
                    }

                    @Override
                    public BarState createFromParcel(Parcel incoming) {
                        return new BarState(incoming);
                    }
                };
        private int progress;
        private int max;

        public BarState(Parcel source) {
            super(source);
            progress = source.readInt();
            max = source.readInt();
        }

        public BarState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(progress);
            dest.writeInt(max);
        }
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(SeekBar seekBar);

        void onStopTrackingTouch(SeekBar seekBar);
    }

}
