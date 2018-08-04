package com.hathoute.bacplus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int noOfTabs;
    private int Year;
    private int Option;
    private int Subject;

    public PagerAdapter(FragmentManager fm, int noOfTabs, int Year, int Option, int Subject) {
        super(fm);
        this.noOfTabs = noOfTabs;
        this.Year = Year;
        this.Option = Option;
        this.Subject = Subject;
    }

    @Override
    public Fragment getItem(int position) {
        LessonsFragment lessonsFragment = new LessonsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("year", Year);
        bundle.putInt("option", Option);
        bundle.putInt("subject", Subject);
        lessonsFragment.setArguments(bundle);
        return lessonsFragment;
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
