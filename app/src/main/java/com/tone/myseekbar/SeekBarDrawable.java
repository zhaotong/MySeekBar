package com.tone.myseekbar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

/**
 * Created by zhaotong on 2016/12/28.
 */

public class SeekBarDrawable extends Drawable implements Animatable {

    private final Paint mPaint;
    private int mSecondaryProgressColor;
    private int mProgressColor;
    private int mBgColor;
    private int mThumbColor;
    private int mTextColor;


    private Point mPoint;
    private int mProgressBgHeight;
    private int mProgressHeight;
    private int mThumbRadius;
    private int mBarHeight;
    private int mTextSize;

    private int mMax = 100;
    private int mProgress = 0;
    private int mSecondaryProgress = 0;
    private StringBuffer time = new StringBuffer("00:00");
    private int textWidth = 0;
    private int textHeight = 0;
//    private boolean isLoading = true;


    private static final int MIN_ANGLE_SWEEP = 3;
    private static final int MAX_ANGLE_SWEEP = 255;
    private int mAngleIncrement = -3;
    private static final int ANGLE_ADD = 5;
    private float mStartAngle = 0;
    private float mSweepAngle = 0;
    private RectF mOval = new RectF();

    public SeekBarDrawable(int mProgressColor, int mBgColor, int mThumbColor, int mSecondaryProgressColor) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setThumbColor(mThumbColor);
        setProgressColor(mProgressColor);
        setBgColor(mBgColor);
        setSecondaryProgressColor(mSecondaryProgressColor);
        mPoint = new Point();
    }

    private final Runnable mAnim = new Runnable() {
        @Override
        public void run() {
            if (mProgress == mSecondaryProgress) {
                onRefresh();
                invalidateSelf();
            } else {
                unscheduleSelf(this);
            }
        }
    };

    public int getSecondaryProgressColor() {
        return mSecondaryProgressColor;
    }

    public void setSecondaryProgressColor(int mSecondaryProgressColor) {
        this.mSecondaryProgressColor = mSecondaryProgressColor;
    }

    public int getSecondaryProgress() {
        return mSecondaryProgress;
    }

    public void setSecondaryProgress(int mSecondaryProgress) {
        this.mSecondaryProgress = mSecondaryProgress;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int mMax) {
        this.mMax = mMax;
        setProgress(mProgress);
    }

    public int getProgress() {
        return mProgress;
    }

    public synchronized void setProgress(int mProgress) {
        if (mProgress > mMax) {
            mProgress = mMax;
        }
        this.mProgress = mProgress;
        calculateTime();
        Rect bounds = getBounds();
        float scale = mProgress / (float) mMax;
        int width = bounds.width();
        int x = (int) (bounds.left + width * scale);
        mPoint.set(x, bounds.centerY());
//        isLoading = false;
    }

//    public boolean isLoading() {
//        return isLoading;
//    }

