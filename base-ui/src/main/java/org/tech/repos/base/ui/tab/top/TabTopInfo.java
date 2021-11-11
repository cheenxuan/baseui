package org.tech.repos.base.ui.tab.top;

import android.graphics.Bitmap;

import org.tech.repos.base.ui.tab.common.TabInfo;

/**
 * Author: xuan
 * Created on 2021/3/22 13:56.
 * <p>
 * Describe:
 */
public class TabTopInfo<Color> extends TabInfo {
    public enum TabType{
        BITMAP,TEXT
    }
    
    public String name;
    public Bitmap defaultBitmap;
    public Bitmap selectedBitmap;
    public Color defaultColor;
    public Color tintColor;
    public TabType tabType;
    public float defaultSize = 15;
    public float selectedSize = 15;

    public TabTopInfo(String name, Bitmap defaultBitmap, Bitmap selectedBitmap) {
        this.name = name;
        this.defaultBitmap = defaultBitmap;
        this.selectedBitmap = selectedBitmap;
        this.tabType = TabType.BITMAP;
    }

    public TabTopInfo(String name, Color defaultColor, Color tintColor) {
        this.name = name;
        this.defaultColor = defaultColor;
        this.tintColor = tintColor;
        this.tabType = TabType.TEXT;
    }
}
