package com.hathoute.bacplus;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Subject {
    public static int MATH = 0;
    public static int ARABIC = 1;
    public static int PHYSICS = 2;
    public static int SVT = 3;
    public static int FRENCH = 4;
    public static int ENGLISH = 5;
    public static int PHILOSOPHY = 6;
    public static int ISLAMIC = 7;
    public static int GEOGRAPHY = 8;
    public static int ENGINEERING = 9;

    // Conserving the same order as in strings.xml
    private Integer[] subjectsIcons = {
            R.drawable.icon_math,
            R.drawable.icon_arabic,
            R.drawable.icon_pc,
            R.drawable.icon_svt,
            R.drawable.icon_french,
            R.drawable.icon_english,
            R.drawable.icon_philosophy,
            R.drawable.icon_islamiceduc,
            R.drawable.icon_geography,
            R.drawable.icon_gear
    };

    private Context mContext;
    private int Year;
    private int Option;
    private int Subject;
    private String SubjectAbv;

    public Subject(Context Context, int Year, int Option, int Subject) {
        this.mContext = Context;
        this.Year = Year;
        this.Option = Option;
        this.Subject = Subject;
        this.SubjectAbv = mContext.getResources().getStringArray(R.array.subjects_abv)[Subject];
    }

    public String getTitle() {
        return mContext.getResources().getStringArray(R.array.subjects)[Subject];
    }

    public int getDrawableId() {
        return this.subjectsIcons[Subject];
    }

    public List<Lesson> getLessons() {
        List<Lesson> lessons = new ArrayList<>();

        String lessonsTokensName = "lessons_" + getYearStr() + "_" + SubjectAbv + "_tokens";
        String[] lessonsTokens = mContext.getResources()
                .getStringArray(mContext.getResources()
                        .getIdentifier(lessonsTokensName, "array",
                                mContext.getPackageName()));
        for(int i = 0; i < lessonsTokens.length; i++) {
            List<String> lessonTokens = Arrays.asList(lessonsTokens[i].split(";"));
            if(lessonTokens.contains(mContext.getResources()
                    .getStringArray(Year == MainActivity.YEAR_FIRST ? R.array.options_firstyear_helper
                            : R.array.options_secondyear_helper)[Option])) {
                lessons.add(new Lesson(mContext, Year, Subject, i));
            }
        }
        return lessons;
    }

    public List<Exam> getExams() {
        List<Exam> exams = new ArrayList<>();

        String examsName = "exams_" + getYearStr() + "_" + SubjectAbv;
        String[] examsTokens =  mContext.getResources()
                .getStringArray(mContext.getResources()
                        .getIdentifier(examsName + "_tokens", "array",
                                mContext.getPackageName()));

        for(int i = 0; i < examsTokens.length; i++) {
            List<String> tokens = Arrays.asList(examsTokens[i].split(";"));
            if(!tokens.contains(mContext.getResources()
                    .getStringArray(Year == MainActivity.YEAR_FIRST ?
                            R.array.options_firstyear_helper :
                            R.array.options_secondyear_helper)[Option]))
                continue;

            exams.add(new Exam(mContext, Year, Subject, i));
        }
        return exams;
    }

    public List<Video> getVideos() {
        List<Video> videos = new ArrayList<>();

        String videoName = "videos_" + getYearStr() + "_" + SubjectAbv;
        String[] videoTokens = mContext.getResources()
                .getStringArray(mContext.getResources()
                        .getIdentifier(videoName + "_tokens", "array",
                                mContext.getPackageName()));

        for(int i = videoTokens.length - 1; i >= 0; i--) {
            List<String> tokens = Arrays.asList(videoTokens[i].split(";"));
            if(!tokens.contains(mContext.getResources()
                    .getStringArray(Year == MainActivity.YEAR_FIRST ?
                            R.array.options_firstyear_helper :
                            R.array.options_secondyear_helper)[Option]))
                continue;

            videos.add(new Video(mContext, Year, Subject, i));
        }
        return videos;
    }

    public int getYear() {
        return this.Year;
    }

    public int getOption() {
        return this.Option;
    }

    public int getSubject() {
        return this.Subject;
    }

    private String getYearStr() {
        return Year == MainActivity.YEAR_FIRST ? "1st" : "2nd";
    }
}
