package com.ufpi.leevfrequency.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ufpi.leevfrequency.R;
import com.ufpi.leevfrequency.Utils.ConstantUtils;
import com.ufpi.leevfrequency.Utils.MethodUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText eEmail;
    private EditText ePassword;
    private Button bLogin;
    private Button bForgetPassword;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(ConstantUtils.DATABASE_ACTUAL_BRANCH).child("users");

        eEmail = findViewById(R.id.eEmail);
        ePassword = findViewById(R.id.ePassword);
        bLogin = findViewById(R.id.bLogin);
        bForgetPassword = findViewById(R.id.bForgetPassword);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Verificar se algum dos campos está vazio
                if(eEmail.getText().toString().isEmpty() ||
                   ePassword.getText().toString().isEmpty()){

                    Toast.makeText(getContext(), "Um dos campos está vazio!", Toast.LENGTH_SHORT).show();
                }
                else{

                    //Verificar se o campo de e-mail possui um endereço de e-mail válido
                    if(!MethodUtils.isEmailValid(eEmail.getText().toString())){
                        Toast.makeText(getContext(), "Informe um e-mail válido!", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        //Ou seja, Cadastro finalizado
                        mAuth.signInWithEmailAndPassword(eEmail.getText().toString(), ePassword.getText().toString())
                                .addOnCompleteListener(configureCompleteSignInListener())
                                .addOnFailureListener(configureFailureSigninListener());
                    }
                }
            }
        });

        bForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private Context getContext(){
        return this;
    }

    private OnCompleteListener configureCompleteSignInListener(){
        return new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(getContext(), "Login com sucesso!", Toast.LENGTH_SHORT).show();

                    //Agora, buscamos as informações referentes a este Usuário
                    mDatabase.orderByChild("email").equalTo(eEmail.getText().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){
                                for(DataSnapshot d : dataSnapshot.getChildren()){

                                    //Iremos salvar nas preferences apenas email e id
                                    String email = (String) d.child("email").getValue();
                                    String id = d.getKey();

                                    prefs = getSharedPreferences("com.ufpi.leevfrequency", MODE_PRIVATE);
                                    prefs.edit().putString("email", email).commit();
                                    prefs.edit().putString("id", id).commit();

                                    Intent intent = new Intent(getContext(), UserActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    // If sign in fails, display a message to the user.
                    Log.i("TAG", "signInWithEmail:failure", task.getException());

                }
            }
        };
    }

    private OnFailureListener configureFailureSigninListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if(e instanceof FirebaseNetworkException){
                    Toast.makeText(getContext(), "Verifique sua conexão", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i("TAG", e.getMessage());
                    Log.i("TAG", ( (FirebaseAuthException) e).getErrorCode());

                    String errorCode = ( (FirebaseAuthException) e).getErrorCode();

                    switch (errorCode){
                        case "ERROR_INVALID_EMAIL":
                            Toast.makeText(getContext(), "E-mail inválido. Verifique o formato", Toast.LENGTH_SHORT).show();
                            break;
                        case "ERROR_USER_NOT_FOUND":
                            Toast.makeText(getContext(), "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                            break;
                        case "ERROR_WRONG_PASSWORD":
                            Toast.makeText(getContext(), "Senha inválida", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };
    }
}