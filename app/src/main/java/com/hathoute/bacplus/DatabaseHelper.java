package com.hathoute.bacplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class DatabaseHelper {

    private static final String TAG = "lols";

    private static final String DATABASE_NAME = "bacdata";
    private static final int DATABASE_VERSION = 2;

    private final Context context;
    private boolean createDb = false, upgradeDb = false;

    private final SQLiteOpenHelper _openHelper;
    private final String[] subjects;

    public class Columns {
        public static final String Title = "TITLE";
        public static final String Year = "YEAR";
        public static final String Options = "OPTIONS";
        public static final String Link = "LINK";
    }

    public DatabaseHelper(Context context) {
        this.context = context;
        _openHelper = new SimpleSQLiteOpenHelper(context);
        subjects = context.getResources().getStringArray(R.array.subjects_abv);
    }

    private void copyDatabaseFromAssets(SQLiteDatabase db) {
        Log.i(TAG, "copyDatabase");
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            // Open db packaged as asset as the input stream
            myInput = context.getAssets().open(DATABASE_NAME + ".db");

            // Open the db in the application package context:
            myOutput = new FileOutputStream(db.getPath());

            File file = new File(db.getPath(), DATABASE_NAME + ".db");
            if(file.exists()) {
                if(file.delete())
                    Log.i(TAG, "File Deleted!");
                else
                    Log.e(TAG, "File exists, but not deleted!");
            }

            // Transfer db file contents:
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();

            // Set the version of the copied database to the current
            // version:
            SQLiteDatabase copiedDb = context.openOrCreateDatabase(
                    DATABASE_NAME, 0, null);
            copiedDb.execSQL("PRAGMA user_version = " + DATABASE_VERSION);
            copiedDb.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new Error(TAG + " Error copying database");
        } finally {
            // Close the streams
            try {
                if (myOutput != null) {
                    myOutput.close();
                }
                if (myInput != null) {
                    myInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error(TAG + " Error closing streams");
            }
        }
    }
    class SimpleSQLiteOpenHelper extends SQLiteOpenHelper {
        SimpleSQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createDb = true;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            upgradeDb = true;
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            Log.i(TAG, "onOpen db");
            if (createDb) {// The db in the application package
                // context is being created.
                // So copy the contents from the db
                // file packaged in the assets
                // folder:
                createDb = false;
                copyDatabaseFromAssets(db);

            }
            if (upgradeDb) {// The db in the application package
                // context is being upgraded from a lower to a higher version.
                upgradeDb = false;
                copyDatabaseFromAssets(db);
            }
        }
    }

    public Cursor getLessons(int Subject) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String sqlQuery = "select id from " + formatLessonTable(subjects[Subject]) + " order by id";
        return db.rawQuery(sqlQuery, null);
    }

    public Cursor getLessons(int Subject, int Year) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String sqlQuery = "select id from " + formatLessonTable(subjects[Subject]) + " where " + Columns.Year + " = ? order by id";
        return db.rawQuery(sqlQuery, new String[] {String.valueOf(Year)});
    }

    public Cursor getLessons(int Subject, int Year, int Option) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String[] Options = context.getResources().getStringArray(Year == MainActivity.YEAR_FIRST ?
                        R.array.options_firstyear_helper : R.array.options_secondyear_helper);
        String sqlQuery = "select id from " + formatLessonTable(subjects[Subject]) + " where " + Columns.Year + " = ? " +
                "and " + Columns.Options + " like '%$%' order by id";
        return db.rawQuery(sqlQuery.replace("$", Options[Option]), new String[] {String.valueOf(Year)});
    }

    public Lesson getLesson(int Subject, int id) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        //Todo: Configure returned null in other methods.
        if(db == null) {
            return null;
        }
        Cursor cur = db.rawQuery("select * from " + formatLessonTable(subjects[Subject]) +
                " where id = ?", new String[] {String.valueOf(id)});
        ContentValues cv = new ContentValues();

        Lesson lesson;
        cur.moveToFirst();
        try {
            cv.put("id", cur.getInt(0));
            cv.put(Columns.Title, cur.getString(1));
            cv.put(Columns.Year, cur.getInt(2));
            cv.put(Columns.Options, cur.getString(3));
            cv.put(Columns.Link, cur.getString(4));
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        lesson = new Lesson(Subject, cv);
        cur.close();
        db.close();
        return lesson;
    }

    private static String formatLessonTable(String subject) {
        //Todo: Configure multilanguage subjects;
        return "lessons_" + subject + "_arab";
    }
}