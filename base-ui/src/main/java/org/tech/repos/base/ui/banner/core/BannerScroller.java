package org.tech.repos.base.ui.banner.core;

import android.content.Context;
import android.widget.Scroller;

public class BannerScroller extends Scroller {
    /***
     * 值越大 滑动越慢
     */
    private int mDuration = 5000;

    public BannerScroller(Context context, int duration) {
        super(context);
        mDuration = duration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
