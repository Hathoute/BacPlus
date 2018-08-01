package com.hathoute.bacplus;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity implements YearFragment.OnCallbackReceived,
        OptionFragment.OnCallbackReceived {

    final static int YEAR_FIRST = 0;
    final static int YEAR_SECOND = 1;

    int iChosenYear;
    int iChosenOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showYears();
    }

    public void showYears() {
        Fragment fragment = new YearFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, "YEAR")
                .commit();
    }

    @Override
    public void onYearChoice(int Year) {
        iChosenYear = Year;
        showOptions();
    }

    public void showOptions() {
        Fragment fragment = new OptionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("year", iChosenYear);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, "OPTION")
                .commit();
    }

    @Override
    public void onOptionChoice(int position) {
        iChosenOption = position;
        LessonsHelper lessonsHelper = new LessonsHelper(this);
        List<String> list = lessonsHelper.formatSubjects(iChosenYear, iChosenOption);
        for(String item : list) {
            System.out.println(item);
        }
    }
}
