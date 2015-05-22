package com.michaelcrivello.apps.snaphunt.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.michaelcrivello.apps.snaphunt.ui.fragments.BaseFragment;
import com.michaelcrivello.apps.snaphunt.ui.fragments.GameList;
import com.michaelcrivello.apps.snaphunt.ui.fragments.InviteList;

import java.util.ArrayList;

/**
 * Created by michael on 5/19/15.
 */
public class HomePagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<BaseFragment> gamePages;
        GameList gameList;
        InviteList inviteList;

        // Build a Constructor and assign the passed Values to appropriate values in the class
        public HomePagerAdapter(FragmentManager fm, BaseFragment... fragments) {
            super(fm);
            gamePages = new ArrayList<>();
            for (BaseFragment fragment : fragments) {
                gamePages.add(fragment);
            }
        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {
            return gamePages.get(position);
        }

        // This method return the titles for the Tabs in the Tab Strip

        @Override
        public CharSequence getPageTitle(int position) {
            return gamePages.get(position).getTitle();
        }

        // This method return the Number of tabs for the tabs Strip

        @Override
        public int getCount() {
            return gamePages.size();
        }
}
