package com.michaelcrivello.apps.snaphunt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.SnaphuntApp;
import com.michaelcrivello.apps.snaphunt.adapter.HomePagerAdapter;
import com.michaelcrivello.apps.snaphunt.ui.fragments.GameList;
import com.michaelcrivello.apps.snaphunt.ui.fragments.InviteList;
import com.michaelcrivello.apps.snaphunt.view.SlidingTabLayout;

/**
 * Created by michael on 5/19/15.
 */
public class HomeActivity extends BaseActivity {
    private Toolbar toolbar;
    private ViewPager homeViewPager;
    private HomePagerAdapter homePagerAdapter;
    private SlidingTabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        initToolbar();

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        homePagerAdapter =  new HomePagerAdapter(getSupportFragmentManager(), new GameList(), new InviteList());

        // Assigning ViewPager View and setting the adapter
        homeViewPager = (ViewPager) findViewById(R.id.home_view_pager);
        homeViewPager.setAdapter(homePagerAdapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(homeViewPager);

    }

    private void initToolbar() {
        // Toolbar
        String userHome = userManager.getUser().getUsername();
        userHome.concat(" - " + getString(R.string.home));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(userHome);
        toolbar.inflateMenu(R.menu.home_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.action_logout:
                        logout();
                        return true;
                    case R.id.action_new_game:
                        startNewGame();
                        return true;
                }

                return false;
            }
        });
    }

    private void startNewGame() {
        startActivity(new Intent(this, GameCreationActivity.class));
        overridePendingTransition(0,0);
    }
}
