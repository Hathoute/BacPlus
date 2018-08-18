package com.hathoute.bacplus;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
        startService(new Intent(getBaseContext(), OnAppDestroyedService.class));
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
        Intent intent = new Intent(getBaseContext(), SubjectsActivity.class);
        intent.putExtra("year", iChosenYear)
                .putExtra("option", iChosenOption);
        startActivity(intent);
    }
}
