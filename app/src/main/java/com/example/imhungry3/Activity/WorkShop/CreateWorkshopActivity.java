package com.example.imhungry3.Activity.WorkShop;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.imhungry3.BaseActivity;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CreateWorkshopActivity extends AppCompatActivity implements View.OnClickListener {

    private Button startTimeBtn, endTimeBtn, BTN_Date, chosePic_BTN;
    private FloatingActionButton send_btn;
    private int mYear, mMonth, mDay, imageCount = 1;
    private ProgressDialog pd;
    private String lastClassId, StartTime, EndTime;
    private TextInputLayout ClassName_ET, Descraption_ET, MaxGuest_ET, Location_ET, Price_ET, whatPrepear_ET;
    private final int REQUEST_EXTERNAL_STORAGE = 100;
    private ImageView workshopImage;
    private FirebaseStorage storage;
    private FirebaseUser user;
    private StorageReference storageReference;
    private Uri imageUriToUpload;
    private TimePickerDialog timePickerDialog;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private ImageButton backPage;
    Calendar c1, c2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workshop);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
//        View rootView = getLayoutInflater().inflate(R.layout.activity_create_workshop, frameLayout);
        send_btn = findViewById(R.id.send_btn);
        send_btn.setOnClickListener(this);
        ProgressDialog pd = new ProgressDialog(CreateWorkshopActivity.this, ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        CheckLastId();
        whatPrepear_ET = findViewById(R.id.what_prepear);
        backPage = findViewById(R.id.back_create_workshop);
        backPage.setOnClickListener(this);
        //===================DatePicker================================
        BTN_Date = findViewById(R.id.BTN_Date);
        BTN_Date.setOnClickListener(this);
//        ===================TimePicker================================
        startTimeBtn = findViewById(R.id.startTime_ET);
        startTimeBtn.setOnClickListener(this);
        endTimeBtn = findViewById(R.id.endTime_ET);
        endTimeBtn.setOnClickListener(this);
//        ======================================================
        ClassName_ET = findViewById(R.id.workshop_name_ET);

        Descraption_ET = findViewById(R.id.Descraption_ET);
        MaxGuest_ET = findViewById(R.id.pplAmount_ET);
        Location_ET = findViewById(R.id.Location_ET);
        Price_ET = findViewById(R.id.price_ET);
//        =================picture picker ==================
        workshopImage = findViewById(R.id.workshopImage);
        chosePic_BTN = findViewById(R.id.ChosePic_btn);
        chosePic_BTN.setOnClickListener(this);
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("workShopImages");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading image/s....");


    }


//================================imagePicker====================================

    public void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    launchGalleryIntent();
                } else {
                    // permission denied
                    MyToast.showShort(getApplicationContext(), "Permission denied check permission in phone settings");
                }
                return;
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * onActivityResult- android default function ->  on image select we check the request code to make sure this is what we looking for (specific request )
         * RESULT_OK -> check if hungry app have premission to join phone library
         * imageUriToUpload -> after checks - take image path
         */
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK) {

            final List<Bitmap> bitmaps = new ArrayList<>();
            imageUriToUpload = data.getData();


            if (imageUriToUpload == null) {

                return;


            } else if (imageUriToUpload != null) {

                workshopImage.setImageURI(imageUriToUpload);

            } else {
                MyToast.showShort(getApplicationContext(), "Error");
            }
        }
    }

    public void uploadPicture(Uri imageUri) {
        /**
         * uploadPicture - this function get path of picture
         * and upload it to firebase Storage
         * imageUri -> handle the path of the image
         * riversRef -> handle the path of where we wanna put the image in Firebase Storage
         */
        progressDialog.show();
        StorageReference riversRef = storageReference.child(lastClassId);
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        MyToast.showShort(getApplicationContext(), "Seccess");
                        if (imageUriToUpload == null) {
                            progressDialog.dismiss();
                            startActivity(new Intent(CreateWorkshopActivity.this, WorkShopActivity.class));
                            ((Activity) (getBaseContext())).finish();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        MyToast.showShort(getApplicationContext(), "Error");

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progressPrecent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Image: " + imageCount + " :" + (int) progressPrecent + "%");
            }
        });
    }

