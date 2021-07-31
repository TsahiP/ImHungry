package com.example.imhungry3.Activity.UserPlace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.Adapter.AdapterTop;
import com.example.imhungry3.BaseActivity;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class PersonalAreaUserActivity extends BaseActivity implements View.OnClickListener {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText fName, lName, email, bDay, city, gender,phone;//must to have
    private TextView title;
    private ImageView img_profie,editProfile,editProfilePicture;;
    private boolean edit_flag = false;
    private FloatingActionButton bookOrInfo;
    private ListView myRecipe;
    private LinearLayout linearInfo, linearName, linearList, llPhone;

    private ArrayList<String> idRecipes;
    private ArrayList<String> titleRecipes;
    private ArrayList<String> descriptionRecipes;
    private ArrayList<String> watch;
    private ArrayList<String> like;
    private ArrayList<String> imageRecipes;
    private AdapterTop adapterTop;
    private ListView listView;

    //    upload image
    final int REQUEST_EXTERNAL_STORAGE = 10;
    private ProgressDialog progressDialog;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri profileImageUri;


    private boolean ifIsCreator = false;
    private String intentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_personal_area);
        Bundle bundle = getIntent().getExtras();
        View rootView = getLayoutInflater().inflate(R.layout.activity_personal_area_user, frameLayout);
        LayoutInflater inflater = LayoutInflater.from(new ContextThemeWrapper(getApplicationContext(), R.style.AppTheme));
        fName = findViewById(R.id.first_name_profile);
        //            street
        lName = findViewById(R.id.last_name_profile);
        email = findViewById(R.id.email_profile);
        city = findViewById(R.id.city_profile);
        gender = findViewById(R.id.gendar_profile);
        mAuth = FirebaseAuth.getInstance();
        email.setText(mAuth.getCurrentUser().getEmail());
        bDay = findViewById(R.id.date_profile);
        img_profie = findViewById(R.id.profile_image);
        title = findViewById(R.id.name_profile);
        phone = findViewById(R.id.phone_et_xml);
        getUserData();
        bookOrInfo = findViewById(R.id.bookOrInfo);
//        edit btn

        bookOrInfo.setOnClickListener(this);
        llPhone = findViewById(R.id.linear_phone);


