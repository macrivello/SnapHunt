package com.michaelcrivello.apps.snaphunt.view;

import android.content.Context;
import android.widget.Button;

import com.michaelcrivello.apps.snaphunt.data.model.Theme;

/**
 * Created by michael on 5/21/15.
 */
public class ThemeButton extends Button {
    private Theme theme;

    public ThemeButton(Context context, Theme theme) {
        super(context);
        this.theme = theme;
    }

    public ThemeButton(Context context) {
        this(context, null);
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
