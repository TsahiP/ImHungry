package com.example.imhungry3.Activity.Login;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//facebook sign in

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.imhungry3.Activity.HomeActivity;
import com.example.imhungry3.Activity.Register.PreRegisterActivity;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mEmail;
    EditText mPassword;
    Button mLogin_btn;
    TextView mMoveToReg,forget_pw;
    static final String TAG = "LoginActivity";

    boolean Reg_flag = false;

    //FB auth
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
//============================
    ProgressDialog pd;
    //google sign - in
    private static final int RC_SIGN_IN=100;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton mGoogleLogin_btn;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
//    =========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getSupportActionBar().hide();//hide action bar (top bar)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        Firebase
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        forget_pw = findViewById(R.id.forget_password);
        forget_pw.setOnClickListener(this);
        mEmail=findViewById(R.id.EmailPut);
        mPassword=findViewById(R.id.PassPut);
        mLogin_btn=findViewById(R.id.LoginButton);
        mMoveToReg=findViewById(R.id.MoveToReg);
        mGoogleLogin_btn =findViewById(R.id.googleSignInButton);
        pd =  new ProgressDialog(this);
        pd.setMessage("Loggin in ...");
        //LoginButton
        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mEmail.getText().toString();
                String pass=mPassword.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //invaild email
                    mEmail.setError("invalid Email");
                    mEmail.setFocusable(true);
                }else{
                    loginUser(email,pass);
                }

            }


        });


        //not have account (have to make a String : not have account ? want sign up?
        mMoveToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PreRegisterActivity.class));
                finish();
            }
        });
//        ==========================================================================

        //google log in
        mGoogleLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //begin google login
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                pd.show();
            }
        });



        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();//get current user if already login
                if(mFirebaseUser != null){
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };

    }

    private void loginUser(String email, String pass) {


        pd.show();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            pd.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            finish();
                        }
                        if (task.isCanceled())
                        {

                            MyToast.showLong(LoginActivity.this, "The user or password is invalid");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                MyToast.showLong(LoginActivity.this, "The user or password is invalid");
                //error
//                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();//get current user if already login
                if(mFirebaseUser != null){
//                    Toast.makeText(LoginActivity.this,"You are logged in ",Toast.LENGTH_SHORT).show();

                }else{
                    Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(i);
                    finish();

                }
            }
        };
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                // ...
                pd.dismiss();

            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            finish();
                        } else {
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onClick(View v) {
        if (v == forget_pw)
        {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);

            View mView = getLayoutInflater().inflate(R.layout.forget_password_dialog, null);
            Button send = mView.findViewById(R.id.send_et);
            EditText userEmail = mView.findViewById(R.id.email_et);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!userEmail.getText().toString().contains("@"))
                    {
                        MyToast.showLong(LoginActivity.this, "The email is invalid");
                    }else
                    {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String emailAddress = userEmail.getText().toString();

                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Email sent.");
                                            MyToast.showLong(LoginActivity.this, "The email has been sent");
                                            dialog.dismiss();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                MyToast.showLong(LoginActivity.this, "The email is invalid");
                            }
                        });
                    }
                }
            });
            dialog.show();

        }

    }
}
