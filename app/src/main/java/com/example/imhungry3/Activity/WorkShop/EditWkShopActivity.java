package com.example.imhungry3.Activity.WorkShop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.Class.WorkShopClass;
import com.example.imhungry3.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EditWkShopActivity extends AppCompatActivity  implements View.OnClickListener{
    private Button startTimeBtn, endTimeBtn, BTN_Date, chosePic_BTN;
    private FloatingActionButton send_btn;
    private ImageButton back;
    private int mYear, mMonth, mDay, imageCount = 1;
    private ProgressDialog pd;
    private String id, StartTime, EndTime;
    private TextInputLayout ClassName_ET, Descraption_ET, MaxGuest_ET, Location_ET, Price_ET,whatPrepear_ET;
    private final int REQUEST_EXTERNAL_STORAGE = 100;
    private ImageView imageTest;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ClipData clipData;
    private ImageButton backPage;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private WorkShopClass wk;
    //    FireBase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    CardView cardView ;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wk_shop);

        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            wk = (WorkShopClass) bundle.getSerializable("wk");
        }
        back = findViewById(R.id.back_create_workshop);
        send_btn = findViewById(R.id.send_btn);
        send_btn.setOnClickListener(this);
        back.setOnClickListener(this);
        ProgressDialog pd = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        whatPrepear_ET = findViewById(R.id.what_prepear2);
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
        image = findViewById(R.id.classImage);
//        =================picture picker ==================
        chosePic_BTN = findViewById(R.id.ChosePic_btn);
        chosePic_BTN.setOnClickListener(this);
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("workShopImages");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading image/s....");

        showClassDetails(wk);
    }
    @Override
    public void onClick(View v) {

        if(v == back)
        {
            super.onBackPressed();
        }

        if (chosePic_BTN == v) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
            DatePickerDialog dp = new DatePickerDialog(this, R.style.TimePickerTheme, new SetDate(), mYear, mMonth, mDay);
            dp.show();
        }
        if (v == startTimeBtn) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            String txtTime;
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.TimePickerTheme,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            String tempMin = null;
                            if (minute < 10) {
                                tempMin = "0" + minute;
                            }
                            else
                            {
                                tempMin = "" + minute;
                            }
                            startTimeBtn.setText(hourOfDay + ":" + tempMin);
                            StartTime = hourOfDay + ":" + tempMin;
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }


        if (v == endTimeBtn) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            String txtTime;
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.TimePickerTheme,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            String tempMin = null;
                            if(minute < 10)
                            {
                                tempMin = "0" + minute;
                            }
                            else
                            {
                                tempMin = "" + minute;
                            }
                            endTimeBtn.setText(hourOfDay + ":" + tempMin);
                            EndTime = hourOfDay + ":" + tempMin;
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }

//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("classess").child("lastClassId");
////        myRef.setValue(0);
        if (send_btn == v) {
            boolean flag = true;
            if (ClassName_ET.getEditText().getText().toString().isEmpty()) {
                flag = false;
                ClassName_ET.setError("Please enter workshop name");
                return;
            }
            if (Descraption_ET.getEditText().getText().toString().isEmpty()) {
                flag = false;
                Descraption_ET.setError("Please enter description ");
                return;
            }
            if (MaxGuest_ET.getEditText().getText().toString().isEmpty() && MaxGuest_ET.getEditText().getText().toString().equals("0")) {
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
                startTimeBtn.setError("Choose a time to start the workshop");
                return;
            }
            if (endTimeBtn.length() == 0) {
                flag = false;
                startTimeBtn.setError("Select a time for the end of the workshop");
                return;
            }

            if (Location_ET.getEditText().getText().toString().isEmpty()) {
                flag = false;
                Location_ET.setError("Choose a location for the workshop");
                return;
            }
            if (Price_ET.getEditText().getText().toString().isEmpty()) {
                flag = false;
                Price_ET.setError("Choose a price for the workshop");
                return;
            }
            if (whatPrepear_ET.getEditText().getText().toString().isEmpty()) {
                flag = false;
                whatPrepear_ET.setError("what you will prepare in the workshop");
                return;
            }
            insertClass(wk.getWkId());
//            =====uploading picture/s===========================
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {

                    Uri imageUri = clipData.getItemAt(i).getUri();
                    uploadPicture(imageUri);
                }
            } else {
                if (imageUri != null) {
                    uploadPicture(imageUri);

                }
            }
//==============================================================
            Intent intent = new Intent(EditWkShopActivity.this, WorkShopActivity.class);
            startActivity(intent);
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


    //    upload image
    public void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
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
                    MyToast.showShort(EditWkShopActivity.this, "Permission denied check permission in phone settings");
                }
                return;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK) {

            final List<Bitmap> bitmaps = new ArrayList<>();
            if (data.getData() != null) {
                imageUri = data.getData();
                //add your drawable here like this image.setImageResource(R.drawable.redeight)or set like this imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                Glide.with(this).load(imageUri).into(image);

            } else {
                MyToast.showShort(EditWkShopActivity.this, "You need to select one image");
            }
        }
    }

    public void uploadPicture(Uri imageUri) {
        progressDialog.show();
//        final String randomkey = UUID.randomUUID().toString();
        final String randomkey = "1";
        StorageReference riversRef = storageReference.child(wk.getWkId());
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
//                        Toast.makeText(this, "Seccess", Toast.LENGTH_SHORT).show();
                        imageCount += 1;
                        if (clipData == null || clipData.getItemCount() == imageCount) {
                            progressDialog.dismiss();
//                            startActivity(new Intent(EditWkShopActivity.this, WorkShopActivity.class));
//                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        MyToast.showShort(EditWkShopActivity.this, "Error");

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progressPrecent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Image: " + imageCount + " :" + (int) progressPrecent + "%");
            }
        });
    }
    public void showClassDetails(WorkShopClass wk){
        //        get intent data
        wk.setWkPrice(wk.getWkPrice().replace("₪",""));
        wk.setWkPrice(wk.getWkPrice().replace(" ",""));
        ClassName_ET.getEditText().setText(wk.getWkName());
        Descraption_ET.getEditText().setText(wk.getWkDescraption());
        MaxGuest_ET.getEditText().setText(wk.getWkMaxGuests());
        startTimeBtn.setText(wk.getWkStartTime());
        endTimeBtn.setText(wk.getWkEndTime());
        Location_ET.getEditText().setText(wk.getWkLocation());
        Price_ET.getEditText().setText(wk.getWkPrice());
        BTN_Date.setText(wk.getWkDate());
        StartTime = wk.getWkStartTime();
        EndTime = wk.getWkEndTime();
        whatPrepear_ET.getEditText().setText(wk.getWkWhatPrepare());
        String uri = wk.getWkPicUrl();
//        Glide.with(this).load(wk.getWkPicUrl()).into(image);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.image_workshop);
        try {
            Glide.with(this).setDefaultRequestOptions(requestOptions)
                    .load(uri)
                    .into(image);
        } catch (Exception e) {

        }
    }
    public void insertClass(String id) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("classess");
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
        map.put("id", fUser.getUid());
        map.put("whatPrepear", whatPrepear_ET.getEditText().toString());
        myRef = database.getReference("classess");
        myRef.child(id).setValue(map);

    }

    public interface uploadImageCallBack {
        void onCallback(String uri);
    }
}
