package org.tech.repos.base.ui.refresh;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Author: xuan
 * Created on 2021/3/31 14:16.
 * <p>
 * Describe:
 */
public class ExtRefreshLayout extends FrameLayout implements ExtRefresh {

    private ExtOverView.RefreshState mState;
    private GestureDetector mGestureDetector;
    private RefreshListener mHiRefreshListener;
    protected ExtOverView mExtOverView;
    private int mLastY;
    private boolean disableRefreshScroll;
    private AutoScroller mAutoScroller;
    private long refreshTime = 0L;
    private Handler handler = new Handler(Looper.getMainLooper());
    
    public ExtRefreshLayout(@NonNull Context context) {
        this(context,null);
    }

    public ExtRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExtRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), extGestureDetector);
        mAutoScroller = new AutoScroller();
    }

    @Override
    public void setDisableRefreshScroll(boolean disableRefreshScroll) {
        this.disableRefreshScroll = disableRefreshScroll;
    }

    @Override
    public void refreshFinished() {
        long time = System.currentTimeMillis() - refreshTime;
        long delay = 0;
//        System.out.println("refreshFinished:: time = " + time);
        if(time < 400 && time > 0){
            delay = time;
        }else{
            delay = 0;
        }

        handler.postDelayed(() -> {
            View head = getChildAt(0);
            int bottom = head.getBottom();
//            System.out.println("refreshFinished:: bottom = " + bottom);
            if (bottom > 0) {
                recover(bottom);
            }
            mExtOverView.onFinish();
            mExtOverView.setState(ExtOverView.RefreshState.STATE_INIT);
            mState = ExtOverView.RefreshState.STATE_INIT;
        }, delay);
    }

    @Override
    public void setRefreshListener(RefreshListener listener) {
        this.mHiRefreshListener = listener;
    }

    @Override
    public void setRefreshOverView(ExtOverView extOverView) {
        if (this.mExtOverView != null) {
            removeView(mExtOverView);
        }
        this.mExtOverView = extOverView;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        
        addView(mExtOverView,0,params);
        
    }
    
    ExtGestureDetector extGestureDetector = new ExtGestureDetector(){

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) > Math.abs(distanceY) || (mHiRefreshListener != null && !mHiRefreshListener.enableRefresh())) {
                //??????????????? ?????????????????????????????????
                return false;
            }

            if (disableRefreshScroll && mState == ExtOverView.RefreshState.STATE_REFRESH) {
                //???????????????????????????
                return true;
            }

            View head = getChildAt(0);
            View child = ScrollUtil.findScrollableChild(ExtRefreshLayout.this);
            
            if (ScrollUtil.childScrolled(child)) {
                //???????????????????????????????????????
                return false;
            }
            
            //????????????????????????????????????????????????????????????????????????????????????
            if ((mState != ExtOverView.RefreshState.STATE_REFRESH || head.getBottom() <= mExtOverView.mPullRefreshHeight) && (head.getBottom() > 0 || distanceY <= 0.0f)) {
                
                //?????????????????????
                if (mState != ExtOverView.RefreshState.STATE_OVER_RELEASE) {
                    int seed;
                    if (child.getTop() < mExtOverView.mPullRefreshHeight) {
                        seed = (int) (mLastY / mExtOverView.minDamp);
                    } else {
                        seed = (int) (mLastY / mExtOverView.maxDamp);
                    }
                    //????????????????????????????????????????????????????????????????????????
                    boolean bool = moveDown(seed, true);
                    mLastY = (int) -distanceY;
                    return bool;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    };

    /**
     * ?????????????????????header???child
     * @param offsetY ?????????
     * @param nonAuto ???????????????????????????
     * @return
     */
    private boolean moveDown(int offsetY, boolean nonAuto) {
//        System.out.println("moveDown::  offsetY = "+offsetY+"  nonAuto = " + nonAuto);
        View head = getChildAt(0);
        View child = getChildAt(1);
        int childTop = child.getTop() + offsetY;
        if (childTop <= 0) {
            //?????????????????????
            offsetY = -child.getTop();
            //??????head???child????????????????????????
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (mState != ExtOverView.RefreshState.STATE_REFRESH) {
                mExtOverView.setState(ExtOverView.RefreshState.STATE_INIT);
                mState = ExtOverView.RefreshState.STATE_INIT;
            }
        } else if (mState == ExtOverView.RefreshState.STATE_REFRESH && childTop > mExtOverView.mPullRefreshHeight) {
            //????????????????????????????????????????????????
            return false;
        } else if (childTop <= mExtOverView.mPullRefreshHeight) {
            //?????????????????????????????????
            if (mState != ExtOverView.RefreshState.STATE_VISIBLE && nonAuto) {
                //??????????????????
                mExtOverView.onVisible();
                mExtOverView.setState(ExtOverView.RefreshState.STATE_VISIBLE);
                mState = ExtOverView.RefreshState.STATE_VISIBLE;
            }

            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);

            if (childTop == mExtOverView.mPullRefreshHeight && mState == ExtOverView.RefreshState.STATE_OVER_RELEASE) {
                //??????????????????
//                System.out.println("moveDown: -------onRefresh--------");
                refresh();
            }
        } else {
            if (mState != ExtOverView.RefreshState.STATE_OVER && nonAuto) {
                //??????????????????
                mExtOverView.onOver();
                mExtOverView.setState(ExtOverView.RefreshState.STATE_OVER);
                mState = ExtOverView.RefreshState.STATE_OVER;
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
        }

        if (mExtOverView != null) {
            mExtOverView.onScroll(head.getBottom(), mExtOverView.mPullRefreshHeight);
        }
        return true;
    }

    /**
     * ??????
     */
    private void refresh() {
        if (mHiRefreshListener != null) {
            refreshTime = System.currentTimeMillis();
            mExtOverView.onRefresh();
            mExtOverView.setState(ExtOverView.RefreshState.STATE_REFRESH);
            mState = ExtOverView.RefreshState.STATE_REFRESH;
            handler.postDelayed(() -> mHiRefreshListener.onRefresh(),200);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        View head = getChildAt(0);
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK) {
            //?????????
            if (head.getBottom() > 0) {
                if (mState != ExtOverView.RefreshState.STATE_REFRESH) {//????????????????????????
                    recover(head.getBottom());
                    return false;
                }
            }
            mLastY = 0;
        }
        boolean consumed = mGestureDetector.onTouchEvent(ev);
        if ((consumed || (mState != ExtOverView.RefreshState.STATE_INIT && mState != ExtOverView.RefreshState.STATE_REFRESH) && head.getBottom() != 0)) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
            return super.dispatchTouchEvent(ev);
        }
        if (consumed) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    private void recover(int dis) {
        if (mHiRefreshListener != null && dis > mExtOverView.mPullRefreshHeight) {
            //????????????????????? dis - mHiOverView.mPullRefreshHeight
//            mAutoScroller = new AutoScroller();
            mAutoScroller.recover(dis - mExtOverView.mPullRefreshHeight);
            mExtOverView.setState(ExtOverView.RefreshState.STATE_OVER_RELEASE);
            mState = ExtOverView.RefreshState.STATE_OVER_RELEASE;
        } else {
//            mAutoScroller = new AutoScroller();
            mAutoScroller.recover(dis);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //??????head???child???????????????

        View head = getChildAt(0);
        View child = getChildAt(1);
        if (head != null && child != null) {
            int childTop = child.getTop();
            if (mState == ExtOverView.RefreshState.STATE_REFRESH) {
                head.layout(0,mExtOverView.mPullRefreshHeight - head.getMeasuredHeight(),right, mExtOverView.mPullRefreshHeight);
                child.layout(0,mExtOverView.mPullRefreshHeight,right,mExtOverView.mPullRefreshHeight + child.getMeasuredHeight());
            }else{
                head.layout(0,childTop - head.getMeasuredHeight(),right, childTop);
                child.layout(0,childTop,right,childTop + child.getMeasuredHeight());
            }

            View other;
            for (int i = 2; i < getChildCount(); i++) {
                other = getChildAt(i);
                other.layout(0,top,right,bottom);
            }
        }
    }

    private class AutoScroller implements Runnable {
        private Scroller mScroller;
        private volatile int mLastY;
        private boolean mIsFinished;

        public AutoScroller() {
            mScroller = new Scroller(getContext(),new LinearInterpolator());
            mIsFinished = true;
        }

        @Override
        public synchronized void run() {
//            System.out.println("AutoScroll:::: run:: mScroller.computeScrollOffset() = " + mScroller.computeScrollOffset());
            if (mScroller.computeScrollOffset()) {//??????????????????
//                System.out.println("AutoScroll:::: run:: -> moveDown() mLastY = " + mLastY +" mScroller.getCurrY() = " + mScroller.getCurrY());
                moveDown(mLastY - mScroller.getCurrY(), false);
//                System.out.println("--------------mLastY = mScroller.getCurrY()--------------------");
                mLastY = mScroller.getCurrY();
                post(this);
            } else {
                removeCallbacks(this);
                mIsFinished = true;
            }
        }

        void recover(int dis) {
            if (dis < 0) {
                return;
            }
            removeCallbacks(this);
//            System.out.println("--------------mLastY = 0--------------------");
            mLastY = 0;
            mIsFinished = false;
//            System.out.println("AutoScroll:::: recover:: mLastY = " + mLastY +" mIsFinished = " + mIsFinished + " dis = " + dis);
            mScroller.startScroll(0, 0, 0, dis, 300);
            post(this);
        }
        
        boolean isFinished() {
            return mIsFinished;
        }
    }
    
}
