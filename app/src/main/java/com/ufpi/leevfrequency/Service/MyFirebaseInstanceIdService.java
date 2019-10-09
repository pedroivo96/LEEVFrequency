package com.ufpi.leevfrequency.Service;

import android.content.SharedPreferences;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ufpi.leevfrequency.Utils.ConstantUtils;

import java.util.HashMap;
import java.util.logging.ConsoleHandler;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // Obter o novo InstanceID
        //String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        String firebaseToken = FirebaseInstanceId.getInstance().getId();
        /*
        //Actualizar na base de dados
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("utilizadores/"+uid+"/FirebaseToken")
                .setValue(firebaseToken);
        */

        SharedPreferences prefs = prefs = getSharedPreferences("com.ufpi.leevfrequency", MODE_PRIVATE);
        if(prefs.getString(ConstantUtils.USER_FIELD_INSTANCE_ID, "").length() > 0){

            String userId = prefs.getString(ConstantUtils.USER_FIELD_ID, "");

            HashMap<String, Object> result = new HashMap<>();
            result.put(ConstantUtils.USER_FIELD_INSTANCE_ID, firebaseToken);


            FirebaseDatabase.getInstance().getReference()
                    .child(ConstantUtils.USERS_BRANCH)
                    .child(userId)
                    .updateChildren(result);
        }
    }
}