//===============================================================================


    public void CheckLastId() {
        /**
         * CheckLastId -> check the last index id of active workshops before we upload new one
         * listen to the server and update the last when change in real time .
         * lastClassId -> static value - handle the last id
         *
         */
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("classess");
        myRef.child("lastClassId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                lastClassId = value;
                Log.d("here:", "" + lastClassId);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void insertClass() {

        /**
         * insertClass - on submit this function takes all the info check it and upload it to the server
         * all the details came from the inputs and put it to one hashmap .
         */
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("classess");
        CheckLastId();
//        put the Data into a hashMap
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("email", fUser.getEmail().toString());
        map.put("ClassName", ClassName_ET.getEditText().getText().toString());
        map.put("Descraptions", Descraption_ET.getEditText().getText().toString());
        map.put("MaxGuests", MaxGuest_ET.getEditText().getText().toString());
        map.put("Registerd", "0");
        map.put("StartTime", StartTime);
        map.put("EndTime", EndTime);
        map.put("Location", Location_ET.getEditText().getText().toString());
        map.put("Price", Price_ET.getEditText().getText().toString());
        map.put("Date", BTN_Date.getText().toString());
        map.put("id", user.getUid());
        map.put("whatPrepear", whatPrepear_ET.getEditText().getText().toString());
        lastClassId = (Integer.parseInt(lastClassId) + 1) + "";
        Log.d("lastId:", "" + lastClassId);
        myRef = database.getReference("classess");
        myRef.child(lastClassId).setValue(map);
        myRef.child("lastClassId").setValue(lastClassId);

    }

    @Override
    public void onClick(View v) {


        if (chosePic_BTN == v) {
            if (ActivityCompat.checkSelfPermission(CreateWorkshopActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CreateWorkshopActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                    return;
            } else {
                launchGalleryIntent();
            }

        }
        if (v == BTN_Date) {
            Calendar c = Calendar.getInstance().getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dp = new DatePickerDialog(this, R.style.TimePickerTheme, new CreateWorkshopActivity.SetDate(), mYear, mMonth, mDay);
            dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dp.show();
        }
        if (v == startTimeBtn) {
            // Get Current Time
            c1 = Calendar.getInstance();
            int mHour = c1.get(Calendar.HOUR_OF_DAY);
            int mMinute = c1.get(Calendar.MINUTE);

            String txtTime;
            // Launch Time Picker Dialog
            timePickerDialog = new TimePickerDialog(this, R.style.TimePickerTheme,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            String tempMin = null;
                            if (minute < 10) {
                                tempMin = "0" + minute;
                            } else {
                                tempMin = "" + minute;
                            }
                            startTimeBtn.setText(hourOfDay + ":" + tempMin);
                            StartTime = hourOfDay + ":" + tempMin;
                            c1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c1.set(Calendar.MINUTE, Integer.parseInt(tempMin));
                            if (StartTime != null) {
                                endTimeBtn.setEnabled(true);
                            }
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();


        }

        if (v == endTimeBtn && !startTimeBtn.getText().toString().equals("START TIME")) {
            // Get Current Time
            c2 = Calendar.getInstance();
            int mHour = c2.get(Calendar.HOUR_OF_DAY);
            int mMinute = c2.get(Calendar.MINUTE);
            c2.set(Calendar.HOUR_OF_DAY, mHour);
            c2.set(Calendar.MINUTE, mMinute);

            String txtTime;
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.TimePickerTheme,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            String startTime = startTimeBtn.getText().toString();
                            String[] split = startTime.split(":");

                            if (hourOfDay >= Integer.parseInt(split[0]) && minute >= Integer.parseInt(split[1])) {

                                String tempMin = null;
                                if (minute < 10) {
                                    tempMin = "0" + minute;
                                } else {
                                    tempMin = "" + minute;
                                }

                                endTimeBtn.setText(hourOfDay + ":" + tempMin);
                                EndTime = hourOfDay + ":" + tempMin;
                            } else {
                                MyToast.showShort(CreateWorkshopActivity.this, "Error");
                            }
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }

//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("classess").child("lastClassId");
////        myRef.setValue(0);
        if (backPage == v) {
            onBackPressed();
        }
        if (send_btn == v) {
            boolean flag = true;
            if (ClassName_ET.getEditText().getText().toString().isEmpty()) {
                flag = false;
                ClassName_ET.setError("Please enter workshop name");
                return;
            }
            if (Descraption_ET.getEditText().getText().toString().isEmpty()) {
                flag = false;
                Descraption_ET.setError("Please enter description");
                return;
            }
            if (MaxGuest_ET.getEditText().getText().toString().isEmpty() && MaxGuest_ET.getEditText().getText().toString().toString().equals("0")) {
                flag = false;
                MaxGuest_ET.setError("Choose how many people could come to the workshop");
                return;
            }


            if (BTN_Date == null) {
                flag = false;
                BTN_Date.setError("Choose a date for the workshop");
                return;
            }
            if (startTimeBtn.length() == 0) {
                flag = false;
                MyToast.showShort(CreateWorkshopActivity.this, "Choose a time to start the workshop");
                startTimeBtn.setError("");
                return;
            }
            if (endTimeBtn.length() == 0) {
                flag = false;
                MyToast.showLong(CreateWorkshopActivity.this, "Select a time for the end of the workshop");
                startTimeBtn.setError("");
                return;
            }

            if (Location_ET.getEditText().getText().toString().toString().isEmpty()) {
                flag = false;
                Location_ET.setError("please enter WorkShop Location");
                MyToast.showLong(CreateWorkshopActivity.this, "Choose a location for the workshop");

                return;
            }
            if (Price_ET.getEditText().getText().toString().toString().isEmpty()) {
                flag = false;
                Price_ET.setError("please enter WorkShop Price");
                MyToast.showShort(CreateWorkshopActivity.this, "Choose a price for the workshop");
                return;
            }
            insertClass();
//            =====uploading picture/s===========================
            if (imageUriToUpload != null) {
                uploadPicture(imageUriToUpload);

            } else {
                if (imageUri != null) {
                    uploadPicture(imageUri);

                }
            }
//==============================================================
            Intent intent = new Intent(this, WorkShopActivity.class);
            finish();
        }

        if (v == backPage) {
            onBackPressed();
        }
    }


    private class SetDate implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            BTN_Date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);


        }
    }


}