package com.michaelcrivello.apps.snaphunt.event;

/**
 * Created by tao on 7/5/15.
 */
public class AutoRefresh {
    boolean autoRefresh;
    public AutoRefresh(boolean b) {
        this.autoRefresh = b;
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }
}

