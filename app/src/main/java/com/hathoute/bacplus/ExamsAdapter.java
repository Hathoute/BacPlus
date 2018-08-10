package com.hathoute.bacplus;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ExamsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Exam> exams;

    private static class ViewHolder {
        TextView tvExamType;
        TextView tvExamId;
    }

    public ExamsAdapter(Context context, Subject subject) {
        this.mContext = context;
        this.exams = subject.getExams();
        for(int i = 0; i < this.exams.size(); i++) {
            if(this.exams.get(i).getExamYear() > 2500)
                continue;

            int j = 0;
            while(j < i) {
                if(this.exams.get(j).getExamYear() < this.exams.get(i).getExamYear()
                        || (this.exams.get(j).getExamYear()-1000 <= this.exams.get(i).getExamYear() && this.exams.get(j).getExamYear() > 2500)) {
                    Exam exam = this.exams.remove(j);
                    this.exams.add(exam);
                    i--;
                    continue;
                }
                j++;
            }
        }
    }

    @Override
    public int getCount() {
        return exams.size();
    }

    @Override
    public Exam getItem(int position) {
        return exams.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Exam exam = getItem(position);

        ExamsAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ExamsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.exam_item_arabic, parent, false);
            viewHolder.tvExamType = convertView.findViewById(R.id.examType);
            viewHolder.tvExamId = convertView.findViewById(R.id.examId);

            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ExamsAdapter.ViewHolder) convertView.getTag();

        String ExamId;
        String[] Type = mContext.getResources().getStringArray(R.array.exam_sessions);
        if(exam.getExamYear() < 1000)
            ExamId = mContext.getResources()
                    .getString(R.string.exam_surveille)
                    .replace("$", "<b>" + (exam.getExamYear()) + "</b>");
        else if(exam.getExamYear() > 2500)
            ExamId = mContext.getResources()
                    .getString(R.string.exam_session)
                    .replace("$", "<b>" + (exam.getExamYear()-1000)
                            + " " + Type[1] + "</b>");
        else
            ExamId = mContext.getResources()
                    .getString(R.string.exam_session)
                    .replace("$", "<b>" + (exam.getExamYear())
                            + " " + Type[0] + "</b>");

        viewHolder.tvExamId.setText(Html.fromHtml(ExamId));
        viewHolder.tvExamType.setText(mContext.getResources()
                .getStringArray(R.array.exam_types)[exam.getType()]);
        // Return the completed view to render on screen
        return convertView;
    }
}
