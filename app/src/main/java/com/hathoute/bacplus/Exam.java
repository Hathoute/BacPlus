package com.hathoute.bacplus;

import android.content.ContentValues;
import android.content.Context;

import java.io.File;

public class Exam {

    public class Types {
        public static final int NORMAL = 0;
        public static final int RATTRAPAGE = 1;
        public static final int CLASS = 2;
    }

    private final ContentValues cv;
    private final int Subject;

    public Exam(int Subject, ContentValues row) {
        this.Subject = Subject;
        this.cv = row;
    }

    public int getId() {
        return cv.getAsInteger("id");
    }

    public int getExamId() {
        return cv.getAsInteger(BacDataDBHelper.Columns.ExamId);
    }

    public int getYear() {
        return cv.getAsInteger(BacDataDBHelper.Columns.Year);
    }

    public int getType() {
        return cv.getAsInteger(BacDataDBHelper.Columns.Type);
    }

    public String getOptions() {
        return cv.getAsString(BacDataDBHelper.Columns.Options);
    }

    public int getSubject() {
        return this.Subject;
    }

    public String getDirectoryPath(Context context) {
        return "bacplus/" + context.getResources().getStringArray(R.array.subjects_abv)[Subject] +
                "/exams";
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