//       upload image
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading profile image/s....");

        if (bundle != null) {
            ifIsCreator = bundle.getBoolean("ifIsCreator");
            intentUserId = bundle.getString("idUser");

            user = mAuth.getCurrentUser();
            if (!(user.getUid().equals(intentUserId))) {
                editProfile.setVisibility(View.GONE);
                editProfilePicture.setVisibility(View.GONE);
            }


        }

    }

    public void getUserData() {

        String uid = mAuth.getUid();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.logo);
        idRecipes = new ArrayList<>();
        Glide.with(this).load(R.drawable.logo).into(img_profie);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users").child(uid);
        DatabaseReference mRef = databaseReference;
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lName.setText(snapshot.child("lastName").getValue().toString());
                fName.setText(snapshot.child("firstName").getValue().toString());
                city.setText(snapshot.child("city").getValue().toString());
                email.setText(snapshot.child("email").getValue().toString());
                gender.setText(snapshot.child("gender").getValue().toString());
                bDay.setText(snapshot.child("birthDay").getValue().toString());
                if (snapshot.child("phone").exists()){
                    phone.setText(snapshot.child("phone").getValue().toString());
                }else{
                    llPhone.setVisibility(View.GONE);
                }
                title.setText(fName.getText() + " " + lName.getText());
                if (snapshot.child("profilePicture").exists()) {
                    Glide.with(PersonalAreaUserActivity.this).load(snapshot.child("profilePicture").getValue().toString()).into(img_profie);
                }
                DataSnapshot myRecipes = snapshot.child("recipes");
                Iterable<DataSnapshot> myRecipesCildren = myRecipes.getChildren();
                for (DataSnapshot data : myRecipesCildren) {
                    idRecipes.add(data.getKey());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View v) {


        if (v == bookOrInfo) {
            linearInfo = findViewById(R.id.linear_info);
            linearName = findViewById(R.id.linear_name);
            linearList = findViewById(R.id.linear_my_recipe);
            myRecipe = findViewById(R.id.listRecipes);

//            AdapterTop adapterRecipe = new AdapterTop(getBaseContext(),)
//            myRecipe.setAdapter(adapterRecipe);

            if (bookOrInfo.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_book).getConstantState())) {

                linearInfo.setVisibility(View.GONE);
                linearName.setVisibility(View.GONE);
                bookOrInfo.setImageResource(R.drawable.ic_info);
                linearList.setVisibility(View.VISIBLE);
                getMyBook();

            } else {
                linearList.setVisibility(View.GONE);
                linearName.setVisibility(View.VISIBLE);
                linearInfo.setVisibility(View.VISIBLE);
                bookOrInfo.setImageResource(R.drawable.ic_book);
            }
        }

    }

    public void getMyBook() {
        String id = mAuth.getUid();
        titleRecipes = new ArrayList<>();
        descriptionRecipes = new ArrayList<>();
        watch = new ArrayList<>();
        like = new ArrayList<>();
        imageRecipes = new ArrayList<>();
        myRecipe = findViewById(R.id.listRecipes);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("recipe");
//        send request to get the data
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //                get Details

                for (int i = 0; i < idRecipes.size()-1; i++) {

                    DataSnapshot Details = snapshot.child(idRecipes.get(i)).child("Details");
                    Iterable<DataSnapshot> detailsCildren = Details.getChildren();
                    for (DataSnapshot data : detailsCildren) {
                        switch (data.getKey()) {
                            case "Descraptions": {
                                descriptionRecipes.add(data.getValue().toString());
                                break;
                            }
                            case "Likes": {
                                like.add(data.getValue().toString());
                                break;
                            }
                            case "RecipeName": {
                                titleRecipes.add(data.getValue().toString());
                                break;
                            }
                        }
                    }

                    DataSnapshot mainPicture = snapshot.child(idRecipes.get(i)).child("MainImages");
                    Iterable<DataSnapshot> mainPictureCildren = mainPicture.getChildren();
                    for (DataSnapshot data : mainPictureCildren) {
                        imageRecipes.add(data.getValue().toString());
                    }

                    watch.add(snapshot.child(idRecipes.get(i)).child("views").getValue().toString());
                    like.add(snapshot.child(idRecipes.get(i)).child("Likes").child("Amount").getValue().toString());
                }

                adapterTop = new AdapterTop(getBaseContext(), titleRecipes, descriptionRecipes, watch, like, imageRecipes,null);
                adapterTop.notifyDataSetChanged();
                myRecipe.setAdapter(adapterTop);
                myRecipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //================================imagePicker====================================

    public void launchGalleryIntent(int Choser) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, Choser);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    launchGalleryIntent(REQUEST_EXTERNAL_STORAGE);
                } else {
                    // permission denied
                    MyToast.showLong(getApplicationContext(), "Permission denied check permission in phone settings");
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri imageUri = data.getData();
                Glide.with(this).load(imageUri).into(img_profie);
                uploadPictureToFirebaseStorage(new FirebaseCallback() {
                    @Override
                    public void onCallback(Uri uri) {
                        profileImageUri = uri;
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        String userUid = mAuth.getUid();
                        databaseReference = firebaseDatabase.getReference("users").child(userUid);
                        databaseReference.child("profilePicture").setValue(profileImageUri.toString());
                        progressDialog.dismiss();
                    }
                }, imageUri);
            } else {
                MyToast.showShort(getApplicationContext(), "You can select one image");
            }
        }
    }

    public interface FirebaseCallback {
        void onCallback(Uri uri);
    }

    public void uploadPictureToFirebaseStorage(FirebaseCallback firebaseCallback, Uri imageUri) {
        progressDialog.show();

//            upload to storage
        int imageCount = 1;
        storageReference = storage.getReference("usersImages");
        StorageReference riversRef = storageReference.child(mAuth.getUid());
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        MyToast.showShort(getApplicationContext(), "Seccess");
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                profileImageUri = uri;
                                firebaseCallback.onCallback(profileImageUri);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        MyToast.showShort(getApplicationContext(), "Failure");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progressPrecent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Image: " + imageCount + " :" + (int) progressPrecent + "%");
            }
        });
    }

}
