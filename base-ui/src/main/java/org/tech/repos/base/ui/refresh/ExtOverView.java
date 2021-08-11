package org.tech.repos.base.ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tech.repos.base.lib.utils.DisplayUtil;

/**
 * Author: xuan
 * Created on 2021/3/30 17:44.
 * <p>
 * Describe: 下拉刷新的Overlay的视图，可以重载这个类来定义自己的Overlay
 */
public abstract class ExtOverView extends FrameLayout {
    
    public enum RefreshState{
        /**
         * 初始态
         */
        STATE_INIT,
        /**
         * Header展示的状态
         */
        STATE_VISIBLE,

        /**
         * 刷新中的状态
         */
        STATE_OVER,
        /**
         * 超出可刷新的距离的状态
         */
        STATE_REFRESH,
        /**
         * 超出刷新位置松开收后的状态
         */
        STATE_OVER_RELEASE
        
    }

    protected RefreshState mState = RefreshState.STATE_INIT;

    /**
     * 触发下拉舒心束缚的最小高度
     */
    public int mPullRefreshHeight;

    /**
     * 最小阻尼
     */
    public float minDamp = 1.6f;
    /**
     * 最大阻尼
     */
    public float maxDamp = 2.2f;
    
    public ExtOverView(@NonNull Context context) {
        super(context);
        preInit();
    }

    public ExtOverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        preInit();
    }

    public ExtOverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        preInit();
    }

    protected void preInit() {
        mPullRefreshHeight = DisplayUtil.INSTANCE.dp2px(66, getResources());
        init();
    }
    /**
     * 初始化
     */
    public abstract void init();

    protected abstract void onScroll(int scrollY,int mPullRefreshHeight);

    /**
     * 显示Overlay
     */
    protected abstract void onVisible();

    /**
     * 超过Overlay，释放就会加载
     */
    public abstract void onOver();

    /**
     * 正在刷新
     */
    public abstract void onRefresh();

    /**
     * 刷新完成
     */
    public abstract void onFinish();

    public void setState(RefreshState mState) {
        this.mState = mState;
    }

    public RefreshState getState() {
        return mState;
    }
}
