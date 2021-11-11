package org.tech.repos.base.ui.tab.top;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tech.repos.base.ui.tab.common.ITab;

import org.tech.repos.base.ui.R;


/**
 * Author: xuan
 * Created on 2021/3/22 14:33.
 * <p>
 * Describe:
 */
public class TabTop extends RelativeLayout implements ITab<TabTopInfo<?>> {

    private TabTopInfo<?> tabInfo;
    private ImageView tabImageView;
    private TextView tabNameView;
    private View indicator;

    public TabTop(Context context) {
        this(context,null);
    }

    public TabTop(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TabTop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.tab_top,this);
        tabImageView = findViewById(R.id.iv_image);
        tabNameView = findViewById(R.id.tv_trans_type);
        indicator = findViewById(R.id.tab_top_indicator);
    }

    @Override
    public void setTabInfo(@NonNull TabTopInfo<?> data) {
        this.tabInfo = data;
        inflateInfo(false,true);
    }

    private void inflateInfo(boolean selected, boolean init) {
        if (tabInfo.tabType == TabTopInfo.TabType.TEXT) {
            if (init) {
                tabImageView.setVisibility(View.GONE);
                tabNameView.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(tabInfo.name)) {
                    tabNameView.setText(tabInfo.name);
                }
            }

            if (selected) {
                indicator.setVisibility(View.VISIBLE);
                tabNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP,tabInfo.selectedSize);
                tabNameView.setTextColor(getTextColor(tabInfo.tintColor));
            } else {
                indicator.setVisibility(View.GONE);
                tabNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP,tabInfo.defaultSize);
                tabNameView.setTextColor(getTextColor(tabInfo.defaultColor));
            }
        } else if(tabInfo.tabType == TabTopInfo.TabType.BITMAP){
            if (init) {
                tabNameView.setVisibility(View.GONE);
                tabImageView.setVisibility(View.VISIBLE);
            }

            if (selected) {
                tabImageView.setImageBitmap(tabInfo.selectedBitmap);
            } else {
                tabImageView.setImageBitmap(tabInfo.defaultBitmap);
            }
        }  
    }

    public TabTopInfo<?> getTabInfo() {
        return tabInfo;
    }

    public ImageView getTabImageView() {
        return tabImageView;
    }

    public TextView getTabNameView() {
        return tabNameView;
    }

    @Override
    public void resetHeight(int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        getTabNameView().setVisibility(GONE);
    }

    @Override
    public void onTabSelectedChange(int index, @Nullable TabTopInfo<?> preInfo, @NonNull TabTopInfo<?> nextInfo) {
        if (preInfo != tabInfo && nextInfo != tabInfo || preInfo == nextInfo) {
            return;
        }

        if (preInfo == tabInfo) {
            inflateInfo(false,false);
        }else{
            inflateInfo(true,false);
        }
    }

    @ColorInt
    private int getTextColor(Object color) {
        if (color instanceof String) {
            return Color.parseColor((String) color);
        }else{
            return (int) color;
        }
    }

    @Override
    public void resetWidth(int width) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        setLayoutParams(layoutParams);
        getTabNameView().setVisibility(GONE);
    }
}
