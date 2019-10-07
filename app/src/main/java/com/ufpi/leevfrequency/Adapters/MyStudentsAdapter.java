package com.ufpi.leevfrequency.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ufpi.leevfrequency.Model.User;
import com.ufpi.leevfrequency.R;
import com.ufpi.leevfrequency.Utils.ConstantUtils;

import java.util.ArrayList;

public class MyStudentsAdapter extends BaseAdapter {

    private ArrayList<User> myStudents;
    private Context context;
    private DatabaseReference mDatabase;

    public MyStudentsAdapter(ArrayList<User> myStudents, Context context){
        this.myStudents = myStudents;
        this.context = context;

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.USERS_BRANCH);
    }

    @Override
    public int getCount() {
        return myStudents.size();
    }

    @Override
    public Object getItem(int position) {
        return myStudents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.students_with_remove_item_listview, parent, false);

        final User student = myStudents.get(position);

        TextView tStudentName = view1.findViewById(R.id.tStudentName);
        TextView tStudentProjects = view1.findViewById(R.id.tStudentProjects);

        tStudentName.setText(student.getName());
        tStudentProjects.setText(student.getProjects());

        return view1;
    }
}
