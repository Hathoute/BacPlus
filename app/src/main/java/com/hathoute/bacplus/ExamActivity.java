package com.hathoute.bacplus;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.klinker.android.sliding.SlidingActivity;

public class ExamActivity extends SlidingActivity {

    private int iChosenYear;
    private int iChosenOption;
    private int iChosenSubject;
    private int iChosenExam;
    private Exam exam;
    private Resources resources;

    @Override
    public void init(Bundle savedInstanceState) {
        setImage(R.drawable.exam_image);
        setContent(R.layout.activity_exam);
        iChosenYear = getIntent().getIntExtra("year", 0);
        iChosenOption = getIntent().getIntExtra("option", 0);
        iChosenSubject = getIntent().getIntExtra("subject", 0);
        iChosenExam = getIntent().getIntExtra("exam", 0);
        exam = new Exam(this, iChosenYear, iChosenSubject, iChosenExam);
        resources = this.getResources();
        setupViews();
    }

    public void setupViews() {
        TextView tvExam = findViewById(R.id.tvExam);
        TextView tvSubject = findViewById(R.id.tvSubject);
        TextView tvOptions = findViewById(R.id.tvOptions);
        TextView tvYear = findViewById(R.id.tvYear);

        if(exam.getExamYear() > 1000)
            tvExam.setText(Html.fromHtml(resources.getString(R.string.exam_session)
                    .replace("$", "<b>" + (exam.getExamYear() < 2500 ?
                            (exam.getExamYear() + " " + resources.getStringArray(R.array.exam_sessions)[0]) :
                            (exam.getExamYear() - 1000) + " " + resources.getStringArray(R.array.exam_sessions)[1])
                            + " " + "</b>")));
        else

            tvExam.setText(Html.fromHtml(resources.getString(R.string.exam_surveille)
                    .replace("$", "<b>" + exam.getExamYear() + "</b>")));

        tvSubject.setText(Html.fromHtml(resources.getString(R.string.string_subject)
                .replace("$", "<b>" +
                        resources.getStringArray(R.array.subjects)[iChosenSubject] + "</b>")));

        tvYear.setText(Html.fromHtml(resources.getString(R.string.string_year)
                .replace("$", "<b>" + resources.getString
                        (iChosenYear == MainActivity.YEAR_FIRST ?
                                R.string.years_first : R.string.years_second) + "</b>")));

        tvOptions.setText(Html.fromHtml(resources.getString(R.string.string_option)
                .replace("$", "<b>" +
                        resources.getStringArray(iChosenYear == MainActivity.YEAR_FIRST ?
                                R.array.options_firstyear_array :
                                R.array.options_secondyear_array)[iChosenOption] + "</b>")));
    }
}
