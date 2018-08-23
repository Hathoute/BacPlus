package com.hathoute.bacplus;

import android.content.ContentValues;
import android.content.Context;

import java.io.File;

public class Lesson {

    private ContentValues contentValues;
    private int Subject;

    Lesson(int Subject, ContentValues row) {
        this.Subject = Subject;
        this.contentValues = row;
    }

    public String getName() {
        return contentValues.getAsString(BacDataDBHelper.Columns.Title);
    }

    public int getYear() {
        return contentValues.getAsInteger(BacDataDBHelper.Columns.Year);
    }

    public int getSubject() {
        return this.Subject;
    }

    public int getId() {
        return contentValues.getAsInteger("id");
    }

    public String getOptions() {
        return contentValues.getAsString(BacDataDBHelper.Columns.Options);
    }

    public String getDirectoryPath(Context context) {
        return "bacplus/" + context.getResources().getStringArray(R.array.subjects_abv)[Subject] +
                "/lessons";
    }

    public int isAvailable(Context context) {
        File file = new File(getDirectoryPath(context), getId()+".pdf");
        File cacheFile = new File(context.getCacheDir(), file.toString());
        File dataFile = new File(context.getFilesDir(), file.toString());
        if(dataFile.exists())
            return AppHelper.Storage.Data;
        else if(cacheFile.exists())
            return AppHelper.Storage.Cache;
        else
            return AppHelper.Storage.None;
    }
}
