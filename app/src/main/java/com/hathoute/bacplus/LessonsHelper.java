package com.hathoute.bacplus;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LessonsHelper {

    private Context mContext;

    public LessonsHelper(Context context) {
        this.mContext = context;
    }

    public List<String> formatSubjects(int Year, int Option) {
        String[] Subjects = mContext.getResources().getStringArray(R.array.subjects);
        String[] SubjectsTokens = mContext.getResources().getStringArray(R.array.subjects_helper);
        String choosenYear = Year == MainActivity.YEAR_FIRST ? "1st" : "2nd";
        String choosenOption = mContext.getResources()
                .getStringArray(Year == MainActivity.YEAR_FIRST ?
                        R.array.options_firstyear_helper : R.array.options_secondyear_helper)[Option];
        List<String> optionSubjects = new ArrayList<>();

        for(int i = 0; i < Subjects.length; i++) {
            List<String> TokensList = Arrays.asList(SubjectsTokens[i].split(";"));
            if(!TokensList.contains(choosenYear)) {
                continue;
            }

            if(TokensList.contains("all") && !TokensList.contains("-" + choosenOption)) {
                optionSubjects.add(Subjects[i]);
            }
            else if(TokensList.contains("+" + choosenOption)) {
                optionSubjects.add(Subjects[i]);
            }
        }

        return optionSubjects;
    }
}
