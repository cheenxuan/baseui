package org.tech.repos.base.ui.tab.top;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.tech.repos.base.lib.utils.DisplayUtil;
import org.tech.repos.base.ui.R;
import org.tech.repos.base.ui.tab.common.ITabLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: xuan
 * Created on 2021/3/23 17:29.
 * <p>
 * Describe:
 */
public class TabTopLayout extends HorizontalScrollView implements ITabLayout<TabTop, TabTopInfo<?>> {
    private List<OnTabSelectedListener<TabTopInfo<?>>> tabSelcetedListeners = new ArrayList<>();
    private TabTopInfo<?> selectedInfo;
    private List<? extends TabTopInfo<?>> infoList;
    private boolean canScroll = true;
    private boolean fullScreen = false;
    
    
    public TabTopLayout(Context context) {
        this(context,null);
    }

    public TabTopLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TabTopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
        setVerticalScrollBarEnabled(false);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabTopLayout);
        canScroll = typedArray.getBoolean(R.styleable.TabTopLayout_canScroll,true);
        fullScreen = typedArray.getBoolean(R.styleable.TabTopLayout_fullScrenn,false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setNestedScrollingEnabled(canScroll);
        }

        typedArray.recycle();
    }

    @Override
    public TabTop findTab(@NonNull TabTopInfo<?> data) {
        ViewGroup ll = getRootLayout(false);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child instanceof TabTop) {
                TabTop tab = (TabTop) child;
                if (tab.getTabInfo() == data) {
                    return tab;
                }
            }
        }
        return null;
    }

    @Override
    public void addTabSelectedChangeListener(OnTabSelectedListener<TabTopInfo<?>> listener) {
        tabSelcetedListeners.add(listener);
    }

    @Override
    public void defaultSelect(@NonNull TabTopInfo<?> defaultInfo) {
        onSelected(defaultInfo);
    }

    private void onSelected(@NonNull TabTopInfo<?> nextInfo) {
        for (OnTabSelectedListener<TabTopInfo<?>> listener : tabSelcetedListeners) {
            listener.onTabSelectedChange(infoList.indexOf(nextInfo), selectedInfo, nextInfo);
        }
        this.selectedInfo = nextInfo;
        autoScroll(nextInfo);
    }

    int tabWith;

    /**
     * 自动滚动，实现点击的位置能够自动滚动以展示前后2个
     *
     * @param nextInfo 点击tab的info
     */
    private void autoScroll(TabTopInfo nextInfo) {
        TabTop tabTop = findTab(nextInfo);
        if (tabTop == null) {
            return;
        }
        int index = infoList.indexOf(nextInfo);
        int[] loc = new int[2];
        tabTop.getLocationInWindow(loc);
        int scrollWidth;

        if (tabWith == 0) {
            tabWith = tabTop.getWidth();
        }

        System.out.println("是否点击了左侧或者右侧：" + ((loc[0] + tabWith / 2) > DisplayUtil.INSTANCE.getDisplayHeightInPx(getContext()) / 2));
        //判断点击了屏幕左侧还是右侧
        if ((loc[0] + tabWith / 2) > DisplayUtil.INSTANCE.getDisplayWidthInPx(getContext()) / 2) {
            scrollWidth = rangeScrollWidth(index, 2);
        } else {
            scrollWidth = rangeScrollWidth(index, -2);
        }
        smoothScrollTo(getScrollX() + scrollWidth, 0);
    }

    /**
     * 获取可滚动的范围
     *
     * @param index 从第几个开始
     * @param range 向前向后的范围
     * @return 可滚动的范围
     */
    private int rangeScrollWidth(int index, int range) {
        int scrollWidth = 0;
        for (int i = 0; i <= Math.abs(range); i++) {
            int next;
            if (range < 0) {
                next = range + i + index;
            } else {
                next = range - i + index;
            }
            if (next >= 0 && next < infoList.size()) {
                if (range < 0) {
                    scrollWidth -= scrollWidth(next, false);
                } else {
                    scrollWidth += scrollWidth(next, true);
                }
            }
        }
        return scrollWidth;

    }

    /**
     * 指定位置的控件可滚动的距离
     *
     * @param index   指定位置的控件
     * @param toRight 是否是点击了屏幕右侧
     * @return 可滚动的距离
     */
    private int scrollWidth(int index, boolean toRight) {
        TabTop target = findTab(infoList.get(index));
        if (target == null) return 0;
        Rect rect = new Rect();
        boolean visible = target.getLocalVisibleRect(rect);
        if (!visible) {
            return tabWith;
        } else {
            return tabWith - (rect.right - rect.left);
        }
    }
    
    private LinearLayout getRootLayout(boolean clear) {
        LinearLayout rootView = (LinearLayout) getChildAt(0);
        if (rootView == null) {
            rootView = new LinearLayout(getContext());
            rootView.setOrientation(LinearLayout.HORIZONTAL);
            if(fullScreen){
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                addView(rootView,params);
                setFillViewport(true);
            }else{
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER_VERTICAL;
                addView(rootView,params);
            }
            
        }else if(clear){
            rootView.removeAllViews();
        }

        return rootView;
    }


    @Override
    public void inflateInfo(@NotNull List<? extends TabTopInfo<?>> infoList) {
        if(infoList.isEmpty())
            return;
        this.infoList = infoList;
        LinearLayout linearLayout = getRootLayout(true);
        selectedInfo = null;
        //清除之前添加的HiTabBottom listener tips：java foreach remove的问题
        Iterator<OnTabSelectedListener<TabTopInfo<?>>> iterator = tabSelcetedListeners.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof TabTop) {
                iterator.remove();
            }
        }

        for (int i = 0; i < infoList.size(); i++) {
            final TabTopInfo<?> info = infoList.get(i);
            TabTop tab = new TabTop(getContext());
            tabSelcetedListeners.add(tab);
            tab.setTabInfo(info);
            if (fullScreen) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                params.weight = 1;
                linearLayout.addView(tab, params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                linearLayout.addView(tab,params);
            }
            tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelected(info);
                }
            });
        }
    }
    
}
