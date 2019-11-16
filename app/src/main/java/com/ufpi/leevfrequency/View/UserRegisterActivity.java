package com.ufpi.leevfrequency.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
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
import com.ufpi.leevfrequency.Utils.NavigationDrawerUtils;

import java.util.ArrayList;

public class UserRegisterActivity extends AppCompatActivity {

    //------------------------- NavigationDrawer---------------------------------------------------
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private EditText eUserName;
    private EditText eUserProjects;
    private EditText eUserEmail;
    private Spinner spUserType;
    private Button bRegisterStudent;

    private DatabaseReference mDatabase;

    private String idAdvisor;
    private String selectedUserType;

    private SharedPreferences prefs = null;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutProjects;

    private CoordinatorLayout myCoordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        myCoordinatorLayout = findViewById(R.id.myCoordinatorLayout);

        eUserName = findViewById(R.id.eUserName);
        eUserProjects = findViewById(R.id.eUserProjects);
        eUserEmail = findViewById(R.id.eUserEmail);
        spUserType = findViewById(R.id.spUserType);
        bRegisterStudent = findViewById(R.id.bRegisterStudent);

        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutProjects = findViewById(R.id.textInputLayoutProjects);

        textInputLayoutName.setHint("Nome do usuário");
        textInputLayoutEmail.setHint("E-mail");
        textInputLayoutProjects.setHint("Área/Projetos");

        prefs = getSharedPreferences("com.ufpi.leevfrequency", MODE_PRIVATE);
        idAdvisor = prefs.getString(ConstantUtils.USER_FIELD_ID,"");

        configureUserTypeSpinner();

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.USERS_BRANCH);

        bRegisterStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearErrorName();
                clearErrorEmail();
                clearErrorProjects();

                //Verifica campos vazios
                if(eUserName.getText().toString().isEmpty() ||
                   eUserEmail.getText().toString().isEmpty() ||
                   eUserProjects.getText().toString().isEmpty()){

                    verifyEmptyFields();

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

        configureNavigationDrawer();
    }

    private void verifyEmptyFields(){
        if(eUserName.getText().toString().isEmpty()){
            enableAndShowErrorName("O campo de nome está vazio");
        }
        if(eUserEmail.getText().toString().isEmpty()){
            enableAndShowErrorEmail("O campo de e-mail está vazio");
        }
        if(eUserProjects.getText().toString().isEmpty()){
            enableAndShowErrorProjects("O campo de área/projetos está vazio");
        }
    }

    private ValueEventListener verifyEmailExistence(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //Toast.makeText(getContext(), "Já existe um usuário cadastrado com esse e-mail", Toast.LENGTH_SHORT).show();
                    enableAndShowErrorEmail("E-mail já cadastrado");
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

        simplySnackbar("Pré-cadastro de "+user.getName()+" foi realizado");

        eUserName.setText("");
        eUserEmail.setText("");
        eUserProjects.setText("");
        //Toast.makeText(getContext(), "Pré-cadastro de"+user.getName()+" foi realizado", Toast.LENGTH_SHORT).show();
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

    private void configureNavigationDrawer(){
        //----------------------------Configure NavigationDrawer------------------------------------
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_menu_white, getContext().getTheme());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.open_drawer, R.string.close_drawer);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.getMenu().clear();

        View headerView = navigationView.getHeaderView(0);
        TextView nav_header_nome = (TextView) headerView.findViewById(R.id.nav_header_name);
        nav_header_nome.setText(prefs.getString("name", ""));

        TextView nav_header_email = (TextView) headerView.findViewById(R.id.nav_header_email);
        nav_header_email.setText(prefs.getString("email",""));

        if(prefs.getInt(ConstantUtils.USER_FIELD_USERTYPE, -1) == ConstantUtils.USER_TYPE_STUDENT){
            //Usuário é um estudante
            navigationView.inflateMenu(R.menu.menu_student);
        }
        else{
            //Usuário é um professor
            navigationView.inflateMenu(R.menu.menu_teacher);
        }

        //Configura o evento de seleção de algum item do menu do DrawerLayout
        navigationView.setNavigationItemSelectedListener(
                NavigationDrawerUtils.getNavigationDrawerItemSelectedListener(getContext(),
                        prefs.getInt(ConstantUtils.USER_FIELD_USERTYPE,-1), drawerLayout));

    }

    private void enableAndShowErrorName(String errorMessage){
        textInputLayoutName.setErrorEnabled(true);
        textInputLayoutName.setError(errorMessage);
    }

    private void enableAndShowErrorEmail(String errorMessage){
        textInputLayoutEmail.setErrorEnabled(true);
        textInputLayoutEmail.setError(errorMessage);
    }

    private void enableAndShowErrorProjects(String errorMessage){
        textInputLayoutProjects.setErrorEnabled(true);
        textInputLayoutProjects.setError(errorMessage);
    }

    private void clearErrorName(){
        textInputLayoutName.setErrorEnabled(false);
        textInputLayoutName.setError(null);
    }

    private void clearErrorEmail(){
        textInputLayoutEmail.setErrorEnabled(false);
        textInputLayoutEmail.setError(null);
    }

    private void clearErrorProjects(){
        textInputLayoutProjects.setErrorEnabled(false);
        textInputLayoutProjects.setError(null);
    }

    public void simplySnackbar(String message){

        Snackbar snackbar = Snackbar.make(myCoordinatorLayout,message, Snackbar.LENGTH_LONG);
        snackbar.show();

    }

}