//    public void setLoading(boolean loading) {
//        isLoading = loading;
//        invalidateSelf();
//    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    private void calculateTime() {
        time.setLength(0);
        int m = mProgress / 1000 / 60;
        int s = mProgress / 1000 % 60;
        if (m < 10) {
            time.append("0").append(m + ":");
        } else {
            time.append(m + ":");
        }
        if (s < 10) {
            time.append("0").append(s + "");
        } else {
            time.append(s + "");
        }
    }

    public Point getPosPoint() {
        return mPoint;
    }

    public int getThumbRadius() {
        return mThumbRadius;
    }

    public void setThumbRadius(int mThumbRadius) {
        this.mThumbRadius = mThumbRadius;
    }

    public int getBarHeight() {
        return mBarHeight;
    }

    public void setBarHeight(int mBarHeight) {
        this.mBarHeight = mBarHeight;
    }

    public int getProgressBgHeight() {
        return mProgressBgHeight;
    }

    public void setProgressBgHeight(int mProgressBgHeight) {
        if (mProgressBgHeight < 0)
            mProgressBgHeight = 0;
        this.mProgressBgHeight = mProgressBgHeight;
    }


    public int getProgressHeight() {
        return mProgressHeight;
    }

    public void setProgressHeight(int mProgressHeight) {
        if (mProgressHeight < 0)
            mProgressHeight = 0;
        this.mProgressHeight = mProgressHeight;
    }


    public void getTouchBounds(Rect rect) {
        Rect bounds = getBounds();
        rect.set(mPoint.x, bounds.top, mPoint.x + textWidth, bounds.bottom);
    }


    public int getThumbColor() {
        return mThumbColor;
    }

    public void setThumbColor(int mThumbColor) {
        this.mThumbColor = mThumbColor;
        invalidateSelf();
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int mProgressColor) {
        this.mProgressColor = mProgressColor;
        invalidateSelf();
    }

    public int getBgColor() {
        return mBgColor;
    }

    public void setBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
        invalidateSelf();
    }


    @Override
    public void draw(Canvas canvas) {
        draw(canvas, mPaint, mThumbColor, mProgressColor, mSecondaryProgressColor, mBgColor);
    }

    @Override
    public void setAlpha(int alpha) {

    }


    private void draw(Canvas canvas, Paint paint, int thumbColor, int progressColor, int secondaryProgressColor, int bgColor) {
        Rect bounds = getBounds();

        float thumbX = mPoint.x;
        float thumbY = mPoint.y;
        float startLeft = bounds.left;
        float startRight = bounds.right;
        float secondaryW = bounds.width() * (mSecondaryProgress / (float) mMax);

        paint.setAntiAlias(true);
        //总长度
        paint.setColor(bgColor);
        canvas.drawRect(secondaryW, thumbY - (mProgressBgHeight >> 1), startRight, thumbY + (mProgressBgHeight >> 1), paint);
        //缓冲
        paint.setColor(secondaryProgressColor);
        canvas.drawRect(thumbX, thumbY - (mProgressBgHeight >> 1), secondaryW, thumbY + (mProgressBgHeight >> 1), paint);
        //进度
        paint.setColor(progressColor);
        canvas.drawRect(startLeft, thumbY - (mProgressHeight >> 1), thumbX, thumbY + (mProgressHeight >> 1), paint);

        if (mProgress == mSecondaryProgress) {
            int count = canvas.save();

            paint.setColor(thumbColor);
            float radius = thumbY * 3 / 4;
            float cx = Math.max(radius,thumbX);
            if (thumbX==startRight)
                cx = startRight-radius;
            canvas.drawCircle(cx, thumbY, radius, paint);

            paint.setColor(progressColor);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            mOval.set(cx - radius * 3 / 5, thumbY - radius * 3 / 5, cx + radius * 3 / 5, thumbY + radius * 3 / 5);
            canvas.drawArc(mOval, mStartAngle, -mSweepAngle, false, paint);
            paint.reset();

            scheduleSelf(mAnim, SystemClock.uptimeMillis() + 16);

            canvas.restoreToCount(count);
        } else {
            paint.setColor(thumbColor);
            paint.setTextSize(mTextSize);
            textWidth = (int) paint.measureText(time.toString());
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
            textHeight = fm.bottom - fm.top;
            int baseline = (bounds.bottom + bounds.top - fm.bottom - fm.top) / 2;

            float a = bounds.width() - textWidth * 1.2f;
            thumbX = thumbX * a / bounds.width();

            mOval.set(thumbX, thumbY - textHeight / 2f, thumbX + textWidth * 1.2f, thumbY + textHeight / 2f);
            canvas.drawRoundRect(mOval, mThumbRadius, mThumbRadius, paint);
            paint.setColor(Color.GRAY);
            canvas.drawText(time.toString(), thumbX + textWidth * 0.1f, baseline, paint);
        }
    }

    protected void onRefresh() {
        final float angle = ANGLE_ADD;
        mStartAngle += angle;

        if (mStartAngle > 360) {
            mStartAngle -= 360;
        }

        if (mSweepAngle > MAX_ANGLE_SWEEP) {
            mAngleIncrement = -mAngleIncrement;
        } else if (mSweepAngle < MIN_ANGLE_SWEEP) {
            mSweepAngle = MIN_ANGLE_SWEEP;
            return;
        } else if (mSweepAngle == MIN_ANGLE_SWEEP) {
            mAngleIncrement = -mAngleIncrement;
        }
        mSweepAngle += mAngleIncrement;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        float scale = mProgress / (float) mMax;
        int width = bounds.width();
        int x = (int) (bounds.left + width * scale);
        mPoint.set(x, bounds.centerY());
    }

    @Override
    public int getIntrinsicHeight() {
        int maxHeight = Math.max(mProgressHeight, mProgressBgHeight);
        maxHeight = Math.max(maxHeight, mThumbRadius * 2);
        maxHeight = Math.max(maxHeight, mBarHeight * 2);
        return maxHeight;
    }

    @Override
    public void start() {
        if (mProgress == mSecondaryProgress) {
//            isLoading = true;
            scheduleSelf(mAnim, SystemClock.uptimeMillis() + 16);
        }
    }

    @Override
    public void stop() {
        if (mProgress == mSecondaryProgress) {
//            isLoading = false;
            unscheduleSelf(mAnim);
            invalidateSelf();
        }
    }

    @Override
    public boolean isRunning() {
        return true;
    }
}
