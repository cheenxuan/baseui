package org.tech.repos.base.ui.item

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class UiDataItem<Data, VH : RecyclerView.ViewHolder>(val data: Data?=null) {
    var uiAdapter: UiAdapter? = null
    var mData: Data? = null
    fun setAdapter(adapter: UiAdapter) {
        this.uiAdapter = adapter
    }

    init {
        this.mData = data
    }

    /***
     * 绑定数据的方法
     */
    abstract fun onBindData(holder: VH, position: Int)

    /***
     * 返回该item的布局资源id
     */
    open fun getItemLayoutRes(): Int {
        return -1
    }

    /***
     * 返回该item的视图view
     */
    open fun getItemView(parent: ViewGroup): View? {
        return null
    }

    /***
     * 刷新列表
     */
    fun refreshItem() {
        if (uiAdapter != null) {
            uiAdapter!!.refreshItem(this)
        }
    }

    /***
     *  从列表移除
     */
    fun removeItem() {
        if (uiAdapter != null) {
            uiAdapter!!.removeItem(this)
        }
    }

    /***
     * 改item在列表上占据几列
     */
    open fun getSpanSize(): Int {
        return 0
    }

    /**
     * 该item被滑进屏幕
     */
    open fun onViewAttachedToWindow(holder: VH) {

    }

    /**
     * 该item被滑出屏幕
     */
    open fun onViewDetachedFromWindow(holder: VH) {

    }
    
    open fun onCreateViewHolder(parent: ViewGroup): VH? {
        return null
    }

}