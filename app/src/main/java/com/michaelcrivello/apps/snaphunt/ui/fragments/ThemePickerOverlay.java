package com.michaelcrivello.apps.snaphunt.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.model.Theme;
import com.michaelcrivello.apps.snaphunt.view.ThemeButton;

import java.util.ArrayList;
import java.util.List;

import roboguice.fragment.RoboFragment;

/**
 * Fragment meant to overlay the game_activity activity for the judge to select a phrase to begin a new round.
 */
public class ThemePickerOverlay extends RoboFragment {
    ThemeSelection themeSelection;
    ArrayList<Theme> themes;
    ArrayList<Button> themeButtons;
    LinearLayout themeSelectorButtonContainer;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.theme_picker_overlay,container,false);
        themeSelectorButtonContainer = (LinearLayout) v.findViewById(R.id.themeSelectorButtonContainer);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;

        if (activity instanceof ThemeSelection) {
            themeSelection = (ThemeSelection) activity;
        }
    }

    public boolean addThemes(ArrayList<Theme> themes){
        // Add theme button to layout
        this.themes = themes;
        for (Theme theme : themes) {
            Button btn = new ThemeButton(context, theme);
            btn.setText(theme.getPhrase());
            btn.setBackgroundColor(getResources().getColor(android.R.color.black));
            btn.setTextColor(getResources().getColor(android.R.color.white));

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: Make sure no other themes can get selected. Disable clicks or something
                    themeSelection.themeSelected(((ThemeButton)view).getTheme());
                }
            });
        }

        return false;
    }

}
