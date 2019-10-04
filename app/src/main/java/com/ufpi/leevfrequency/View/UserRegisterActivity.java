package com.ufpi.leevfrequency.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ufpi.leevfrequency.Adapters.UserTypeSpinnerAdapter;
import com.ufpi.leevfrequency.Model.User;
import com.ufpi.leevfrequency.R;
import com.ufpi.leevfrequency.Utils.ConstantUtils;

import java.util.ArrayList;

public class UserRegisterActivity extends AppCompatActivity {

    private EditText eUserName;
    private EditText eUserProjects;
    private EditText eUserEmail;
    private Spinner spUserType;
    private Button bRegisterStudent;

    private DatabaseReference mDatabase;

    private String idAdvisor;
    private String selectedUserType;

    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        eUserName = findViewById(R.id.eUserName);
        eUserProjects = findViewById(R.id.eUserProjects);
        eUserEmail = findViewById(R.id.eUserEmail);
        spUserType = findViewById(R.id.spUserType);
        bRegisterStudent = findViewById(R.id.bRegisterStudent);

        prefs = getSharedPreferences("com.ufpi.leevfrequency", MODE_PRIVATE);
        idAdvisor = prefs.getString(ConstantUtils.USER_FIELD_ID,"");

        configureUserTypeSpinner();

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.USERS_BRANCH);

        bRegisterStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Verifica campos vazios
                if(verifyEmptyFields()){
                    Toast.makeText(getContext(), "Um dos campos está vazio", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Caso os campos estejam todos preenchidos

                    mDatabase
                            .orderByChild(ConstantUtils.USER_FIELD_EMAIL)
                            .equalTo(eUserEmail.getText().toString())
                            .addListenerForSingleValueEvent(verifyEmailExistence());
                }
            }
        });
    }

    private boolean verifyEmptyFields(){
        if(eUserName.getText().toString().isEmpty() ||
                eUserProjects.getText().toString().isEmpty() ||
           eUserEmail.getText().toString().isEmpty()){

            return true;
        }
        else{
            return false;
        }
    }

    private ValueEventListener verifyEmailExistence(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(getContext(), "Já existe um usuário cadastrado com esse e-mail", Toast.LENGTH_SHORT).show();
                }
                else{

                    //Constrói um objeto com os dados do novo usuário
                    User newUser = new User();
                    newUser.setName(eUserName.getText().toString());
                    newUser.setEmail(eUserEmail.getText().toString());
                    newUser.setProjects(eUserProjects.getText().toString());
                    newUser.setUserType((selectedUserType.equals("Aluno") ? ConstantUtils.USER_TYPE_STUDENT : ConstantUtils.USER_TYPE_TEACHER));
                    newUser.setVisible(true);
                    newUser.setRegisterFinalized(false);
                    newUser.setIdAdvisor(selectedUserType.equals("Aluno") ? idAdvisor : "Sem orientador");

                    registerStudent(newUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void registerStudent(User user){

        DatabaseReference elementReference = mDatabase.push();
        String id = elementReference.getKey();

        mDatabase.child(id).child(ConstantUtils.USER_FIELD_NAME).setValue(user.getName());
        mDatabase.child(id).child(ConstantUtils.USER_FIELD_EMAIL).setValue(user.getEmail());
        mDatabase.child(id).child(ConstantUtils.USER_FIELD_PROJECTS).setValue(user.getProjects());
        mDatabase.child(id).child(ConstantUtils.USER_FIELD_USERTYPE).setValue(user.getUserType());
        mDatabase.child(id).child(ConstantUtils.USER_FIELD_VISIBLE).setValue(user.getVisible());
        mDatabase.child(id).child(ConstantUtils.USER_FIELD_REGISTERFINALIZED).setValue(user.getRegisterFinalized());
        mDatabase.child(id).child(ConstantUtils.USER_FIELD_IDADVISOR).setValue(user.getIdAdvisor());

        Toast.makeText(getContext(), "Pré-cadastro de"+user.getName()+"foi realizado", Toast.LENGTH_SHORT).show();
    }

    private Context getContext(){
        return this;
    }

    private void configureUserTypeSpinner(){
        String[] typeArray = getResources().getStringArray(R.array.user_types);

        ArrayList<String> options = new ArrayList<>();
        options.add(typeArray[0]);
        options.add(typeArray[1]);

        SpinnerAdapter mCustomAdapter = new UserTypeSpinnerAdapter(options, getContext());
        spUserType.setAdapter(mCustomAdapter);

        spUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUserType = (String) adapterView.getItemAtPosition(i);
                int selectedId = i+1;

                Log.i("TAG", selectedUserType+": "+selectedId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
