package org.tech.repos.base.ui.banner.core;

import androidx.annotation.LayoutRes;
import androidx.viewpager.widget.ViewPager;

import org.tech.repos.base.ui.banner.indicator.Indicator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/***
 * @description IBanner组件对外提供功能的接口
 * @author 
 * @date 2020 年 1月6日
 */
public interface IBanner {
    /***
     * 设置HIBanner组件绑定的数据
     * @param layoutResId
     * @param models
     */
    void setBannerData(@LayoutRes int layoutResId, @NotNull List<? extends BannerMo> models);

    void setBannerData(@NotNull List<? extends BannerMo> models);

    void setHiIndicator(Indicator<?> haIndicator);

    void setAutoPlay(boolean autoPlay);

    void setLoop(boolean loop);

    void setIntervalTime(int intervalTime);

    void setBindAdapter(IBindAdapter bindAdapter);

    void setScrollDuration(int duration);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener);

    void setOnBannerClickListener(OnBannerClickListener onBannerClickListener);
    
    void startPlay();
    
    void stopPlay();

    interface OnBannerClickListener {
        void onBannerClick(@NotNull BannerAdapter.HiBannerViewHolder viewHolder, @NotNull BannerMo bannerMo, int position);
    }
}
