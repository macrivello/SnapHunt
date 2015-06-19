package com.michaelcrivello.apps.snaphunt.misc;

/**
 * Created by tao on 6/18/15.
 */
public abstract class AbstractSelectable {
    boolean selected;
    Object object;

    abstract void setSelected(boolean selected);
}
