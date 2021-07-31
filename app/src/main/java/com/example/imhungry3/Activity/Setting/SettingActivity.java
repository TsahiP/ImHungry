package com.example.imhungry3.Activity.Setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    Button delBtn,changePw;
    private ImageButton back;
    public static final int PASSWORD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        delBtn = findViewById(R.id.del_acc);
        changePw = findViewById(R.id.changeing_pw);
        back = findViewById(R.id.back_setting);
        back.setOnClickListener(this);
        changePw.setOnClickListener(this);
        delBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == delBtn){
            startActivity(new Intent(SettingActivity.this, SplashDeleteAccActivity.class));
            finishAffinity();
        }
        if (v == back){
            onBackPressed();
        }
        if (v == changePw)
        {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.setting_change_password_dialog, null);
            Button save = mView.findViewById(R.id.save_btn);
            EditText pass = mView.findViewById(R.id.pass_et), rePass = mView.findViewById(R.id.repass_et);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tmpPass = pass.getText().toString() , tmpRePass = rePass.getText().toString();
                    if (tmpPass.equals(tmpRePass) )
                    {
                        if (is_Valid_Password(tmpPass))
                        {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updatePassword(tmpPass)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG", "User password updated.");
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                        }else{
                            MyToast.showLong(getApplicationContext(),
                                    "1. A password must have at least eight characters.\n" +
                                    "2. A password consists of only letters and digits.\n" +
                                    "3. A password must contain at least two digits \n" +
                                    "Input a password (You are agreeing to the above Terms and Conditions.): ");

                        }
                    }
                }
            });

            dialog.show();
        }
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