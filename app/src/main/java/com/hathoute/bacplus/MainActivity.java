package com.hathoute.bacplus;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.hathoute.bacplus.BacDataDBHelper.DATABASE_NAME;
import static com.hathoute.bacplus.BacDataDBHelper.DATABASE_VERSION;

public class MainActivity extends AppCompatActivity implements YearFragment.OnCallbackReceived,
        OptionFragment.OnCallbackReceived {

    //Todo: getTotalSpace,getFreeSpace and getUsableSpace to check if user has location to download PDF.
    final static int YEAR_FIRST = 0;
    final static int YEAR_SECOND = 1;

    int iChosenYear;
    int iChosenOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getBaseContext(), OnAppDestroyedService.class));
        new BacDataDBHelper(this).readifyDB();
        Intent intent = new Intent(this, OfflineDocsActivity.class);
        startActivity(intent);
        //AppHelper.cleanAll(this);
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


    //Todo: Find a better way to compare database files.
    /*public static void checkDatabase(Context context) {
        InputStream db_asset = null;
        try {
            db_asset = context.getAssets().open(DATABASE_NAME + ".db");
            File db_app = context.getDatabasePath(DATABASE_NAME);
            System.out.println("AAEZA: " + db_app.length() + "  " + db_asset.available());
            if (db_app.length() != db_asset.available()) {
                copyDatabaseFromAssets(context);
            }
        } catch(Exception ignored) {
            copyDatabaseFromAssets(context);
        } finally {
            if(db_asset != null)
                try {
                    db_asset.close();
                } catch(IOException ignored){
                }
        }
    }*/
}
