package org.tech.repos.base.ui.pickerview.adapter;


import org.tech.repos.base.ui.wheel.adapter.WheelAdapter;

import java.util.List;

/**
 * The simple Array wheel adapter
 *
 * @param <T> the element type
 */
public class ArrayWheelAdapter<T> implements WheelAdapter {


    // items
    private List<T> items;

    /**
     * Constructor
     *
     * @param items the items
     */
    public ArrayWheelAdapter(List<T> items) {
        this.items = items;

    }

    @Override
    public Object getItem(int index) {
        if (items != null && items.size() > 0 && index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return "";
    }

    @Override
    public int getItemsCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public int indexOf(Object o) {
        return items == null ? -1 : items.indexOf(o);
    }

}
