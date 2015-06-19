package com.michaelcrivello.apps.snaphunt.adapter;

import android.widget.BaseAdapter;

import com.michaelcrivello.apps.snaphunt.misc.Selectable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by tao on 6/18/15.
 */
public interface SelectableAdapter  {
    abstract List<?> getSelectedItems();
    abstract Selectable getSelectable(String key);
    abstract void setSelected(Selectable item, boolean selectedState);
}
