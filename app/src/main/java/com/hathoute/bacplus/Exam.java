package com.hathoute.bacplus;

import android.content.Context;

public class Exam {

    public static int NATIONAL = 0;
    public static int REGIONAL = 1;
    public static int CONTINU = 2;

    private Context mContext;
    private int Year;
    private int ExamId;
    private int Subject;

    public Exam(Context context, int Year, int Subject, int ExamId) {
        this.mContext = context;
        this.Year = Year;
        this.Subject = Subject;
        this.ExamId = ExamId;
    }

    public String getPDFLink(int Option) {
        return "lol"; //Todo: setup getPDFLink.
    }

    public int getYear() {
        return this.Year;
    }

    public int getExamYear() {
        String SubjectAbv = mContext.getResources().getStringArray(R.array.subjects_abv)[Subject];
        return Integer.valueOf(mContext.getResources().getStringArray(mContext.getResources()
                .getIdentifier("exams_" + getYearStr() + "_" + SubjectAbv + "_years", "array",
                        mContext.getPackageName()))[ExamId]);
    }

    public int getType() {
        if(getExamYear() < 1000)
            return CONTINU;
        else {
            return this.Year == MainActivity.YEAR_FIRST ? REGIONAL : NATIONAL;
        }
    }

    public int getSubject() {
        return this.Subject;
    }

    private String getYearStr() {
        return Year == MainActivity.YEAR_FIRST ? "1st" : "2nd";
    }
}
