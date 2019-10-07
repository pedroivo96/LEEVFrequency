package com.ufpi.leevfrequency.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.ufpi.leevfrequency.Model.Frequency;
import com.ufpi.leevfrequency.R;
import com.ufpi.leevfrequency.Utils.ConstantUtils;
import com.ufpi.leevfrequency.Utils.EventDecorator;
import com.ufpi.leevfrequency.Utils.NavigationDrawerUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UserAdvisorModeActivity extends AppCompatActivity {

    //------------------------- NavigationDrawer---------------------------------------------------
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView tUserId;
    private TextView tUserName;
    private TextView tUserEmail;
    private TextView tUserProjects;
    private Button bAddNewFrequency;
    private MaterialCalendarView calendarUserFrequencies;

    private ArrayList<Frequency> userFrequencies;

    private SharedPreferences prefs = null;

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseFrequencies;

    private String idStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_advisor_mode);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.USERS_BRANCH);
        mDatabaseFrequencies = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.FREQUENCIES_BRANCH);

        idStudent = getIntent().getStringExtra("idStudent");

        prefs = getSharedPreferences("com.ufpi.leevfrequency", MODE_PRIVATE);

        userFrequencies = new ArrayList<>();

        tUserId = findViewById(R.id.tUserId);
        tUserName = findViewById(R.id.tUserName);
        tUserEmail = findViewById(R.id.tUserEmail);
        tUserProjects = findViewById(R.id.tUserProjects);
        bAddNewFrequency = findViewById(R.id.bAddNewFrequency);
        calendarUserFrequencies = findViewById(R.id.calendarUserFrequencies);

        calendarUserFrequencies.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);

        configureNavigationDrawer();

        //Busca as informação do usuário logado para exibí-las nessa tela
        mDatabaseUsers
                .orderByKey()
                .equalTo(idStudent)
                .addValueEventListener(getUserInformationListener());

        getUserFrequencies();

    }

    private Context getContext(){
        return this;
    }

    private void configureNavigationDrawer(){
        //----------------------------Configure NavigationDrawer------------------------------------
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_menu_black, getContext().getTheme());
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

    private ValueEventListener getUserInformationListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        tUserId.setText(d.getKey());
                        tUserName.setText((String) d.child(ConstantUtils.USER_FIELD_NAME).getValue());
                        tUserEmail.setText((String) d.child(ConstantUtils.USER_FIELD_EMAIL).getValue());
                        tUserProjects.setText((String) d.child(ConstantUtils.USER_FIELD_PROJECTS).getValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void getUserFrequencies(){
        mDatabaseFrequencies
                .child(idStudent)
                .addValueEventListener(getUserFrequenciesListener());
    }

    private ValueEventListener getUserFrequenciesListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for(DataSnapshot d: dataSnapshot.getChildren()){

                        Frequency frequency = new Frequency();
                        frequency.setId(d.getKey());
                        frequency.setDate((Long) d.child(ConstantUtils.FREQUENCY_FIELD_DATE).getValue());

                        userFrequencies.add(frequency);
                    }
                    Log.i("TAG", String.valueOf(dataSnapshot.getChildrenCount()));
                }

                calendarUserFrequencies.clearSelection();
                ArrayList<CalendarDay> calendarDays = new ArrayList<>();

                for(Frequency f : userFrequencies){

                    Date date = new Date();
                    date.setTime(f.getDate());

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);

                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH)+1;
                    int year = calendar.get(Calendar.YEAR);

                    Log.i("TAG", day+"/"+month+"/"+year);

                    calendarUserFrequencies
                            .setDateSelected(CalendarDay.from(year, month, day), true);
                    calendarDays.add(CalendarDay.from(year, month, day));
                }

                /*Adiciona ao CalendarView um EventDecorator que permite posicionar um ponto embaixo
                 * das datas contidas na lista calendarDays */
                calendarUserFrequencies.addDecorator(new EventDecorator(R.color.colorAccent, calendarDays));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
