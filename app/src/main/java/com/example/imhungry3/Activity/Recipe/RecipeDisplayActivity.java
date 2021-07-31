package com.example.imhungry3.Activity.Recipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.imhungry3.Activity.UserPlace.PersonalAreaActivity;
import com.example.imhungry3.Adapter.AdapterRecyclerIngredients;
import com.example.imhungry3.Adapter.AdapterStepsDisplay;
import com.example.imhungry3.BaseActivity;
import com.example.imhungry3.Activity.Chat.DisplayChatFragment;
import com.example.imhungry3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class RecipeDisplayActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AdapterRecyclerIngredients";
    private ImageButton backPage;
    private SlidingUpPanelLayout slide_ingredients;
    private ImageSlider mainImageSlider;
    private List<SlideModel> mainImagesSlideModel;
    private ArrayList<String> ingridientsNames, ingridientsAmount, ingridientsImageUrls;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton likeBtn, fab4, sendMessage;
    private boolean flag = true;
    private String id;
    //    TextViews
    TextView tv_Category, tv_preparationTime, tv_RecipeName, tv_Descraption, tv_Likes, tv_Views;
    Button tv_User;
    private int amountLikes;
    //
//    steps values
    private ArrayList<String> stepsList, stepsImagesUriArr;
    private String recipeId,creator_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_display);
        backPage = findViewById(R.id.back);
        likeBtn = findViewById(R.id.likeBtn);
        likeBtn.setOnClickListener(this);
        sendMessage = findViewById(R.id.send_message2);
        sendMessage.setOnClickListener(this);
        fab4 = (FloatingActionButton) findViewById(R.id.fab);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    checkCreator();

                    fab4.setImageResource(R.drawable.ic_close_black);
                    flag = false;

                } else {
                    likeBtn.hide();
                    sendMessage.hide();
                    sendMessage.animate().translationY(0);
                    likeBtn.animate().translationY(0);

                    fab4.setImageResource(R.drawable.ic_add_black);
                    flag = true;

                }
            }
        });

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null) {
            recipeId = (String) b.get("recipeId");
        }
//        rootView = getLayoutInflater().inflate(R.layout.activity_recipe_display, frameLayout);
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

//        stepslists
        stepsImagesUriArr = new ArrayList<>();
        stepsList = new ArrayList<>();

//        Connected TextViews with xml File
//        tv_Category = findViewById(R.id.)  -> not set in xml file
        tv_Descraption = findViewById(R.id.tv_Descraption);
        tv_Likes = findViewById(R.id.tv_likes);
        tv_Views = findViewById(R.id.tv_Views);
        tv_preparationTime = findViewById(R.id.tv_time);
        tv_RecipeName = findViewById(R.id.tv_recipeName);
        tv_User = findViewById(R.id.tv_createBy);
        tv_User.setOnClickListener(this);

        //create image slider
        mainImageSlider = findViewById(R.id.image_slider);
        //create list all picture
        mainImagesSlideModel = new ArrayList<>();
//      mainImagesSlideModel.add(new SlideModel(R.drawable.pic1,ScaleTypes.FIT));

        //create slider category ingredients
        slide_ingredients = (SlidingUpPanelLayout) findViewById(R.id.slidingUp);

        //recycleview Ingredients
        ingridientsNames = new ArrayList<>();
        ingridientsAmount = new ArrayList<>();
        ingridientsImageUrls = new ArrayList<>();

