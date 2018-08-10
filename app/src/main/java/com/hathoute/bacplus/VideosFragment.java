package com.hathoute.bacplus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends Fragment {

    private Context mContext;
    ListView lvVideos;
    Subject subject;
    boolean rowsEnd;
    boolean isLoading;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        int iChosenYear = getArguments().getInt("year");
        int iChosenOption = getArguments().getInt("option");
        int iChosenSubject = getArguments().getInt("subject");
        subject = new Subject(mContext, iChosenYear, iChosenOption, iChosenSubject);
        lvVideos = view.findViewById(R.id.lvVideos);
        final VideosAdapter adapter = new VideosAdapter(new ArrayList<Video>(), mContext, subject);
        lvVideos.setAdapter(adapter);
        new configureData().execute();
        lvVideos.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(!isLoading && !rowsEnd)
                    {
                        isLoading = true;
                        new configureData().execute();
                    }
                }
            }
        });
        lvVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppHelper.watchYoutubeVideo(mContext, adapter.getItem(position).getYTId());
            }
        });
        return view;
    }


    private int curRow = 0;

    class configureData extends AsyncTask<Void, Video, Boolean> {
        private VideosAdapter videosAdapter;
        List<Video> videos;
        int lastRow = curRow;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            videos = subject.getVideos();
            videosAdapter = (VideosAdapter) lvVideos.getAdapter();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while(curRow < lastRow+10) {
                try {
                    Video video = videos.get(curRow);
                    if(!video.generateData())
                        continue;

                    publishProgress(video);
                } catch (Exception e) {
                    return true;
                }
                curRow++;
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Video... videos) {
            videosAdapter.add(videos[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            rowsEnd = result;
            isLoading = false;
        }
    }

}
