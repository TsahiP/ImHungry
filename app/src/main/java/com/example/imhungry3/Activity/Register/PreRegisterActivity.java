package com.example.imhungry3.Activity.Register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imhungry3.Activity.Login.LoginActivity;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PreRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth firebaseAuth;
    Button nextStep;
    EditText email, pw, rePw;
    TextView moveToRegister;
    public static final int PASSWORD_LENGTH = 8;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_register);
        email = findViewById(R.id.EmailPut);
        pw = findViewById(R.id.PassPut);
        rePw = findViewById(R.id.RePassPut);
        moveToRegister = findViewById(R.id.moveToLogin);
        moveToRegister.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        nextStep = findViewById(R.id.nextStep);
        nextStep.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == moveToRegister) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (v == nextStep) {
            String tempPw = rePw.getText().toString(), tempPw2 = pw.getText().toString();
            if (tempPw.equals(tempPw2)) {
                if (is_Valid_Password(tempPw)) {
                    firebaseAuth = FirebaseAuth.getInstance();
                    String emailToSave = email.getText().toString(), pass = pw.getText().toString();
                    firebaseAuth.createUserWithEmailAndPassword(emailToSave, pass)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("", "signInWithEmail:success");
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        nextStep();
//                                updateUI(user);
                                        nextStep();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("", "signInWithEmail:failure", task.getException());
                                        MyToast.showLong(getApplicationContext(),"Authentication failed");

                                        // ...
                                    }

                                    // ...
                                }
                            });

                }else
                {
                    MyToast.showLong(getApplicationContext(),
                            "1. A password must have at least eight characters.\n" +
                            "2. A password consists of only letters and digits.\n" +
                            "3. A password must contain at least two digits \n" +
                            "Input a password (You are agreeing to the above Terms and Conditions.): ");
                }
            }else
            {
                MyToast.showLong(getApplicationContext(),"The passwords do not match");
            }
        }

    }

    public void nextStep() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public static boolean is_Valid_Password(String password) {

        if (password.length() < PASSWORD_LENGTH) return false;

        int charCount = 0;
        int numCount = 0;
        for (int i = 0; i < password.length(); i++) {

            char ch = password.charAt(i);

            if (is_Numeric(ch)) numCount++;
            else if (is_Letter(ch)) charCount++;
            else return false;
        }


        return (charCount >= 2 && numCount >= 2);
    }

    public static boolean is_Letter(char ch) {
        ch = Character.toUpperCase(ch);
        return (ch >= 'A' && ch <= 'Z');
    }

    public static boolean is_Numeric(char ch) {

        return (ch >= '0' && ch <= '9');
    }
}