//      likeBtn


        backPage.setOnClickListener(this);
        getRecipeData(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<String> stepsImagesUriArr, ArrayList<String> stepsList) {

                getLikes();
                initRecyclerView();
            }
        });

    }


    private void getLikes() {
//        firebase referance
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("recipe").child(recipeId + "").child("Likes");
//        get likes
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("here: ", "onDataChange: " + user.getEmail());
                    String email = user.getEmail().split("@")[0];
                    Log.d("here: ", "onDataChange: " + email);

                    if (snapshot.child("whoLike").child(email).exists()) {
                        likeBtn.setImageResource(R.drawable.ic_unlike);
                    }

                    amountLikes = Integer.parseInt(snapshot.child("Amount").getValue().toString());
                    tv_Likes.setText(amountLikes + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRecipeData(FirebaseCallback firebaseCallback) {
//        set path
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("recipe");
//        send request to get the data
        databaseReference.child("" + recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                get Details
                if (snapshot == null) {
                    finish();
                }
                DataSnapshot Details = snapshot.child("Details");
                Iterable<DataSnapshot> detailsCildren = Details.getChildren();
                for (DataSnapshot data : detailsCildren) {
                    switch (data.getKey()) {
                        case "Category": {

                            break;
                        }
                        case "RecipeName": {
                            tv_RecipeName.setText(data.getValue().toString());
                            break;
                        }
                        case "Descraptions": {
                            tv_Descraption.setText(data.getValue().toString());
                            break;
                        }
                        case "email": {
                            String temp[] = data.getValue().toString().split("@");
                            creator_email = data.getValue().toString();
                            tv_User.setText(temp[0]);
                            break;
                        }
                        case "preparationTime": {
                            tv_preparationTime.setText(data.getValue().toString());
                            break;
                        }
                        case "Views": {

                            break;
                        }
                        case "id": {
                            id = data.getValue().toString();
                            break;
                        }
                    }
                }

//                Views

                int tempViews = Integer.parseInt(snapshot.child("views").getValue().toString());
                updateViews(tempViews);

                DataSnapshot mainPicture = snapshot.child("MainImages");
                Iterable<DataSnapshot> mainPictureCildren = mainPicture.getChildren();
                for (DataSnapshot data : mainPictureCildren) {
                    SlideModel slideModel = new SlideModel(data.getValue().toString(), ScaleTypes.FIT);
                    if (slideModel != null) {
                        mainImagesSlideModel.add(slideModel);
                    } else {
                        mainImagesSlideModel.add(new SlideModel(R.drawable.image_workshop, ScaleTypes.FIT));
                    }

                    mainImageSlider.setImageList(mainImagesSlideModel);
                }

                DataSnapshot ingredients = snapshot.child("ingredients");
                Iterable<DataSnapshot> ingredientsChildren = ingredients.getChildren();
                for (DataSnapshot data : ingredientsChildren) {
                    Iterable<DataSnapshot> inChildren = data.getChildren();
                    for (DataSnapshot i : inChildren) {
                        switch (i.getKey()) {
                            case "ingredientAmount": {
                                ingridientsAmount.add(i.getValue().toString());
                                break;
                            }

                            case "ingredientDownloadUri": {
                                ingridientsImageUrls.add(i.getValue().toString());
                                break;
                            }
                            case "ingredientName": {
                                ingridientsNames.add(i.getValue().toString());
                                break;
                            }

                        }
                    }

                }
                DataSnapshot stepsSnapshot = snapshot.child("Steps");
                Iterable<DataSnapshot> stepsChildren = stepsSnapshot.child("StepsList").getChildren();
                int counter = 0;
                for (DataSnapshot data : stepsChildren) {

                    stepsList.add(data.getValue().toString());
                    Log.d("index:" + counter, " " + stepsList.get(counter));
                }
                Iterable<DataSnapshot> imagesUri = stepsSnapshot.child("StepsImages").getChildren();
                for (DataSnapshot images : imagesUri) {
                    stepsImagesUriArr.add(images.getValue().toString());
                }

                firebaseCallback.onCallback(stepsImagesUriArr, stepsList);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public interface FirebaseCallback {
        void onCallback(ArrayList<String> stepsImagesUriArr, ArrayList<String> stepsList);

    }

    private void initRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        AdapterRecyclerIngredients adapter = new AdapterRecyclerIngredients(this, ingridientsNames, ingridientsAmount, ingridientsImageUrls);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView stepDisplay = findViewById(R.id.stepDisplay);
        stepDisplay.setLayoutManager(layoutManager2);
        AdapterStepsDisplay adapterStepsDisplay = new AdapterStepsDisplay(getBaseContext(), stepsList, stepsImagesUriArr);
        stepDisplay.setAdapter(adapterStepsDisplay);

    }

    private void updateViews(int numOfViews) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("recipe").child(recipeId).child("views");
        databaseReference.setValue("" + (numOfViews + 1));
        tv_Views.setText("" + (numOfViews + 1));
    }


    @Override
    public void onClick(View v) {

        if (v == sendMessage) {
            GetDataToSendMessage(new send_message_interface() {
                @Override
                public void onCallback(String uid, String uri) {
                    Fragment displayChatFragment = DisplayChatFragment.newInstance(uid, uri);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right, R.anim.enter_to_right, R.anim.exit_to_right);
                    transaction.addToBackStack(null);
                    transaction.add(R.id.chat_framelayout, displayChatFragment).commit();
                }
            });

        }
        if (v == tv_User) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("recipe").child(recipeId).child("Details").child("id");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    id = snapshot.getValue().toString();
                    Intent intentPersonalInfo = new Intent(RecipeDisplayActivity.this, PersonalAreaActivity.class);
                    intentPersonalInfo.putExtra("ifIsCreator", true);
                    intentPersonalInfo.putExtra("idUser", id);
                    startActivity(intentPersonalInfo);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        if (v == likeBtn) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("recipe").child(recipeId).child("Likes");
//            if the user allready like this recipe

            databaseReference.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    int Likes = Integer.parseInt(currentData.child("Amount").getValue().toString());
                    String temp[] = user.getEmail().split("@");
                    currentData.child("whoLike").getChildren();
                    Iterable<MutableData> whoLikes = currentData.getChildren();
//                    Drawable tmp

                    if (likeBtn.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_unlike).getConstantState())) {
                        if (Likes > 0)
                        {
                        currentData.child("Amount").setValue(Likes - 1);
                        currentData.child("whoLike").child(temp[0]).setValue(null);
                        likeBtn.setImageResource(R.drawable.ic_like2);
                        }
                    } else {
                        currentData.child("Amount").setValue(Likes + 1);
                        currentData.child("whoLike").child(temp[0]).setValue(user.getEmail());
                        likeBtn.setImageResource(R.drawable.ic_unlike);
                    }


                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                }
            });
        }

        if (v == backPage) {
            onBackPressed();
            finish();
        }
    }

    private void GetDataToSendMessage(send_message_interface msgInterface) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users").child(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid = id, uri = "null";
                if (snapshot.child("profilePicture").exists()) {
                    uri = snapshot.child("profilePicture").getValue().toString();
                }
                msgInterface.onCallback(uid, uri);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface send_message_interface {
        void onCallback(String uid, String uri);
    }


    public void checkCreator() {

        if (creator_email.equals(user.getEmail())) {
//            if i create this workshop then give me Edit option
//            and set visble to edit button
            likeBtn.show();
            likeBtn.animate().translationY(-(fab4.getCustomSize()) + 25);
        } else {
            likeBtn.show();
            sendMessage.show();
            sendMessage.animate().translationY(-(likeBtn.getCustomSize() + fab4.getCustomSize()));
            likeBtn.animate().translationY(-(fab4.getCustomSize()) + 25);
//            sendMessage.animate().translationY(-(join.getCustomSize() + fab4.getCustomSize()));
        }
    }
}