package com.example.imhungry3.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.imhungry3.Activity.Login.LoginActivity;
import com.example.imhungry3.Activity.Register.RegisterActivity;
import com.example.imhungry3.BaseActivity;
import com.example.imhungry3.Fragment.FragmentNewRecipes;
import com.example.imhungry3.Fragment.FragmentTopRate;
import com.example.imhungry3.Fragment.FragmentTopWatch;
import com.example.imhungry3.R;
import com.example.imhungry3.Adapter.*;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
// latest verion
public class HomeActivity extends BaseActivity {
    Button LogOut_btn;
    static final String TAG = "HomeActivity";

    //          FireBase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    //
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FragmentTopRate topRate;
    private FragmentTopWatch topWatch;
    private FragmentNewRecipes newRecipes;
    ProgressDialog pd;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);
//        startActivity(new Intent(this, CreateRecipeActivity.class));
        rootView = getLayoutInflater().inflate(R.layout.activity_home, frameLayout);
        mAuth = FirebaseAuth.getInstance();
        checkRegisterComplite();

        //toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);//variable to display page fregment
        tabLayout = findViewById(R.id.tab_layout);//variable to display tabbs

        //class fregments
        topRate = new FragmentTopRate();
        topWatch = new FragmentTopWatch();
        newRecipes = new FragmentNewRecipes();

        tabLayout.setupWithViewPager(viewPager);

        //make adapter for all fregment and add all fregment to adapter
        AdapterViewPager viewPagerAdapter = new AdapterViewPager(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(topRate, getString(R.string.top_rate));
        viewPagerAdapter.addFragment(topWatch, getString(R.string.top_watch));
        viewPagerAdapter.addFragment(newRecipes, getString(R.string.new_recipes));
        viewPager.setAdapter(viewPagerAdapter);

        //set the number a tab and the icon
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_star_border_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_eyes);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_new);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();//get current user if already login
                if (mFirebaseUser == null) {
                    Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();

                }
            }
        };

    }



    private void checkRegisterComplite() {
        // Read from the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users");
        FirebaseUser user = mAuth.getCurrentUser();
        Query q = myRef.orderByKey().equalTo(user.getUid());
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {

                    Log.d(TAG, "Value is: here ");
                    Log.d(TAG, "Value is: here " + dataSnapshot);
                    startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
                    finish();
                    Log.d(TAG, "reg_flaggggggggggggggggggggggg =");
                } else {

                    Log.d(TAG, "Homeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ");
                    Log.d(TAG, "Value is: here " + dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onBackPressed() {
//        on back press shows pop up and ask for logOut
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            ExitApp();

        }
    }
    //    picker Dialog ask for Log Out on BackPress

    protected void ExitApp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());

        builder.setTitle("Log Out");
        builder.setMessage("Do You Want To LogOut?");
        builder.setIcon(R.drawable.logo);


        final AlertDialog dialog = builder.create();
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mAuth.signOut();

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

}