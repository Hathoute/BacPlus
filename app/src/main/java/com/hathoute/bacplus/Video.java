package com.hathoute.bacplus;

import android.content.Context;
import android.graphics.Bitmap;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;

public class Video {

    private Context mContext;
    private int Year;
    private int Subject;
    private String YTId;
    private Bitmap Thumbnail;
    private String Title;
    private String Channel;

    public Video(Context context, int Year, int Subject, int VideoId) {
        this.mContext = context;
        this.Year = Year;
        this.Subject = Subject;
        String SubjectAbv = mContext.getResources().getStringArray(R.array.subjects_abv)[Subject];
        this.YTId = mContext.getResources().getStringArray(mContext.getResources()
                .getIdentifier("videos_" + getYearStr() + "_" + SubjectAbv, "array",
                        mContext.getPackageName()))[VideoId];
    }

    public String getLink() {
        return "https://www.youtube.com/watch?v=" + YTId;
    }

    public String getTitle() {
        return this.Title;
    }

    public boolean generateData() {
        try {
            this.Thumbnail = AppHelper.getBitmapFromURL("http://img.youtube.com/vi/" + YTId + "/hqdefault.jpg");
            this.Thumbnail = Bitmap.createBitmap(Thumbnail, 0, 45, 480, 270);
        } catch (Exception e) {
            //Todo: remove all e.printstacktrace();
        }

        try {
            URL embededURL = new URL("http://www.youtube.com/oembed?url=" +
                    "https://www.youtube.com/watch?v=" +
                    YTId + "&format=json"
            );
            this.Title = new JSONObject(IOUtils.toString(embededURL)).getString("title");
        } catch (Exception e) {
            return false;
        }

        try {
            URL embededURL = new URL("http://www.youtube.com/oembed?url=" +
                    "https://www.youtube.com/watch?v=" +
                    YTId + "&format=json"
            );
            this.Channel = new JSONObject(IOUtils.toString(embededURL)).getString("author_name");
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Bitmap getThumbnail() {
        //Todo: Remove comment.
        /*if(Thumbnail == null)
            Thumbnail = BitmapFactory.decodeResource(mContext.getResources()
                    .getDrawable(R.drawable.default_thumbnail));*/
        return this.Thumbnail;
    }

    public String getChannel() {
        return this.Channel;
    }

    public String getYTId() {
        return this.YTId;
    }

    public int getYear() {
        return this.Year;
    }

    public int getSubject() {
        return this.Subject;
    }

    private String getYearStr() {
        return Year == MainActivity.YEAR_FIRST ? "1st" : "2nd";
    }
}

