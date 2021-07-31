package com.example.imhungry3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.imhungry3.Activity.Recipe.CreateRecipeActivity;
import com.example.imhungry3.Activity.WorkShop.CreateWorkshopActivity;
import com.example.imhungry3.Activity.HomeActivity;
import com.example.imhungry3.Activity.UserPlace.PersonalAreaActivity;
import com.example.imhungry3.Activity.Search.SearchActivity;
import com.example.imhungry3.Activity.Setting.SettingActivity;
import com.example.imhungry3.Activity.WorkShop.WorkShopActivity;
import com.example.imhungry3.Activity.Category.RecipeCategoryActivity;
import com.example.imhungry3.Activity.Chat.ChatActivity;
import com.example.imhungry3.Class.MyToast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected FrameLayout frameLayout;
    private Context context;
    private Toolbar toolbar;
    public TextView txt_username, txt_email, txt_change_pass;
    public ImageView img_menuTitle, img_menuOption, image_profile;
    SignInButton googleSignInBtn;
    EditText email_delAcc , pass_delAcc ;
    private Button moveToSettingBtn;

    private DrawerLayout drawer;
    private static final int INTENT_REQUEST_CODE = 200;
    private int INTENT_CAMERA_CODE = 100;
    ActionBarDrawerToggle toggle;
    //    fire base to take user data
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    String Email, Name;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        context = this;
        initView();
        frameLayout = (FrameLayout) findViewById(R.id.container);
        pd = new ProgressDialog(this);
        pd.setMessage("LogOut...");


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        img_menuTitle = findViewById(R.id.im_hungry_bar_title);
        img_menuOption = findViewById(R.id.img_menuOption);
        img_menuOption.setBackgroundResource(R.drawable.menu);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);

//        set user data
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        Email = firebaseUser.getEmail();
//        Name = firebaseUser.getDisplayName();
        txt_username = headerview.findViewById(R.id.txt_username);
//        txt_username.setText(Name);
        txt_email = headerview.findViewById(R.id.txt_email);
        txt_email.setText(Email);
        image_profile = headerview.findViewById(R.id.image_profile);
//        לא עובד משום מה לבדוק בהמשך פיתרון אפשרי
//        Log.d("here",firebaseUser.getPhotoUrl().toString());
//        Uri uri = firebaseUser.getPhotoUrl();
//        image_profile.setImageURI(uri);


        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        img_menuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("firstName").exists())
                {
                    Name = snapshot.child("firstName").getValue().toString();
                }
                if (snapshot.child("lastName").exists())
                {
                    Name += " "+snapshot.child("lastName").getValue().toString();
                    txt_username.setText(Name);
                }


                if (snapshot.child("profilePicture").exists())
                {
                    Glide.with(BaseActivity.this).load(snapshot.child("profilePicture").getValue().toString()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), PersonalAreaActivity.class));

//                Toast.makeText(context, "move to user page", Toast.LENGTH_SHORT).show();
//                :)ההמשך יבוא כשיהיה עמוד אישי
            }
        });
    }


////        for 3 dots inside the toolbar remove the notes
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.base, menu);
//        return false;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        item.setVisible(false);
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    //    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.nav_home) {
//            Toast.makeText(context, "You Click On Home", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            // Handle the camera action

        } else if (id == R.id.WorkShop) {
            startActivity(new Intent(this, WorkShopActivity.class));
        } else if (id == R.id.Search_menu) {
            startActivity(new Intent(this, SearchActivity.class));
        } else if (id == R.id.WorkShopCreate) {
            startActivity(new Intent(this, CreateWorkshopActivity.class));

        } else if (id == R.id.LogOut_btn) {
            mAuth.signOut();
            finish();
        } else if (id == R.id.Chat) {
            startActivity(new Intent(this, ChatActivity.class));
        } else if (id == R.id.createRecipe) {
            startActivity(new Intent(this, CreateRecipeActivity.class));
        }
        else if (id == R.id.setting_btn) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(BaseActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.setting_dialog, null);
            mBuilder.setView(mView);
            email_delAcc = mView.findViewById(R.id.email_et);
                    pass_delAcc = mView.findViewById(R.id.pass_et);
                    moveToSettingBtn = mView.findViewById(R.id.move_to_setting_btn);
            googleSignInBtn = mView.findViewById(R.id.google_reAuth_btn);

            final AlertDialog dialog = mBuilder.create();
            for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("password")) {
                    email_delAcc.setVisibility(View.VISIBLE);
                    pass_delAcc.setVisibility(View.VISIBLE);
                    moveToSettingBtn.setVisibility(View.VISIBLE);
                    googleSignInBtn.setVisibility(View.GONE);
                 }else {
                    if (user.getProviderId().equals("google.com")) {
                        googleSignInBtn.setVisibility(View.VISIBLE);

                    }
                }
            }
            moveToSettingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tmpEmail = email_delAcc.getText().toString();
                    String tmpPass = pass_delAcc.getText().toString();
                    if (!tmpEmail.equals("") && !tmpPass.equals("")) {

                        AuthCredential credential = EmailAuthProvider
                                .getCredential(tmpEmail, tmpPass);
                        firebaseUser.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Log.d("TAG", "User re-authenticated.");
                                            startActivity(new Intent(BaseActivity.this, SettingActivity.class));
                                            dialog.dismiss();
                                        }
//                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                MyToast.showShort(BaseActivity.this, "The user or password are invalid");
                                return;
                            }
                        });

                    }else
                    {
                        MyToast.showShort(BaseActivity.this, "The email or password are invalid");
                        return;
                    }
                }
            });
            googleSignInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(BaseActivity.this);
                    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "Reauthenticated.");
                                startActivity(new Intent(BaseActivity.this, SettingActivity.class));
                                dialog.dismiss();
                            }
                        }
                    });
                }
            });
            dialog.show();

//            startActivity(new Intent(this, CreateRecipeActivity.class));
        } else if (id == R.id.category) {
            startActivity(new Intent(this, RecipeCategoryActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
