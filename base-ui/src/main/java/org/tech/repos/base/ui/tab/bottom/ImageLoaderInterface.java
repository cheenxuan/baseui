package org.tech.repos.base.ui.tab.bottom;

import android.content.Context;
import android.view.View;

import java.io.Serializable;

/**
 * Author: xuan
 * Created on 2/22/22 8:23 PM.
 * <p>
 * Describe:
 */
public interface ImageLoaderInterface<T extends View> extends Serializable {

    void displayImage(Context context, Object path, T imageView);
}