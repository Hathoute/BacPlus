package com.hathoute.bacplus;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LastSeenFragment extends Fragment {
    Context mContext;
    RecyclerView rvLastSeen;
    List<Object> objectList;
    RVItemsAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lastseen, container, false);

        rvLastSeen = view.findViewById(R.id.rvLastSeen);
        setupRecyclerView();
        prepareObjects();

        return view;
    }

    private void setupRecyclerView() {
        rvLastSeen.setHasFixedSize(true);
        objectList = new ArrayList<>();
        mAdapter = new RVItemsAdapter(objectList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvLastSeen.setLayoutManager(mLayoutManager);
        rvLastSeen.setItemAnimator(new DefaultItemAnimator());
        rvLastSeen.setAdapter(mAdapter);
        rvLastSeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void prepareObjects() {
        Cursor cursor = new OfflineDBHelper(mContext).get(OfflineDBHelper.LAST);
        if(cursor.moveToFirst()) {
            do {
                int Type = cursor.getInt(1);
                int Subject = cursor.getInt(2);
                int Id = cursor.getInt(3);
                Object object;
                if(Type == OfflineDBHelper.TYPE_LESSON) {
                    object = new BacDataDBHelper(mContext).getLesson(Subject, Id);
                }
                else {
                    object = new BacDataDBHelper(mContext).getExam(Subject, Id);
                }
                objectList.add(object);
            } while (cursor.moveToNext());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
