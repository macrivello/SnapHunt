package com.michaelcrivello.apps.snaphunt.misc;

/**
 * Created by tao on 6/18/15.
 */
public class Selectable extends AbstractSelectable implements ISelectable{
    public Selectable(Object object) {
        this(object, false);
    }
    public Selectable(Object object, boolean selected){
        this.object = object;
        selected = false;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public Object getObject() {
        return object;
    }
}
