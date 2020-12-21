package com.leory.dragphotodemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Description: 图片预览view
 * @Author: leory
 * @Time: 2020/12/21
 */
public class PhotoViewPage extends FrameLayout {
    private static final String TAG = PhotoViewPage.class.getSimpleName();

    private float mDownX;
    private float mDownY;
    private float MAX_TRAN_Y;
    private Rect mSrcRect;
    private Rect mDestRect;

    public PhotoViewPage(@NonNull Context context) {
        this(context, null);
    }

    public PhotoViewPage(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoViewPage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.page_photo_view, this);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (ev.getPointerCount() == 1) {
                    float tranY = ev.getRawY() - mDownY;
                    float tranX = ev.getRawX() - mDownX;
                    setTranslationX(tranX);
                    setTranslationY(tranY);
                    if (tranY > 0 && tranY < MAX_TRAN_Y) {
                        float scale = 1f - Math.abs(tranY) / MAX_TRAN_Y;
                        Log.d(TAG, "scale: " + scale);
                        setScaleX(scale);
                        setScaleY(scale);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                onActionUp();
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 显示
     *
     * @param srcRect
     */
    public void showPhotoView(Rect srcRect) {
        mSrcRect = srcRect;
        setVisibility(View.VISIBLE);
        setTranslationX(0);
        setTranslationY(0);
        setScaleX(1);
        setScaleY(1);
        post(() -> {
            MAX_TRAN_Y = getHeight();
            int[] location = new int[2];
            getLocationOnScreen(location);
            mDestRect = new Rect(location[0], location[1], location[0] + getWidth(), location[1] + getHeight());
            float diffX = mDestRect.centerX() - mSrcRect.centerX();
            float diffY = mDestRect.centerY() - mSrcRect.centerY();
            float smallScale = 1f * mSrcRect.width() / mDestRect.width();
            ValueAnimator enterAnim = ValueAnimator.ofFloat(0, 1);
            enterAnim.setDuration(300);
            enterAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float progress = (float) animation.getAnimatedValue();
                    float tranX = diffX * (progress - 1);
                    setTranslationX(tranX);
                    float tranY = diffY * (progress - 1);
                    setTranslationY(tranY);
                    float scale = (1 - smallScale) * progress + smallScale;
                    setScaleX(scale);
                    setScaleY(scale);
                    setBgTransport(progress);
                }
            });
            enterAnim.start();
        });
    }

    public void closePhotoView() {
        setBgTransport(0);
        float currentTranX = getTranslationX();
        float currentTranY = getTranslationY();
        float diffX = mDestRect.centerX() - mSrcRect.centerX() + currentTranX;
        float diffY = mDestRect.centerY() - mSrcRect.centerY() + currentTranY;
        Log.d(TAG, "diffY: " + diffY);
        float smallScale = 1f * mSrcRect.width() / mDestRect.width();
        float currentScale = getScaleX();
        ValueAnimator exitAnim = ValueAnimator.ofFloat(1, 0);
        exitAnim.setDuration(300);
        exitAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                float tranX = diffX * (progress - 1) + currentTranX;
                setTranslationX(tranX);
                float tranY = diffY * (progress - 1) + currentTranY;
                setTranslationY(tranY);
                float scale = (currentScale - smallScale) * progress + smallScale;
                setScaleX(scale);
                setScaleY(scale);
                setBgTransport(progress);
                if (progress == 0) {
                    setVisibility(View.GONE);
                }
            }
        });
        exitAnim.start();
    }

    public void setBgTransport(float progress) {
        float alpha;
        if (progress < 0.5f) {
            alpha = 0f;
        } else {
            alpha = (progress - 0.5f) * 2;
        }
        int alphaValue = (int) (alpha * 255);
        int bgColor = Color.argb(alphaValue, 0, 0, 0);
        setBackgroundColor(bgColor);
    }


    private void onActionUp() {
        float tranX = getTranslationX();
        float tranY = getTranslationY();
        float scale = getScaleX();
        if (tranX != 0 || tranY != 0 || scale != 1f) {
            if (scale > 0.8f) {
                ValueAnimator releaseAnim = ValueAnimator.ofFloat(1, 0);
                releaseAnim.setDuration(300);
                releaseAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float progress = (float) animation.getAnimatedValue();
                        float newTranX = tranX * progress;
                        setTranslationX(newTranX);
                        float newTranY = tranY * progress;
                        setTranslationY(newTranY);
                        float newScale = (1f - scale) * (1f - progress) + scale;
                        setScaleX(newScale);
                        setScaleY(newScale);
                    }
                });
                releaseAnim.start();
            } else {
                closePhotoView();
            }

        }
    }

}
