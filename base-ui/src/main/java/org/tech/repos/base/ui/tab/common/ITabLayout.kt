package org.tech.repos.base.ui.tab.common

import android.view.ViewGroup

/**
 * Author: xuan
 * Created on 2021/7/9 21:39.
 *
 * Describe:
 */
interface ITabLayout<Tab : ViewGroup, D> {

    fun findTab(data: D): Tab?

    fun addTabSelectedChangeListener(listener: OnTabSelectedListener<D>)

    fun defaultSelect(defaultInfo: D)

    fun inflateInfo(infoList: List<D>)

    open interface OnTabSelectedListener<D> {
        fun onTabSelectedChange(index: Int, preInfo: D?, nextInfo: D)
    }


}