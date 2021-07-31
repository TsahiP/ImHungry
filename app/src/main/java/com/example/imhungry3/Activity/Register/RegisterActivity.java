package com.example.imhungry3.Activity.Register;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imhungry3.Activity.HomeActivity;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    int mYear, mMonth, mDay;
    EditText firstName,lastName,phone, city;
    TextView Email,ET_BDate;
    Button send;
    RadioButton woman,man;


//    firebase
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseUser fUser;
    DatabaseReference myReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        getSupportActionBar().hide();//hide action bar (top bar)
//===================DatePicker================================
        ET_BDate = findViewById(R.id.bdate_et_xml);
        ET_BDate.setOnClickListener(this);
//        ======================================================

//        register values
        firstName = findViewById(R.id.fname_et_xml);
        lastName = findViewById(R.id.lname_et_xml);
        Email = findViewById(R.id.email_et_xml);
        city = findViewById(R.id.gender_et_xml);
        phone=findViewById(R.id.phone_et_xml);
//        ======================================================
//        send btn
        send = findViewById(R.id.send_btn_xml);
        send.setOnClickListener(this);
//        =======================================================
//        radio Buttons
        man = findViewById(R.id.man_radio_xml);
        woman = findViewById(R.id.woman_radio_xml);

//        firebase user
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
//        set email into ET mail
        if (fUser != null){
            Email.setText(fUser.getEmail());
        }else {
            finish();
        }






    }

    @Override
    public void onClick(View v) {
        if (v == ET_BDate) {
            Calendar c = Calendar.getInstance().getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dp = new DatePickerDialog(this, new SetDate(), mYear, mMonth, mDay);
            dp.show();
        }

        if(v == send)
        {
            String gender;
//            check return values
            if(Email.getText().toString().isEmpty())
            {
                lastName.setError("please enter last email...");
                return;
            }
            if(firstName.getText().toString().isEmpty())
            {
                firstName.setError("please enter first name...");
                return;
            }
            if(lastName.getText().toString().isEmpty())
            {
                lastName.setError("please enter last name...");
                return;
            }
            if(ET_BDate.getText().toString().isEmpty())
            {
                Email.setError("Invalid Bdate...");
                return;
            }
            if(!man.isChecked()&&!woman.isChecked())
            {
                MyToast.showLong(getApplicationContext(),"Please Choose gender");
                return;
            }
            if(man.isChecked())
            {
                gender = "man";
            }else{
                gender = "woman";
            }


//            after all checks put the data into users table
//      set firebase
            database = FirebaseDatabase.getInstance();
            myReference = database.getReference("users");
            HashMap map = new HashMap<String,String>();
            map.put("email",Email.getText().toString());
            map.put("firstName",firstName.getText().toString());
            map.put("lastName",lastName.getText().toString());
            map.put("city",city.getText().toString());
            map.put("gender",gender);
            map.put("birthDay",ET_BDate.getText().toString());
            if (!phone.getText().toString().isEmpty())
            {
                map.put("phone",phone.getText().toString());
            }
            myReference.child(fUser.getUid()).setValue(map);
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));

        }
    }


    private class SetDate implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            ET_BDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);


        }
    }
}