package org.tech.repos.base.ui.tab.bottom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import org.tech.repos.base.lib.utils.DisplayUtil
import org.tech.repos.base.lib.utils.ViewUtil
import org.tech.repos.base.ui.R
import org.tech.repos.base.ui.tab.common.ITabLayout

/**
 * Author: xuan
 * Created on 2021/7/9 22:56.
 *
 * Describe:
 */
class TabBottomLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ITabLayout<TabBottom, TabBottomInfo<*>> {

    companion object {
        const val TAG_TAB_BOTTOM = "TAG_TAB_BOTTOM"
    }

    private var bottomAlpha = 1f
    private var bottomLineHeight = 0.5f
    private var tabBottomHeight = 49f
    private var bottomLineColor = "#dfe0e1"
    private var selectedInfo: TabBottomInfo<*>? = null
    private var infoList: List<TabBottomInfo<*>>? = null
    private val tabSelectedListeners =
        mutableListOf<ITabLayout.OnTabSelectedListener<TabBottomInfo<*>>>()
    private var imageLoader: ImageLoaderInterface<View>? = null

    override fun findTab(data: TabBottomInfo<*>): TabBottom? {
        val ll = findViewWithTag<ViewGroup>(TAG_TAB_BOTTOM)
        for (child: View in ll.children) {
            if (child is TabBottom) {
                if (child.getTabInfo() == data) {
                    return child
                }
            }
        }
        return null
    }

    override fun addTabSelectedChangeListener(listener: ITabLayout.OnTabSelectedListener<TabBottomInfo<*>>) {
        tabSelectedListeners.add(listener)
    }

    override fun defaultSelect(defaultInfo: TabBottomInfo<*>) {
        onSelected(defaultInfo)
    }

    override fun inflateInfo(infoList: List<TabBottomInfo<*>>) {
        if (infoList.isEmpty()) {
            return
        }

        this.infoList = infoList
        //移除之前已经添加的view
        val size = childCount - 1
        for (i in size downTo 1) {
            removeViewAt(i)
        }

        selectedInfo = null
        setBackGround()
        //清楚之前添加TabBottom listener tips：java foreach remove的问题
        val iterator: MutableIterator<ITabLayout.OnTabSelectedListener<TabBottomInfo<*>>> =
            tabSelectedListeners.iterator()
        while (iterator.hasNext()) {
            if (iterator.next() is TabBottom) {
                iterator.remove()
            }
        }
        val height = DisplayUtil.dp2px(tabBottomHeight, resources)
        val fl = FrameLayout(context)
        fl.tag = TAG_TAB_BOTTOM
        val width = DisplayUtil.getDisplayWidthInPx(context) / infoList.size
        for ((index, value) in infoList.withIndex()) {
            val info = infoList.get(index)
            //tips: 为何不用LinearLayout 当动态改变child大小后gravity.bottom会失效
            val params = LayoutParams(width, height)
            params.gravity = Gravity.BOTTOM
            params.leftMargin = index * width


            val tabBottom = TabBottom(context)
            imageLoader?.let { tabBottom.setImageLoader(it) }
            tabSelectedListeners.add(tabBottom)
            tabBottom.setTabInfo(info)
            fl.addView(tabBottom, params)
            tabBottom.setOnClickListener {
                onSelected(info)
            }

        }
        val flParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        flParams.gravity = Gravity.BOTTOM
        addBottomLine()
        addView(fl, flParams)
        fixContentView()
    }

    private fun addBottomLine() {
        val bottomLine = View(context)
        bottomLine.setBackgroundColor(Color.parseColor(bottomLineColor))
        val params =
            LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dp2px(bottomLineHeight, resources))
        params.gravity = Gravity.BOTTOM
        params.bottomMargin = DisplayUtil.dp2px(tabBottomHeight - bottomLineHeight, resources)
        addView(bottomLine, params)
        bottomLine.alpha = bottomAlpha
    }

    private fun onSelected(nextinfo: TabBottomInfo<*>) {
        for (listener in tabSelectedListeners) {
            listener.onTabSelectedChange(infoList!!.indexOf(nextinfo), selectedInfo, nextinfo)
        }
        this.selectedInfo = nextinfo
    }

    private fun setBackGround() {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_layout_bg, null)
        val params =
            LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dp2px(tabBottomHeight, resources))
        params.gravity = Gravity.BOTTOM
        addView(view, params)
        view.alpha = bottomAlpha
    }

    fun setTabAlpha(alpha: Float) {
        this.bottomAlpha = alpha
    }

    fun setTabHeight(tabHeight: Float) {
        this.tabBottomHeight = tabBottomHeight
    }

    fun setTabLineColor(tabLineColor: String) {
        this.bottomLineColor = tabLineColor
    }

    fun setImageLoader(imageLoader: ImageLoaderInterface<View>) {
        this.imageLoader = imageLoader
    }

    private fun fixContentView() {
        if (getChildAt(0) !is ViewGroup) {
            return
        }

        val rootView: ViewGroup = getChildAt(0) as ViewGroup
        var targetView: ViewGroup? = ViewUtil.findTypeView(rootView, RecyclerView::class.java)

        if (targetView == null) {
            targetView = ViewUtil.findTypeView(rootView, ScrollView::class.java)
        }

        if (targetView == null) {
            targetView = ViewUtil.findTypeView(rootView, AbsListView::class.java)
        }

        if (targetView != null && (targetView is RecyclerView || targetView is ScrollView || targetView is AbsListView)) {
            targetView.setPadding(0, 0, 0, DisplayUtil.dp2px(tabBottomHeight, resources))
            targetView.clipToPadding = false
        }
    }

    fun clipBottomPadding(targetView: ViewGroup?) {
        if (targetView != null) {
            targetView.setPadding(0, 0, 0, DisplayUtil.dp2px(tabBottomHeight, resources))
            targetView.clipToPadding = false
        }
    }

}