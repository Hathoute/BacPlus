package com.hathoute.bacplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

public class SubjectsActivity extends AppCompatActivity {

    private int iChosenYear;
    private int iChosenOption;
    private GridView gvSubjects;
    private LessonsHelper lessonsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        //If no data is found, choose Second Year and SC Math option.
        iChosenYear = getIntent().getIntExtra("year", MainActivity.YEAR_SECOND);
        iChosenOption = getIntent().getIntExtra("option", 0);

        lessonsHelper = new LessonsHelper(this);
        gvSubjects = findViewById(R.id.gvSubjects);
        setupGridView();
    }

    public void setupGridView() {
        List<Subject> list = lessonsHelper.formatSubjects(iChosenYear, iChosenOption);
        CustomGridAdapter gridAdapter = new CustomGridAdapter(SubjectsActivity.this, list);
        gvSubjects.setAdapter(gridAdapter);
        gvSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent
                        (SubjectsActivity.this, TabbedSubjectActivity.class);
                intent.putExtra("year", iChosenYear)
                        .putExtra("option", iChosenOption)
                        .putExtra("subject", position);
                startActivity(intent);
            }
        });
    }
}
