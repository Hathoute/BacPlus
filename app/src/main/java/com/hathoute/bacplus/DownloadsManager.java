package com.hathoute.bacplus;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadsManager extends AsyncTask<String, Integer, String> {

    OnCallbackReceived mCallback;

    private final Context context;
    private final boolean isDownload;
    private PowerManager.WakeLock mWakeLock;
    private ViewHolder viewHolder;
    private Dialog dHelp;
    private File pdfFile;
    private static String hostURL = "http://80.211.97.124/";

    private static class ViewHolder {
        TextView tvNotice;
        ProgressBar pbLoading;
        Button bOpen;
        Button bCancel;
    }

    public DownloadsManager(Context context, boolean isDownload) {
        this.context = context;
        this.isDownload = isDownload;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallback = (OnCallbackReceived) context;
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire(30000);
        viewHolder = new ViewHolder();
        showDialog();
    }

    @Override
    protected String doInBackground(String... params) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            String directory = params[0];
            String name = params[1] + ".pdf";
            URL url = new URL(hostURL + directory + "/" + name);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            File drc = new File(isDownload ? context.getFilesDir() : context.getCacheDir(), directory);
            if(!drc.exists()) {
                drc.mkdirs();
            }
            pdfFile = new File(drc, name);
            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(pdfFile);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    try {
                        output.close();
                        input.close();
                        pdfFile.delete();
                    } catch(Exception i){
                        i.printStackTrace();
                    }
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) total, fileLength);
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        if(isDownload) {
            viewHolder.tvNotice.setText(context.getResources().getString(R.string.download_size)
                    .replace("$", String.valueOf(progress[0]))
                    .replace("£", String.valueOf(progress[1])));
            viewHolder.pbLoading.setIndeterminate(false);
            viewHolder.pbLoading.setMax(100);
            viewHolder.pbLoading.setProgress((progress[0] * 100 / progress[1]));
        }
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        if(!isDownload)
            dHelp.dismiss();
        if (result != null)
            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            viewHolder.tvNotice.setText(R.string.download_successful);
            viewHolder.bOpen.setEnabled(true);
            mCallback.onDownloadFinished(pdfFile, isDownload);
        }
    }

    private void showDialog() {
        dHelp = new Dialog(context);
        dHelp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dHelp.setContentView(R.layout.dialog_download);
        dHelp.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        viewHolder.tvNotice = dHelp.findViewById(R.id.tvNotice);
        viewHolder.pbLoading = dHelp.findViewById(R.id.pbLoading);
        viewHolder.bCancel = dHelp.findViewById(R.id.bCancel);
        viewHolder.bOpen = dHelp.findViewById(R.id.bOpen);
        viewHolder.tvNotice.setText(isDownload ?
                R.string.download_starting : R.string.opening_starting);
        if(isDownload)
            viewHolder.bOpen.setEnabled(false);
        else
            viewHolder.bOpen.setVisibility(View.GONE);
        dHelp.setCanceledOnTouchOutside(false);
        dHelp.show();
        viewHolder.pbLoading.setIndeterminate(true);
        viewHolder.bOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                intent.setDataAndType(FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() + ".provider",
                        pdfFile), "application/pdf");
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent intent1 = Intent.createChooser(intent, "Open With");
                try {
                    context.startActivity(intent1);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "NOTHING", Toast.LENGTH_SHORT).show();
                    //Todo: Set Message to download PDF Reader
                }
            }
        });
        viewHolder.bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadsManager.this.cancel(true);
                dHelp.dismiss();
            }
        });
        //Todo: Set back click listener to give user choice (double click).
    }

    public interface OnCallbackReceived {
        void onDownloadFinished(File file, boolean isDownload);
    }
}