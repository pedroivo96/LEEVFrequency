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

public class LeevTeachersAdapter extends BaseAdapter {

    private ArrayList<User> leevTeachers;
    private Context context;

    public LeevTeachersAdapter(ArrayList<User> leevTeachers, Context context){
        this.leevTeachers = leevTeachers;
        this.context = context;
    }

    @Override
    public int getCount() {
        return leevTeachers.size();
    }

    @Override
    public Object getItem(int position) {
        return leevTeachers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.teachers_without_remove_item_listview, parent, false);

        User teachers = leevTeachers.get(position);

        TextView tTeacherName = view1.findViewById(R.id.tTeacherName);
        TextView tTeacherProjects = view1.findViewById(R.id.tTeacherProjects);

        tTeacherName.setText(teachers.getName());
        tTeacherProjects.setText(teachers.getProjects());

        return view1;
    }
}
