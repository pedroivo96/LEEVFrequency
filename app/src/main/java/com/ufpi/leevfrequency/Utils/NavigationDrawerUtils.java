package com.ufpi.leevfrequency.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.ufpi.leevfrequency.R;
import com.ufpi.leevfrequency.View.LEEVStudentsActivity;
import com.ufpi.leevfrequency.View.LEEVTeachersActivity;
import com.ufpi.leevfrequency.View.LoginActivity;
import com.ufpi.leevfrequency.View.MyStudentsActivity;
import com.ufpi.leevfrequency.View.UserActivity;
import com.ufpi.leevfrequency.View.UserRegisterActivity;

public class NavigationDrawerUtils {

    public static NavigationView.OnNavigationItemSelectedListener getNavigationDrawerItemSelectedListener(final Context context, final int userType, final DrawerLayout drawerLayout){
        return new NavigationView.OnNavigationItemSelectedListener() {

            public void finishCurrentActivity(){
                if(! (context instanceof UserActivity))
                    ((Activity) context).finish();
            }

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(userType == ConstantUtils.USER_TYPE_TEACHER){

                    //É um professor
                    switch (item.getItemId()) {

                        case R.id.nav_my_profile: {
                            if(! (context instanceof UserActivity)) {
                                Intent intent = new Intent(context, UserActivity.class);
                                context.startActivity(intent);
                                finishCurrentActivity();
                            }
                            break;
                        }
                        case R.id.nav_my_students:{

                            Intent intent = new Intent(context, MyStudentsActivity.class);
                            context.startActivity(intent);

                            break;
                        }
                        case R.id.nav_user_register:{

                            Intent intent1 = new Intent(context, UserRegisterActivity.class);
                            context.startActivity(intent1);
                            finishCurrentActivity();
                            break;
                        }
                        case R.id.nav_leev_students:{

                            Intent intent = new Intent(context, LEEVStudentsActivity.class);
                            context.startActivity(intent);
                            break;
                        }
                        case R.id.nav_leev_teachers:{

                            Intent intent = new Intent(context, LEEVTeachersActivity.class);
                            context.startActivity(intent);
                            break;
                        }
                        case R.id.nav_logout:{

                            FirebaseAuth.getInstance().signOut();
                            Intent intent2 = new Intent(context, LoginActivity.class);
                            context.startActivity(intent2);
                            finishCurrentActivity();

                            break;
                        }

                        default: {
                            //Toast.makeText(this, "Menu Default", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
                else{

                    //É um aluno
                    switch (item.getItemId()) {

                        case R.id.nav_my_profile: {
                            if(! (context instanceof UserActivity)) {
                                Intent intent = new Intent(context, UserActivity.class);
                                context.startActivity(intent);
                                finishCurrentActivity();
                            }
                            break;
                        }
                        case R.id.nav_my_frequencies:{
                            break;
                        }
                        case R.id.nav_see_advisor:{
                            break;
                        }
                        case R.id.nav_leev_teachers:{

                            Intent intent = new Intent(context, LEEVTeachersActivity.class);
                            context.startActivity(intent);
                            break;
                        }
                        case R.id.nav_leev_students:{

                            Intent intent = new Intent(context, LEEVStudentsActivity.class);
                            context.startActivity(intent);
                            break;
                        }
                        case R.id.nav_logout:{

                            FirebaseAuth.getInstance().signOut();
                            Intent intent2 = new Intent(context, LoginActivity.class);
                            context.startActivity(intent2);
                            finishCurrentActivity();

                            break;
                        }

                        default: {
                            //Toast.makeText(this, "Menu Default", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        };
    }

}


