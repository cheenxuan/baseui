package org.tech.repos.base.ui.refresh;

/**
 * Author: xuan
 * Created on 2021/3/30 17:32.
 * <p>
 * Describe:
 */
public interface ExtRefresh {
    /**
     * 刷新时 是否禁止滚动
     *
     * @param disableRefreshScroll
     */
    void setDisableRefreshScroll(boolean disableRefreshScroll);

    /**
     * 刷新完成
     */
    void refreshFinished();

    /**
     * 设置刷新监听器
     * @param listener
     */
    void setRefreshListener(RefreshListener listener);

    /**
     * 设置下拉刷新的视图
     * @param extOverView
     */
    void setRefreshOverView(ExtOverView extOverView);
    
    interface RefreshListener {
        void onRefresh();

        boolean enableRefresh();
    }
    
}
