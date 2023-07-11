package org.tech.repos.base.ui.banner.core;

import android.content.Context;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;
import org.tech.repos.base.ui.R;
import org.tech.repos.base.ui.banner.indicator.CircleIndicator;
import org.tech.repos.base.ui.banner.indicator.Indicator;

import java.util.ArrayList;
import java.util.List;

/***
 * HiBanner的控制器
 * 辅助HiBanner完成各种功能的控制
 * 将HiBanner的一些逻辑内聚在这，保证暴露给使用者的HiBanner干净整洁
 */
public class BannerDelegate implements IBanner, androidx.viewpager.widget.ViewPager.OnPageChangeListener {
    private Context mContext;
    private Banner mBanner;
    private BannerAdapter mAdapter;
    private Indicator<?> mIndicator;
    private boolean mAutoPlay;
    private boolean mLoop;
    private List<? extends BannerMo> mBannerMos = new ArrayList<>();
    private androidx.viewpager.widget.ViewPager.OnPageChangeListener mOnPageChangeListener;
    private int mIntervalTime = 5000;
    private OnBannerClickListener mOnBannerClickListener;
    private ViewPager mViewPager;
    private int mScrollDuration = -1;
    private IBindAdapter bindAdapter;

    public BannerDelegate(Context context, Banner banner) {
        this.mContext = context;
        this.mBanner = banner;
    }

    @Override
    public void setBannerData(int layoutResId, @NotNull List<? extends BannerMo> models) {
        mBannerMos = models;
        init(layoutResId);
    }

    @Override
    public void setBannerData(@NotNull List<? extends BannerMo> models) {
        setBannerData(R.layout.banner_item_image, models);
    }

    public void setAdapter(BannerAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public void setOnPageChangeListener(androidx.viewpager.widget.ViewPager.OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

    @Override
    public void setOnBannerClickListener(OnBannerClickListener onBannerClickListener) {
        this.mOnBannerClickListener = onBannerClickListener;
    }

    @Override
    public void startPlay() {
        if (mViewPager != null) {
            mViewPager.start();
        }
    }

    @Override
    public void stopPlay() {
        if (mViewPager != null) {
            mViewPager.stop();
        }
    }

    @Override
    public void setHiIndicator(Indicator<?> haIndicator) {
        this.mIndicator = haIndicator;
    }

    @Override
    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
        if (mAdapter != null) {
            mAdapter.setAutoPlay(autoPlay);
        }
        if (mViewPager != null) {
            mViewPager.setAutoPlay(autoPlay);
        }
    }

    @Override
    public void setLoop(boolean loop) {
        this.mLoop = loop;
    }

    @Override
    public void setIntervalTime(int intervalTime) {
        if (intervalTime > 0) {
            this.mIntervalTime = intervalTime;
        }
    }

    @Override
    public void setBindAdapter(IBindAdapter bindAdapter) {
        this.bindAdapter = bindAdapter;
        if (mAdapter != null) {
            mAdapter.setBindAdapter(bindAdapter);
        }
    }

    /***
     * 设置ViewPager内部的切换速度
     * @param duration
     */
    @Override
    public void setScrollDuration(int duration) {
        this.mScrollDuration = duration;
        if (mViewPager != null && duration > 0) {
            mViewPager.setScrollDuration(duration);
        }
    }


    private void init(int layoutResId) {
        if (mAdapter == null) {
            mAdapter = new BannerAdapter(mContext);

        }
        if (mIndicator == null) {
            mIndicator = new CircleIndicator(mContext);
        }

        mIndicator.onInflate(mBannerMos.size());
        mAdapter.setLayoutResId(layoutResId);
        mAdapter.setBindAdapter(bindAdapter);
        mAdapter.setBannerData(mBannerMos);
        mAdapter.setAutoPlay(mBannerMos.size() > 1);
        mAdapter.setLoop(mBannerMos.size() > 1);
        mAdapter.setOnBannerClickListener(mOnBannerClickListener);
        mViewPager = new ViewPager(mContext);
        mViewPager.setIntervalTime(mIntervalTime);
        mViewPager.setAutoPlay(mBannerMos.size() > 1);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mAdapter);
        if (mScrollDuration > 0) {
            mViewPager.setScrollDuration(mScrollDuration);
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        if (mLoop || mAutoPlay && mAdapter.getRealCount() != 0) {
            //无限轮播关键点：使第一张能反向滑动到最后一张，以达到无限滚动的效果
            int firstItem = mAdapter.getFirstItem();
            mViewPager.setCurrentItem(firstItem, false);
        }
        // 清除所有的View
        mBanner.removeAllViews();
        mBanner.addView(mViewPager, params);
        mBanner.addView(mIndicator.get(), params);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null && mAdapter.getRealCount() != 0) {
            mOnPageChangeListener.onPageScrolled(position / mAdapter.getRealCount(), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {

        System.out.println("--------------" + position + "----------------------");

        if (mAdapter.getRealCount() == 0) {
            return;
        }
        position = position % mAdapter.getRealCount();
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
        if (mIndicator != null) {
            //fix：data.size == 2
            mIndicator.onPointChange(position >= mAdapter.getFakeCount() ? position - mAdapter.getFakeCount() : position, mAdapter.getRealCount() - mAdapter.getFakeCount());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }

//        switch (state) {
//            case ViewPager.SCROLL_STATE_DRAGGING://start Sliding
//                if (mViewPager.getCurrentItem() == count + 1) {
//                    mViewPager.setCurrentItem(1, false);
//                } else if (currentItem == 0) {
//                    mViewPager.setCurrentItem(count, false);
//                }
//                break;
//            case 2://end Sliding
//                break;
//        }
    }
}
