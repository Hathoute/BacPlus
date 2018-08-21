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
import android.webkit.DownloadListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.klinker.android.sliding.SlidingActivity;

import java.io.File;

public class ExamActivity extends SlidingActivity implements DownloadsManager.OnCallbackReceived {

    private Exam exam;
    private Resources resources;
    private boolean isProcessing = false;

    @Override
    public void init(Bundle savedInstanceState) {
        setImage(R.drawable.exam_image);
        setContent(R.layout.activity_exam);
        int iChosenSubject = getIntent().getIntExtra("subject", 0);
        int iChosenExam = getIntent().getIntExtra("exam", 0);
        exam = new DatabaseHelper(this).getExam(iChosenSubject, iChosenExam);
        resources = this.getResources();
        setupViews();
        setupListeners();
    }

    public void setupViews() {
        TextView tvExam = findViewById(R.id.tvExam);
        TextView tvSubject = findViewById(R.id.tvSubject);
        TextView tvOptions = findViewById(R.id.tvOptions);
        TextView tvYear = findViewById(R.id.tvYear);

        if(exam.getType() != Exam.Types.CLASS)
            tvExam.setText(Html.fromHtml(resources.getString(R.string.exam_session)
                    .replace("$", "<b>" + exam.getExamId() + " " +
                    resources.getStringArray(R.array.exam_sessions)[exam.getType()]) + " " + "</b>"));
        else

            tvExam.setText(Html.fromHtml(resources.getString(R.string.exam_surveille)
                    .replace("$", "<b>" + exam.getExamId() + "</b>")));

        tvSubject.setText(Html.fromHtml(resources.getString(R.string.string_subject)
                .replace("$", "<b>" +
                        resources.getStringArray(R.array.subjects)[exam.getSubject()] + "</b>")));

        tvYear.setText(Html.fromHtml(resources.getString(R.string.string_year)
                .replace("$", "<b>" + resources.getString
                        (exam.getYear() == MainActivity.YEAR_FIRST ?
                                R.string.years_first : R.string.years_second) + "</b>")));

        tvOptions.setText(Html.fromHtml(resources.getString(R.string.string_option)
                .replace("$", "<b>" +
                        resources.getStringArray(exam.getYear() == MainActivity.YEAR_FIRST ?
                                R.array.options_firstyear_array :
                                R.array.options_secondyear_array)[exam.getSubject()] + "</b>")));
    }

    public void setupListeners() {
        LinearLayout openFile = findViewById(R.id.openFile);
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exam.isAvailable(ExamActivity.this) == AppHelper.Storage.None) {
                    new DownloadsManager(ExamActivity.this, false)
                            .execute(exam.getDirectoryPath(ExamActivity.this),
                                    String.valueOf(exam.getId()));
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File pdfdir = new File(exam
                            .isAvailable(ExamActivity.this) == AppHelper.Storage.Cache ?
                            getCacheDir() : getFilesDir(),
                            exam.getDirectoryPath(ExamActivity.this));
                    File pdfFile = new File(pdfdir, exam.getId() + ".pdf");
                    //intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                    intent.setDataAndType(FileProvider.getUriForFile(ExamActivity.this,
                            ExamActivity.this.getApplicationContext().getPackageName() + ".provider",
                            pdfFile), "application/pdf");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent intent1 = Intent.createChooser(intent, "Open With");
                    try {
                        startActivity(intent1);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(ExamActivity.this, "NOTHING", Toast.LENGTH_SHORT).show();
                        //Todo: Set Message to download PDF Reader
                    }
                }
            }
        });

        final LinearLayout downloadFile = findViewById(R.id.downloadFile);
        if(exam.isAvailable(ExamActivity.this) == AppHelper.Storage.Data) {
            ((LinearLayout) downloadFile.getParent()).removeView(downloadFile);
        }
        else {
            downloadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DownloadsManager(ExamActivity.this, true)
                            .execute(exam.getDirectoryPath(ExamActivity.this),
                                    String.valueOf(exam.getId()));
                    isProcessing = true;
                }
            });
        }

        final LinearLayout deleteFile = findViewById(R.id.deleteFile);
        if(exam.isAvailable(ExamActivity.this) != AppHelper.Storage.Data) {
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
        final Dialog dDelete = new Dialog(ExamActivity.this);
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
                        exam.getDirectoryPath(ExamActivity.this) + "/" +
                                exam.getId() + ".pdf");
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
            intent.setDataAndType(FileProvider.getUriForFile(ExamActivity.this,
                    ExamActivity.this.getApplicationContext().getPackageName() + ".provider",
                    pdfFile), "application/pdf");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent intent1 = Intent.createChooser(intent, "Open With");
            try {
                ExamActivity.this.startActivity(intent1);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ExamActivity.this, "NOTHING", Toast.LENGTH_SHORT).show();
                //Todo: Set Message to download PDF Reader
            }
        }
    }
}
