package com.hathoute.bacplus;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import java.util.List;

public class TabbedSubjectsActivity extends AppCompatActivity {

    TabLayout tabLayout;
    private int iChosenYear;
    private int iChosenOption;
    private int iChosenSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_subjects);

        iChosenYear = getIntent().getIntExtra("year", 0);
        iChosenOption = getIntent().getIntExtra("option", 0);
        iChosenSubject = getIntent().getIntExtra("subject", 0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        configureTabs();
    }

    void configureTabs() {
        tabLayout = findViewById(R.id.tab_layout);
        List<Subject> subjects = new LessonsHelper(this)
                .formatSubjects(iChosenYear, iChosenOption);
        for(Subject subject : subjects) {
            tabLayout.addTab(tabLayout.newTab().setText(subject.getTitle()));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount(), iChosenYear, iChosenOption, iChosenSubject);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setCurrentItem(iChosenSubject);
    }
}
