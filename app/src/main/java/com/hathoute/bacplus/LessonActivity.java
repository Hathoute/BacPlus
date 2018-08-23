package com.hathoute.bacplus;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.klinker.android.sliding.SlidingActivity;

import java.io.File;

public class LessonActivity extends SlidingActivity implements DownloadsManager.OnCallbackReceived {

    private Lesson lesson;
    private Resources resources;
    private boolean isProcessing = false;

    @Override
    public void init(Bundle savedInstanceState) {
        setImage(R.drawable.lesson_image);
        setContent(R.layout.activity_lesson);
        int iChosenSubject = getIntent().getIntExtra("subject", 0);
        int iChosenLesson = getIntent().getIntExtra("lesson", 0);
        lesson = new BacDataDBHelper(this).getLesson(iChosenSubject, iChosenLesson);
        resources = this.getResources();
        setupViews();
        setupListeners();
    }

    public void setupViews() {
        TextView tvLesson = findViewById(R.id.tvExam);
        TextView tvSubject = findViewById(R.id.tvSubject);
        TextView tvYear = findViewById(R.id.tvOptions);

        tvLesson.setText(Html.fromHtml(resources.getString(R.string.lesson_name)
                .replace("$", "<b>" + lesson.getName() + "</b>")));
        tvSubject.setText(Html.fromHtml(resources.getString(R.string.string_subject)
                .replace("$", "<b>" +
                        resources.getStringArray(R.array.subjects)[lesson.getSubject()] + "</b>")));
        tvYear.setText(Html.fromHtml(resources.getString(R.string.string_year)
                .replace("$", "<b>" + resources.getString
                        (lesson.getYear() == MainActivity.YEAR_FIRST ?
                                R.string.years_first : R.string.years_second) + "</b>")));
    }

    public void setupListeners() {
        LinearLayout openFile = findViewById(R.id.openFile);
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lesson.isAvailable(LessonActivity.this) == AppHelper.Storage.None) {
                    new DownloadsManager(LessonActivity.this, false)
                            .execute(lesson.getDirectoryPath(LessonActivity.this),
                                    String.valueOf(lesson.getId()));
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File pdfdir = new File(lesson
                            .isAvailable(LessonActivity.this) == AppHelper.Storage.Cache ?
                            getCacheDir() : getFilesDir(),
                            lesson.getDirectoryPath(LessonActivity.this));
                    File pdfFile = new File(pdfdir, lesson.getId() + ".pdf");
                    //intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                    intent.setDataAndType(FileProvider.getUriForFile(LessonActivity.this,
                            LessonActivity.this.getApplicationContext().getPackageName() + ".provider",
                            pdfFile), "application/pdf");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent intent1 = Intent.createChooser(intent, "Open With");
                    try {
                        startActivity(intent1);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(LessonActivity.this, "NOTHING", Toast.LENGTH_SHORT).show();
                        //Todo: Set Message to download PDF Reader
                    }
                }
            }
        });

        final LinearLayout downloadFile = findViewById(R.id.downloadFile);
        if(lesson.isAvailable(LessonActivity.this) == AppHelper.Storage.Data) {
            ((LinearLayout) downloadFile.getParent()).removeView(downloadFile);
        }
        else {
            downloadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DownloadsManager(LessonActivity.this, true)
                            .execute(lesson.getDirectoryPath(LessonActivity.this),
                                    String.valueOf(lesson.getId()));
                    isProcessing = true;
                }
            });
        }

        final LinearLayout deleteFile = findViewById(R.id.deleteFile);
        if(lesson.isAvailable(LessonActivity.this) != AppHelper.Storage.Data) {
            ((LinearLayout) deleteFile.getParent()).removeView(deleteFile);
        }
        else {
            deleteFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog();
                }
            });
        }
    }

    void showDeleteDialog() {
        final Dialog dDelete = new Dialog(LessonActivity.this);
        dDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dDelete.setContentView(R.layout.dialog_download);
        dDelete.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView tvNotice = dDelete.findViewById(R.id.tvNotice);
        final ProgressBar pbLoading = dDelete.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.GONE);
        final Button bCancel = dDelete.findViewById(R.id.bCancel);
        final Button bConfirm = dDelete.findViewById(R.id.bOpen);

        tvNotice.setText(R.string.delete_notice);
        bCancel.setText(R.string.no);
        bConfirm.setText(R.string.yes);

        dDelete.setCanceledOnTouchOutside(false);
        dDelete.show();

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dDelete.dismiss();
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir(),
                        lesson.getDirectoryPath(LessonActivity.this) + "/" +
                                lesson.getId() + ".pdf");
                tvNotice.setText(file.delete() ? R.string.delete_success : R.string.delete_fail);
                bCancel.setText(R.string.back);
                bConfirm.setVisibility(View.GONE);
            }
        });

        //Todo: Set back click listener to give user choice (double click).
    }

    @Override
    public void onDownloadFinished(File pdfFile, boolean isDownload) {
        if(!isDownload) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            intent.setDataAndType(FileProvider.getUriForFile(LessonActivity.this,
                    LessonActivity.this.getApplicationContext().getPackageName() + ".provider",
                    pdfFile), "application/pdf");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent intent1 = Intent.createChooser(intent, "Open With");
            try {
                LessonActivity.this.startActivity(intent1);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(LessonActivity.this, "NOTHING", Toast.LENGTH_SHORT).show();
                //Todo: Set Message to download PDF Reader
            }
        }
    }
}
