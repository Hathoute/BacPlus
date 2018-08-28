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

import java.util.ArrayList;
import java.util.List;

public class AvailableDocsFragment extends Fragment {
    Context mContext;
    RecyclerView rvAvailable;
    int iSubject;
    List<Object> objectList;
    List<Object> sectionObjectList;
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

        iSubject = getArguments().getInt("subject");
        if(iSubject == -1)
            ((OfflineDocsActivity)getActivity()).setActionBarTitle(R.string.nav_all_files);
        else
            ((OfflineDocsActivity)getActivity()).setActionBarTitle(
                    mContext.getResources().getStringArray(R.array.subjects)[iSubject]);

        rvAvailable = view.findViewById(R.id.rvLastSeen);
        setupRecyclerView();
        prepareObjects();
        sectionObjects();

        return view;
    }

    private void setupRecyclerView() {
        rvAvailable.setHasFixedSize(true);
        objectList = new ArrayList<>();
        sectionObjectList = new ArrayList<>();
        mAdapter = new RVItemsAdapter(sectionObjectList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvAvailable.setLayoutManager(mLayoutManager);
        rvAvailable.setItemAnimator(new DefaultItemAnimator());
        rvAvailable.setAdapter(mAdapter);
        rvAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void prepareObjects() {
        Cursor cursor;
        if(iSubject == -1)
            cursor = new OfflineDBHelper(mContext).getAvailable();
        else
            cursor = new OfflineDBHelper(mContext).getAvailable(iSubject);
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
                System.out.println(((Lesson)object).getName());
            } while (cursor.moveToNext());
        }
    }

    private void sectionObjects() {
        int lastId = iSubject;
        for(Object object : objectList) {
            if(object instanceof Lesson) {
                int subId = ((Lesson) object).getSubject();
                if(subId != lastId) {
                    sectionObjectList.add(subId);
                    lastId = subId;
                }
                sectionObjectList.add(object);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}