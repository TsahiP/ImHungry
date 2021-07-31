package com.example.imhungry3.Activity.UserPlace;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.Activity.Recipe.RecipeDisplayActivity;
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

public class PersonalAreaActivity extends BaseActivity implements View.OnClickListener {
//    Firebase variables
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

//    View Variables
    private EditText fName, lName, email, bDay, city, gender,phone;//must to have
    private TextView title;
    private LinearLayout llPhone;
    private ImageView editProfile, img_profie, editProfilePicture;
    private boolean edit_flag = false;

    private FloatingActionButton bookOrInfo;
    private ListView myRecipe;
    private LinearLayout linearInfo, linearName, linearList;

    private ArrayList<String> idRecipes;
    private ArrayList<String> titleRecipes;
    private ArrayList<String> descriptionRecipes;
    private ArrayList<String> watch;
    private ArrayList<String> like;
    private ArrayList<String> imageRecipes;
    private AdapterTop adapterTop;
    private boolean ifIsCreator = false;
    private ImageButton back;
    //    upload image
    final int REQUEST_EXTERNAL_STORAGE = 10;
    private ProgressDialog progressDialog;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri profileImageUri;
    private String intentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_area);

        Bundle bundle = getIntent().getExtras();
//        View rootView = getLayoutInflater().inflate(R.layout.activity_personal_area, frameLayout);
        LayoutInflater inflater = LayoutInflater.from(new ContextThemeWrapper(getApplicationContext(), R.style.AppTheme));
        llPhone = findViewById(R.id.linear_phone);
        back = findViewById(R.id.back_personal);
        back.setOnClickListener(this);
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
        phone = findViewById(R.id.phone_profile);
        bookOrInfo = findViewById(R.id.bookOrInfo);
