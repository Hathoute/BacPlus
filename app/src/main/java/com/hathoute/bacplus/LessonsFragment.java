package com.hathoute.bacplus;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LessonsFragment extends Fragment {

    private Context mContext;
    private int iChosenYear;
    private int iChosenOption;
    private int iChosenSubject;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lessons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iChosenYear = getArguments().getInt("year");
        iChosenOption = getArguments().getInt("option");
        iChosenSubject = getArguments().getInt("subject");
        System.out.println("hello!");
        Subject subject = new Subject(mContext, iChosenYear, iChosenOption, iChosenSubject);
        for(Lesson lesson : subject.getLessons()) {
            System.out.println(lesson.getName());
        }
    }
}
