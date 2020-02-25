package com.ufpi.leevfrequency.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ufpi.leevfrequency.R;
import com.ufpi.leevfrequency.Utils.ConstantUtils;
import com.ufpi.leevfrequency.Utils.MethodUtils;

import java.util.HashMap;

public class FinalizeRegisterActivity extends AppCompatActivity {

    private EditText eEmail;
    private EditText ePassword;
    private EditText ePasswordAgain;
    private Button bFinalizeRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences prefs = null;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutPasswordAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_register);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(ConstantUtils.DATABASE_ACTUAL_BRANCH)
                .child(ConstantUtils.USERS_BRANCH);

        eEmail = findViewById(R.id.eEmail);
        ePassword = findViewById(R.id.ePassword);
        ePasswordAgain = findViewById(R.id.ePasswordAgain);
        bFinalizeRegister = findViewById(R.id.bFinalizeRegister);

        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutPasswordAgain = findViewById(R.id.textInputLayoutPasswordAgain);

        bFinalizeRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearErrorEmail();
                clearErrorPassword();
                clearErrorPasswordAgain();

                /* Verificar se algum dos campos está vazio */
                if(verifyEmptyFields()){
                    checkEmptyFields();
                }
                else{

                    /* Verificar se o campo de email possui o formato apropriado */
                    if(!MethodUtils.isEmailValid(eEmail.getText().toString())){
                        enableAndShowErrorEmail("O e-mail não está em um formato válido");
                    }
                    else{

                        /* Verificar se as senhas coincidem */
                        if(!ePassword.getText().toString().equals(ePasswordAgain.getText().toString())){
                            enableAndShowErrorPassword("As senhas não coincidem");
                            enableAndShowErrorPasswordAgain("As senhas não coincidem");
                        }
                        else{
                            mDatabase
                                    .orderByChild(ConstantUtils.USER_FIELD_EMAIL)
                                    .equalTo(eEmail.getText().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()){
                                                for(DataSnapshot d : dataSnapshot.getChildren()){

                                                    if((boolean) d.child(ConstantUtils.USER_FIELD_REGISTERFINALIZED).getValue()){
                                                        Toast.makeText(getContext(), "O seu registro já foi finalizado anteriormente", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{

                                                        /* Finalizar o cadastro */
                                                        String email = (String) d.child(ConstantUtils.USER_FIELD_EMAIL).getValue();
                                                        String id = d.getKey();
                                                        String name = (String) d.child(ConstantUtils.USER_FIELD_NAME).getValue();
                                                        int userType = d.child(ConstantUtils.USER_FIELD_USERTYPE).getValue(Integer.class);

                                                        prefs = getSharedPreferences("com.ufpi.leevfrequency", MODE_PRIVATE);
                                                        prefs.edit().putString(ConstantUtils.USER_FIELD_EMAIL, email).commit();
                                                        prefs.edit().putString(ConstantUtils.USER_FIELD_NAME, name).commit();
                                                        prefs.edit().putString(ConstantUtils.USER_FIELD_ID, id).commit();
                                                        prefs.edit().putInt(ConstantUtils.USER_FIELD_USERTYPE, userType).commit();

                                                    }
                                                }

                                                insertAndLoginUser();
                                            }
                                            else{
                                                Toast.makeText(getContext(), "Usuário não cadastrado", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                }
            }
        });
    }

    private void insertAndLoginUser(){

        mAuth.createUserWithEmailAndPassword(eEmail.getText().toString(), ePassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");

                            final FirebaseUser user = mAuth.getCurrentUser();

                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(getContext(),
                                                "E-mail de verificação enviado para " + user.getEmail(),
                                                Toast.LENGTH_SHORT).show();

                                        HashMap<String, Object> result = new HashMap<>();
                                        result.put(ConstantUtils.USER_FIELD_REGISTERFINALIZED, true);

                                        mDatabase
                                                .child(prefs.getString(ConstantUtils.USER_FIELD_ID, ""))
                                                .updateChildren(result);

                                    } else {

                                        Toast.makeText(getContext(),
                                                "Falhou em enviar o e-mail de verificação.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {

                            // If sign in fails, display a message to the user.
                            Log.i("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Autenticação falhou.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                if(e instanceof FirebaseNetworkException){
                    Toast.makeText(getContext(), "Verifique sua conexão", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i("TAG", e.getMessage());
                    Log.i("TAG", ( (FirebaseAuthException) e).getErrorCode());

                    String errorCode = ((FirebaseAuthException)e).getErrorCode();
                    Toast.makeText(getContext(), e.getMessage()+"/"+ ( (FirebaseAuthException) e).getErrorCode(), Toast.LENGTH_SHORT).show();

                    switch (errorCode){
                        case "ERROR_INVALID_EMAIL":
                            Toast.makeText(getContext(), "E-mail em formato inválido", Toast.LENGTH_SHORT).show();
                            //enableAndShowErrorEmail("E-mail em formato inválido");
                            break;
                        case "ERROR_WEAK_PASSWORD":
                            Toast.makeText(getContext(), "Senha fraca. Deve ter, no mínimo, 6 caracteres", Toast.LENGTH_SHORT).show();
                            //enableAndShowErrorPassword("Senha fraca. Deve ter, no mínimo, 6 caracteres");
                            //enableAndShowErrorPasswordAgain("Senha fraca. Deve ter, no mínimo, 6 caracteres");
                            break;
                        case "ERROR_EMAIL_ALREADY_IN_USE":
                            Toast.makeText(getContext(), "E-mail já está em uso", Toast.LENGTH_SHORT).show();
                            //enableAndShowErrorEmail("E-mail já está em uso");
                            break;
                    }
                }
            }
        });
    }

    private boolean verifyEmptyFields(){

        if(eEmail.getText().toString().isEmpty() ||
            ePassword.getText().toString().isEmpty() ||
            ePasswordAgain.getText().toString().isEmpty()){

            return true;
        }
        else{
            return false;
        }
    }

    private void checkEmptyFields(){

        String fieldNotEmpty = "Este campo não pode estar vazio";

        if(eEmail.getText().toString().isEmpty()){
            enableAndShowErrorEmail(fieldNotEmpty);
        }
        if(ePassword.getText().toString().isEmpty()){
            enableAndShowErrorPassword(fieldNotEmpty);
        }
        if(ePasswordAgain.getText().toString().isEmpty()){
            enableAndShowErrorPasswordAgain(fieldNotEmpty);
        }
    }

    private Context getContext(){
        return this;
    }

    private void enableAndShowErrorEmail(String errorMessage){
        textInputLayoutEmail.setErrorEnabled(true);
        textInputLayoutEmail.setError(errorMessage);
    }
    private void clearErrorEmail(){
        textInputLayoutEmail.setErrorEnabled(false);
        textInputLayoutEmail.setError(null);
    }

    private void enableAndShowErrorPassword(String errorMessage){
        textInputLayoutPassword.setErrorEnabled(true);
        textInputLayoutPassword.setError(errorMessage);
    }
    private void clearErrorPassword(){
        textInputLayoutPassword.setErrorEnabled(false);
        textInputLayoutPassword.setError(null);
    }

    private void enableAndShowErrorPasswordAgain(String errorMessage){
        textInputLayoutPasswordAgain.setErrorEnabled(true);
        textInputLayoutPasswordAgain.setError(errorMessage);
    }
    private void clearErrorPasswordAgain(){
        textInputLayoutPasswordAgain.setErrorEnabled(false);
        textInputLayoutPasswordAgain.setError(null);
    }
}
