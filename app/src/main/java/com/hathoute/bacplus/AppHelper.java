package com.hathoute.bacplus;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppHelper {

    public static String hostURL = "http://80.211.97.124/";

    public static class Storage {
        static final int None = 0;
        static final int Data = 1;
        static final int Cache = 2;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    public static int getSubjectIDbyAbv(Context context, String subject) {
        String[] subjects = context.getResources().getStringArray(R.array.subjects_abv);
        int index = -1;
        for (int i = 0; i < subjects.length; i++) {
            if (subjects[i].equals(subject)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static String getYearStrbyId(int Year) {
        return App.getContext().getResources().getString(Year == MainActivity.YEAR_FIRST ?
                R.string.years_first : R.string.years_second);
    }

    public static int getYearResbyId(int Year) {
        return Year == MainActivity.YEAR_FIRST ? R.string.years_first : R.string.years_second;
    }

    public static boolean cleanAll(Context context) {
        boolean result = false;
        try {
            FileUtils.deleteDirectory(context.getFilesDir());
            result = true;
        } catch(Exception ignored) {
        }

        return result;
    }
}
