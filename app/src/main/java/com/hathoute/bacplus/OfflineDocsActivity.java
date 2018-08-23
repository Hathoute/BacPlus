package com.hathoute.bacplus;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

public class OfflineDocsActivity extends AppCompatActivity {

    private DrawerLayout dlOffline;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_docs);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR){
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

        Toolbar toolbar = findViewById(R.id.tbOffline);
        setSupportActionBar(toolbar);

        dlOffline = findViewById(R.id.dlOffline);

        drawerToggle = new ActionBarDrawerToggle(this, dlOffline, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlOffline.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        populateNavView();
    }

    private void populateNavView() {
        //Prepare NavigationView items
        NavigationView nvOffline = findViewById(R.id.nvOffline);
        Menu menu = nvOffline.getMenu();
        SubMenu submenu = menu.getItem(2).getSubMenu();
        submenu.clear();

        //Todo: Add items to Navigation View.
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }
}
