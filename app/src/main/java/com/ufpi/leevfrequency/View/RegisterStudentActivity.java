package com.ufpi.leevfrequency.View;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ufpi.leevfrequency.Model.User;
import com.ufpi.leevfrequency.R;
import com.ufpi.leevfrequency.Utils.ConstantUtils;

public class RegisterStudentActivity extends AppCompatActivity {

    private EditText eStudentName;
    private EditText eStudentProject;
    private EditText eStudentEmail;
    private Button bRegisterStudent;

    private DatabaseReference mDatabase;

    private String idTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        eStudentName = findViewById(R.id.eStudentName);
        eStudentProject = findViewById(R.id.eStudentProject);
        eStudentEmail = findViewById(R.id.eStudentEmail);
        bRegisterStudent = findViewById(R.id.bRegisterStudent);

        mDatabase = FirebaseDatabase.getInstance().getReference().child(ConstantUtils.DATABASE_ACTUAL_BRANCH).child("users");

        bRegisterStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Verifica campos vazios
                if(verifyEmptyFields()){
                    Toast.makeText(getContext(), "Um dos campos está vazio", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Caso os campos estejam todos preenchidos

                    //Constrói um objeto com os dados do novo usuário
                    User newUser = new User();
                    newUser.setName(eStudentName.getText().toString());
                    newUser.setEmail(eStudentEmail.getText().toString());
                    newUser.setProjects(eStudentProject.getText().toString());
                    newUser.setUserType(ConstantUtils.USER_TYPE_STUDENT);
                    newUser.setVisible(true);
                    newUser.setFinalizedRegister(false);

                    registerStudent(newUser);
                }
            }
        });
    }

    private boolean verifyEmptyFields(){
        if(eStudentName.getText().toString().isEmpty() ||
           eStudentProject.getText().toString().isEmpty() ||
           eStudentEmail.getText().toString().isEmpty()){

            return true;
        }
        else{
            return false;
        }
    }

    private void registerStudent(User user){

        DatabaseReference elementReference = mDatabase.push();
        String id = elementReference.getKey();

        mDatabase.child(id).child("name").setValue(user.getName());
        mDatabase.child(id).child("email").setValue(user.getEmail());
        mDatabase.child(id).child("projects").setValue(user.getProjects());
        mDatabase.child(id).child("userType").setValue(user.getUserType());
        mDatabase.child(id).child("isVisible").setValue(user.getVisible());
        mDatabase.child(id).child("isFinalizedRegister").setValue(user.getFinalizedRegister());

        Toast.makeText(getContext(), "Pré-cadastro realizado", Toast.LENGTH_SHORT).show();
    }

    private Context getContext(){
        return this;
    }
}
