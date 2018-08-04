package com.hathoute.bacplus;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

public class Lesson {

    private Context mContext;
    private int Year;
    private int Subject;
    private int Lesson;
    private String[] SubjectAbv;
    private String[] LessonsArray;

    public Lesson(Context context, int Year, int Subject, int Lesson) {
        this.mContext = context;
        this.Year = Year;
        this.Subject = Subject;
        this.Lesson = Lesson;
        this.SubjectAbv = mContext.getResources().getStringArray(R.array.subjects_abv);
        String lessonsArrayName = "lessons_" + getYearStr() + "_" + SubjectAbv[Subject];
        LessonsArray = mContext.getResources()
                .getStringArray(mContext.getResources()
                        .getIdentifier(lessonsArrayName, "array",
                                mContext.getPackageName()));
    }

    public String getName() {
        return LessonsArray[Lesson];
    }

    public int getYear() {
        return this.Year;
    }

    public int getSubject() {
        return this.Subject;
    }

    public boolean isAvailable(int Option) {
        String lessonsTokensName = "lessons_" + getYearStr() + "_" + SubjectAbv[Subject] + "_tokens";
        List<String> lessonTokens = Arrays.asList(mContext.getResources()
                .getStringArray(mContext.getResources()
                        .getIdentifier(lessonsTokensName, "array",
                                mContext.getPackageName()))[Option].split(";"));

        return lessonTokens.contains(mContext.getResources()
                .getStringArray(Year == MainActivity.YEAR_FIRST ? R.array.options_firstyear_helper
                        : R.array.options_secondyear_helper)[Option]);
    }

    private String getYearStr() {
        return Year == MainActivity.YEAR_FIRST ? "1st" : "2nd";
    }
}
