package com.ufpi.leevfrequency.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ufpi.leevfrequency.R;
import com.ufpi.leevfrequency.Utils.ConstantUtils;
import com.ufpi.leevfrequency.Utils.NavigationDrawerUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserActivity extends AppCompatActivity {

    //------------------------- NavigationDrawer---------------------------------------------------
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView tUserId;
    private TextView tUserName;
    private TextView tUserEmail;
    private TextView tUserProjects;
    private Button bAddNewFrequency;

    private SharedPreferences prefs = null;

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseFrequencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        prefs = getSharedPreferences("com.ufpi.leevfrequency", MODE_PRIVATE);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.USERS_BRANCH);

        tUserId = findViewById(R.id.tUserId);
        tUserName = findViewById(R.id.tUserName);
        tUserEmail = findViewById(R.id.tUserEmail);
        tUserProjects = findViewById(R.id.tUserProjects);
        bAddNewFrequency = findViewById(R.id.bAddNewFrequency);

        /* Método responsável por configurar os eventos de edição de informações do usuário ao apertar
        * e segurar cada TextView */
        setUpdateUserInformationEvents();

        /*Método usa para checar a conectividade e adicionar um registro de frequẽncia do aluno
        * atualmente logado */
        verifyAndAddFrequency();

        bAddNewFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference elementReference = mDatabaseFrequencies
                        .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                        .push();
                String id = elementReference.getKey();

                mDatabaseFrequencies
                        .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                        .child(id)
                        .child(ConstantUtils.FREQUENCY_FIELD_DATE)
                        .setValue(000000000);
            }
        });

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

        //Busca as informação do usuário logado para exibí-las nessa tela
        mDatabaseUsers
                .orderByChild(ConstantUtils.USER_FIELD_EMAIL)
                .equalTo(prefs.getString(ConstantUtils.USER_FIELD_EMAIL,""))
                .addValueEventListener(getUserInformationListener());

    }

    private Context getContext(){
        return this;
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

    private void setUpdateUserInformationEvents(){
        tUserName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                String updateInformation = createAndShowInputDialog();

                mDatabaseUsers
                        .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                        .child(ConstantUtils.USER_FIELD_NAME).setValue(updateInformation);
                return true;
            }
        });

        tUserEmail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                String updateInformation = createAndShowInputDialog();

                mDatabaseUsers
                        .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                        .child(ConstantUtils.USER_FIELD_EMAIL).setValue(updateInformation);
                return true;
            }
        });

        tUserProjects.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                String updateInformation = createAndShowInputDialog();

                mDatabaseUsers
                        .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                        .child(ConstantUtils.USER_FIELD_PROJECTS).setValue(updateInformation);
                return true;
            }
        });
    }

    private String createAndShowInputDialog(){
        final String[] updateData = new String[1];

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.eInformation);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                updateData[0] = userInput.getText().toString();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        return updateData[0];
    }

    private void verifyAndAddFrequency(){

        //Primeiro, checar se há conectividade
        //Segundo, chegar se é a rede da UFPI
        //Terceiro, adicionar o registro de frequência, caso não haja registro de hoje

        if(isNetworkConnected()){
            Log.i("TAG", "Você está conectado a uma rede");

            if(isUFPINetwork()){
                //Obter as horas 00:00 e 23:59 do dia de hoje

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                int day = Integer.parseInt((String) DateFormat.format("dd", currentTime));
                int month = Integer.parseInt((String) DateFormat.format("MM", currentTime));
                int year = Integer.parseInt((String) DateFormat.format("yyyy", currentTime));

                Log.i("TAG", "Data atual :"+day+month+year);

                try {
                    //Date de 0 horas e 0 minutos do dia atual
                    Date firstDate1 = format.parse (year+"-"+month+"-"+day+" 00:00:00");

                    //Date de 23 horas e 59 minutos do dia atual
                    Date secondDate1 = format.parse (year+"-"+month+"-"+day+" 23:59:59");

                    mDatabaseFrequencies
                            .child(prefs.getString(ConstantUtils.USER_FIELD_ID, ""))
                            .orderByChild(ConstantUtils.FREQUENCY_FIELD_DATE)
                            .startAt(firstDate1.getTime())
                            .endAt(secondDate1.getTime())
                            .addValueEventListener(verifyAndRegisterFrequenciesListener(Calendar.getInstance().getTime()));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            else{
                Log.i("TAG", "Não está conectado à rede da UFPI");
            }
        }
        else{
            Log.i("TAG", "Você não está conectado a uma rede");
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean isUFPINetwork(){

        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !ssid.isEmpty()) {
                ssid = connectionInfo.getSSID();

                if(ssid.equals("UFPI")){
                    return true;
                }
                else{
                    return false;
                }
            }
        }

        return false;
    }

    private ValueEventListener verifyAndRegisterFrequenciesListener(final Date time){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //Já existe um registro de frequência nesse dia

                    Log.i("TAG", "O usuário atualmente logado já possui um registro de frequência hoje");
                }
                else{

                    DatabaseReference elementReference = mDatabaseFrequencies
                            .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                            .push();
                    String id = elementReference.getKey();

                    mDatabaseFrequencies
                            .child(prefs.getString(ConstantUtils.USER_FIELD_ID,""))
                            .child(id)
                            .child(ConstantUtils.FREQUENCY_FIELD_DATE)
                            .setValue(time.getTime());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