//        edit btn
        editProfile = findViewById(R.id.edit_profile);
        editProfile.setOnClickListener(this);
        editProfilePicture = findViewById(R.id.edit_profile_picture);
        editProfilePicture.setOnClickListener(this);
        bookOrInfo.setOnClickListener(this);


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
        getUserData();
    }

    public void getUserData() {

        String uid = mAuth.getUid();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.logo);
        idRecipes = new ArrayList<>();
        Glide.with(this).load(R.drawable.logo).into(img_profie);
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (ifIsCreator == true && !(uid.equals(intentUserId))) {
            databaseReference = firebaseDatabase.getReference("users").child(intentUserId);
        } else {
            databaseReference = firebaseDatabase.getReference("users").child(uid);
        }
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idRecipes.clear();
                if (snapshot.exists()) {
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
                    title.setText(fName.getText().toString().substring(0, 1).toUpperCase() + fName.getText().toString().substring(1)
                            + " " + lName.getText().toString().substring(0, 1).toUpperCase() + lName.getText().toString().substring(1));
                    if (snapshot.child("profilePicture").exists()) {
                        Glide.with(getBaseContext()).load(snapshot.child("profilePicture").getValue().toString()).into(img_profie);
                    }
                    DataSnapshot myRecipes = snapshot.child("recipes");
                    Iterable<DataSnapshot> myRecipesCildren = myRecipes.getChildren();
                    for (DataSnapshot data : myRecipesCildren) {
                        idRecipes.add(data.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v == editProfilePicture) {
            if (ActivityCompat.checkSelfPermission(PersonalAreaActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PersonalAreaActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                    return;
            } else {
                launchGalleryIntent(REQUEST_EXTERNAL_STORAGE);
            }
            return;
        }

        if(v == back)
        {
            onBackPressed();
            return;
        }
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
            return;
        }
        if (v == editProfile && edit_flag == false) {
            MyToast.showShort(PersonalAreaActivity.this,"Edit your profile");
            fName.setTextColor(getResources().getColor(R.color.black));
            lName.setTextColor(getResources().getColor(R.color.black));
            city.setTextColor(getResources().getColor(R.color.black));
            fName.setEnabled(true);
            fName.setFocusable(true);
            lName.setEnabled(true);
            city.setEnabled(true);
            edit_flag = true;
//            set v Image to save the data
//            editProfile.setImageIcon();
            editProfile.setImageResource(R.drawable.ic_edit_finish);
            return;
        }

        if (v == editProfile && edit_flag == true) {
            MyToast.showShort(PersonalAreaActivity.this,"Edit mode close ");
            fName.setTextColor(getResources().getColor(R.color.disableEdittext));
            lName.setTextColor(getResources().getColor(R.color.disableEdittext));
            city.setTextColor(getResources().getColor(R.color.disableEdittext));
            fName.setEnabled(false);
            lName.setEnabled(false);
            city.setEnabled(false);
            editProfile.setImageResource(R.drawable.ic_edit_profile);
            edit_flag = false;
//            set edit Image to save the data
//            editProfile.setImageIcon();
            if (!fName.getText().toString().isEmpty()) {
                String tmp = fName.getText().toString();
                if (tmp.contains("[0-9]+")) {
                    return;
                }

            } else {
                return;
            }

            if (!lName.getText().toString().isEmpty()) {
                String tmp = lName.getText().toString();
                if (tmp.contains("[0-9]+")) {
                    return;
                }

            } else {
                return;
            }
            if (!city.getText().toString().isEmpty()) {
                String tmp = city.getText().toString();
                if (tmp.contains("[0-9]+")) {
                    return;
                }

            } else {
                return;
            }

//            set the new data
            String uid = mAuth.getUid();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("users").child(uid);
            databaseReference.child("city").setValue(city.getText().toString());
            databaseReference.child("lastName").setValue(lName.getText().toString());
            databaseReference.child("firstName").setValue(fName.getText().toString());
            return;

        }


    }

    public void getMyBook() {
        String id = mAuth.getUid();

        myRecipe = findViewById(R.id.listRecipes);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("recipe");
//        send request to get the data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //                get Details
                titleRecipes = new ArrayList<>();
                descriptionRecipes = new ArrayList<>();
                watch = new ArrayList<>();
                like = new ArrayList<>();
                imageRecipes = new ArrayList<>();
                for (int i = 0; i < idRecipes.size(); i++) {

                    DataSnapshot Details = snapshot.child(idRecipes.get(i)).child("Details");
                    Iterable<DataSnapshot> detailsCildren = Details.getChildren();
                    if (snapshot.child(idRecipes.get(i)) != null) {
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

                }
                myRecipe.setAdapter(null);
                adapterTop = new AdapterTop(getBaseContext(), titleRecipes, descriptionRecipes, watch, like, imageRecipes, idRecipes);
                adapterTop.notifyDataSetChanged();
                myRecipe.setAdapter(adapterTop);


                myRecipe.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        user = mAuth.getCurrentUser();
                        if (user.getEmail().equals(email.getText().toString())) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(PersonalAreaActivity.this);
                            View mView = getLayoutInflater().inflate(R.layout.personal_delete_recipe_dialog, null);
                            Button delete, stay;
                            delete = mView.findViewById(R.id.delete);
                            stay = mView.findViewById(R.id.stay);
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    firebaseDatabase = FirebaseDatabase.getInstance();
                                    String removeRecipe = idRecipes.get(position);
                                    titleRecipes.remove(position);
                                    idRecipes.remove(position);
                                    descriptionRecipes.remove(position);
                                    watch.remove(position);
                                    like.remove(position);
                                    imageRecipes.remove(position);
                                    adapterTop = new AdapterTop(getBaseContext(), titleRecipes, descriptionRecipes, watch, like, imageRecipes, idRecipes);
                                    adapterTop.notifyDataSetChanged();
                                    myRecipe.setAdapter(adapterTop);
                                    databaseReference = firebaseDatabase.getReference("users").child(user.getUid()).child("recipes").child(removeRecipe);
                                    databaseReference.removeValue();
                                    databaseReference = firebaseDatabase.getReference("recipe").child(removeRecipe);
                                    databaseReference.removeValue();
                                    dialog.dismiss();
                                }
                            });
                            stay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                        return false;

                    }
                });
                myRecipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(PersonalAreaActivity.this, RecipeDisplayActivity.class);
                        intent.putExtra("recipeId", idRecipes.get(position));
                        startActivity(intent);
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
                        MyToast.showShort(PersonalAreaActivity.this, "Seccess");
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
