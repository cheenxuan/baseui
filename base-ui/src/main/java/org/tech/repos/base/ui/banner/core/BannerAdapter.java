package org.tech.repos.base.ui.banner.core;

import android.content.Context;
import android.os.SystemClock;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends PagerAdapter {
    private Context mContext;
    private SparseArray<HiBannerViewHolder> mCacheViews = new SparseArray<>();
    private IBanner.OnBannerClickListener mBannerClickListener;
    private IBindAdapter mBindAdapter;
    private List<? extends BannerMo> models = new ArrayList<>();
    //fix: data.size == 2
    private List<? extends BannerMo> fakeModels = new ArrayList<>();
    //No quick clicks
    private long lastClickTime;
    private long interval = 1000L;

    public BannerAdapter(Context context) {
        this.mContext = context;
    }

    public void setBannerData(@NotNull List<? extends BannerMo> models) {
        this.models = models;

        //fix: data.size == 2
        if (models.size() == 2) {
            this.fakeModels = models;
        }

        //初始化数据
        initCachedView();
        notifyDataSetChanged();
    }

    public void setBindAdapter(IBindAdapter bindAdapter) {
        this.mBindAdapter = bindAdapter;
    }


    public void setOnBannerClickListener(IBanner.OnBannerClickListener onBannerClickListener) {
        this.mBannerClickListener = onBannerClickListener;

    }

    public void setLayoutResId(@LayoutRes int layoutResId) {
        this.mLayoutResId = layoutResId;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
    }


    public void setLoop(boolean loop) {
        this.mLoop = loop;
    }

    /***
     * 是否开启自动轮播
     */
    private boolean mAutoPlay = true;
    /***
     * 非自动轮播状态下是否可以循环切换
     */
    private boolean mLoop = false;

    private int mLayoutResId = -1;


    @Override
    public int getCount() {
        //无限轮播
        return mAutoPlay ? Integer.MAX_VALUE : (mLoop ? Integer.MAX_VALUE : getRealCount());
    }

    /***
     * 获取Banner页面的数量
     * @return
     */
    public int getRealCount() {
        return models == null ? 0 : models.size() + (fakeModels == null ? 0 : fakeModels.size());
    }

    /***
     * 获取fake Banner页面的数量
     * @return
     */
    public int getFakeCount() {
        return fakeModels == null ? 0 : fakeModels.size();
    }


    /***
     * 获取初次展示的item的位置
     * @return
     */
    public int getFirstItem() {
        return Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % getRealCount();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /***
     * 实例化item的方法
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int realPosition = position;
        if (getRealCount() > 0) {
            realPosition = position % getRealCount();
        }
        HiBannerViewHolder viewHolder = mCacheViews.get(realPosition);
        if (container.equals(viewHolder.rootView.getParent())) {
            container.removeView(viewHolder.rootView);
        }

        //fix: data.size == 2
        if (realPosition >= getFakeCount()) {
            realPosition -= getFakeCount();
        }

        onBind(viewHolder, models.get(realPosition), realPosition);
        if (viewHolder.rootView.getParent() != null) {
            ((ViewGroup) viewHolder.rootView.getParent()).removeView(viewHolder.rootView);
        }

        container.addView(viewHolder.rootView);
        return viewHolder.rootView;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        //让item每次都能刷新
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }

    protected void onBind(@NotNull final HiBannerViewHolder viewHolder, @NotNull final BannerMo bannerMo, int position) {
        viewHolder.rootView.setOnClickListener(v -> {
            if (mBannerClickListener != null) {
                //No quick clicks
                long currentTime = SystemClock.elapsedRealtime();
                if (currentTime - lastClickTime > interval) {
                    mBannerClickListener.onBannerClick(viewHolder, bannerMo, position);
                    lastClickTime = currentTime;
                }
            }
        });
        if (mBindAdapter != null) {
            mBindAdapter.onBind(viewHolder, bannerMo, position);
        }
    }

    private void initCachedView() {
        mCacheViews = new SparseArray<>();
        for (int i = 0; i < models.size(); i++) {
            HiBannerViewHolder viewHolder = new HiBannerViewHolder(createView(LayoutInflater.from(mContext), null));
            mCacheViews.put(i, viewHolder);
        }
        //fix：data.size == 2 添加fakeView
        for (int i = 0; i < fakeModels.size(); i++) {
            HiBannerViewHolder viewHolder = new HiBannerViewHolder(createView(LayoutInflater.from(mContext), null));
            mCacheViews.put(models.size() + i, viewHolder);
        }
    }

    private View createView(LayoutInflater layoutInflater, ViewGroup parent) {
        if (mLayoutResId == -1) {
            throw new IllegalArgumentException("you must be set setLayoutResId first");
        }
        return layoutInflater.inflate(mLayoutResId, parent, false);
    }

    public static class HiBannerViewHolder {

        private SparseArray<View> viewSparseArray;
        public View rootView;

        public HiBannerViewHolder(View rootView) {
            this.rootView = rootView;
        }

        public SparseArray<View> getViewSparseArray() {
            return viewSparseArray;
        }

        public <V extends View> V findViewById(int id) {
            if (!(rootView instanceof ViewGroup)) {
                return (V) rootView;
            }
            if (this.viewSparseArray == null) {
                this.viewSparseArray = new SparseArray<>();
            }
            V childView = (V) viewSparseArray.get(id);
            if (childView == null) {
                childView = rootView.findViewById(id);
                this.viewSparseArray.put(id, childView);

            }
            return childView;
        }

    }
}
