package org.tech.repos.base.ui.tab.bottom

import android.graphics.Bitmap
import org.tech.repos.base.ui.tab.common.TabInfo

/**
 * Author: xuan
 * Created on 2021/7/9 22:01.
 *
 * Describe:
 */
class TabBottomInfo<Color> :TabInfo{
    enum class TabType {
        BITMAP, ICON
    }

    /**
     * @param name title
     * @param defaultBitmap 默认显示
     * @param selectedBitmap 选择后显示
     */
    constructor(
        name: String,
        defaultBitmap: Bitmap, 
        selectedBitmap: Bitmap, 
        defaultColor: Color,
        tintColor: Color
    ) {
        this.name = name
        this.defaultBitmap = defaultBitmap
        this.selectedBitmap = selectedBitmap
        this.defaultColor = defaultColor
        this.tintColor = tintColor
        this.tabType = TabType.BITMAP
    }

    constructor(
        name: String,
        iconFont: String,
        defaultIconName: String,
        selectedIconName: String,
        defaultColor: Color,
        tintColor: Color
    ) {
        this.name = name
        this.iconFont = iconFont
        this.defaultIconName = defaultIconName
        this.selectedIconName = selectedIconName
        this.defaultColor = defaultColor
        this.tintColor = tintColor
        this.tabType = TabType.ICON
    }

    var name: String
    var tabType: TabType
    var defaultBitmap: Bitmap? = null
    var selectedBitmap: Bitmap? = null
    var iconFont: String? = null
    var defaultIconName: String? = null
    var selectedIconName: String? = null
    var defaultColor: Color? = null
    var tintColor: Color? = null


}