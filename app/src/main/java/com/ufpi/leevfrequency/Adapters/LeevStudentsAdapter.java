package com.ufpi.leevfrequency.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ufpi.leevfrequency.Model.User;
import com.ufpi.leevfrequency.R;

import java.util.ArrayList;

public class LeevStudentsAdapter extends BaseAdapter {

    private ArrayList<User> leevStudents;
    private Context context;

    public LeevStudentsAdapter(ArrayList<User> leevStudents, Context context){
        this.leevStudents = leevStudents;
        this.context = context;
    }

    @Override
    public int getCount() {
        return leevStudents.size();
    }

    @Override
    public Object getItem(int position) {
        return leevStudents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.students_without_remove_item_listview, parent, false);

        User student = leevStudents.get(position);

        TextView tStudentName = view1.findViewById(R.id.tStudentName);
        TextView tStudentProjects = view1.findViewById(R.id.tStudentProjects);

        tStudentName.setText(student.getName());
        tStudentProjects.setText(student.getProjects());

        return view1;
    }
}
