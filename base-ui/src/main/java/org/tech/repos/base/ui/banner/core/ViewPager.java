package org.tech.repos.base.ui.banner.core;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;

/***
 * 实现ViewPager的自动翻页
 */
public class ViewPager extends androidx.viewpager.widget.ViewPager {
    //滚动的时间间隔
    private int mIntervalTime;
    //是否开启自动轮播
    private boolean mAutoPlay = true;
    // 标志ViewPager是否被onlayout过
    private boolean isLayout;
    //通过handler实现自动播放
    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            next();
            //  切换到下一个，延迟几秒在执行
            mHandler.postDelayed(this, mIntervalTime);
        }
    };


    public ViewPager(@NonNull Context context) {
        super(context);
    }

    public ViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
        if (!mAutoPlay) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                start();
                break;
            case MotionEvent.ACTION_DOWN:
                stop();
                break;
        }
        return super.onTouchEvent(ev);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isLayout && getAdapter() != null && getAdapter().getCount() > 0) {
            try {
                Field mScroller = androidx.viewpager.widget.ViewPager.class.getDeclaredField("mFirstLayout");
                mScroller.setAccessible(true);
                mScroller.set(this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (((Activity) getContext()).isFinishing()) {
            super.onDetachedFromWindow();
        }
        stop();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        isLayout = true;
    }

    public void setScrollDuration(int duration) {
        try {
            Field scrollerFiled = androidx.viewpager.widget.ViewPager.class.getDeclaredField("mScroller");
            scrollerFiled.setAccessible(true);
            scrollerFiled.set(this,new BannerScroller(getContext(),duration));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIntervalTime(int mIntervalTime) {
        this.mIntervalTime = mIntervalTime;
    }

    public void start() {
        mHandler.removeCallbacksAndMessages(null);
        if (mAutoPlay) {
            mHandler.postDelayed(mRunnable, mIntervalTime);
        }
    }

    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private int next() {
        int nextPosition = -1;
        if (getAdapter() == null || getAdapter().getCount() <= 1) {
            stop();
            return nextPosition;
        }
        nextPosition = getCurrentItem() + 1;
        //下一个索引大于adapter的view的最大数量的时候重新开始
        if (nextPosition >= getAdapter().getCount()) {
            // 获取第一个item的索引
            nextPosition = ((BannerAdapter) getAdapter()).getFirstItem();
        }
        setCurrentItem(nextPosition, true);
        return nextPosition;
    }
}
