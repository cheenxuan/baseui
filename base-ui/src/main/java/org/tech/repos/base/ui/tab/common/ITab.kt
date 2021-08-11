package org.tech.repos.base.ui.tab.common

import androidx.annotation.Px

/**
 * Author: xuan
 * Created on 2021/7/9 21:38.
 *
 * Describe: 对外接口
 */
interface ITab<D> :ITabLayout.OnTabSelectedListener<D>{

    fun setTabInfo(data: D)

    /**
     * 动态修改某个item的大小
     * @param height item的高度
     */
    fun resetHeight(@Px height: Int)


    /**
     * 动态修改某个item的大小
     * @param width item的宽度
     */
    fun resetWidth(@Px width: Int)

}