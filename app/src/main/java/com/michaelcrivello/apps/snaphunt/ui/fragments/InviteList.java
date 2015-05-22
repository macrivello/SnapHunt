package com.michaelcrivello.apps.snaphunt.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michaelcrivello.apps.snaphunt.R;

import javax.annotation.Nullable;

import roboguice.fragment.RoboFragment;

/**
 * Created by michael on 5/20/15.
 */
public class InviteList extends BaseFragment {
    protected static final String TITLE = "Invites";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.invites_list,container,false);
        return v;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
