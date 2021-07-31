package com.example.imhungry3.Activity.Recipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.imhungry3.Adapter.AdapterIngredientsChoice;
import com.example.imhungry3.Adapter.AdapterIngredientsDisplay;
import com.example.imhungry3.Adapter.AdapterStepsDisplay;
import com.example.imhungry3.Adapter.AutoCompleteIngredientsAdapter;
import com.example.imhungry3.BaseActivity;
import com.example.imhungry3.Class.IngredientsItemClass;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CreateRecipeActivity extends BaseActivity implements View.OnClickListener {
    private View child;
    private ImageButton back;
    private FloatingActionButton saveBtn;
    private Button scaleIgrediantDialogBtn,infoBtn, okDialog, cancelDialog, addStep, addPictureStep, IngredietnsCategory, StepsCategory, preparationTimeBtn, addPictureMain, dropCategory;
    private EditText amount, etSteps;
    private int positionIngredientTemp;
    private int uploadingFlag = 1;
    private Dialog dialog;
    private String preparationTime,stepImageUri;
    private static List<String> nameIngredient, uriIngredient;
    private ArrayList<String> amountList, stepList, ingredientsList, imageIngredientsList;
    private ArrayList<String> stepImages;
    private AdapterIngredientsChoice adapterChoice;
    private AdapterIngredientsDisplay adapterDisplay;
    private AutoCompleteTextView search;
    private LinearLayout linear, steps, infoLinear;
    //======droper
    private CardView cv;
    //    step recycler
    private RecyclerView recyclerViewStep,listIngredietns;
    private TextInputLayout recipeName, recipeDiscraption;

    //    upload and save into firebase
//    main pics
    private List<SlideModel> slideModel;
    private ImageSlider imageSlider;
    ArrayList<Uri> mainPicsArrayUri;
    //    =========
    private ImageView displays_image;
    private String bitmap;
    private AdapterStepsDisplay myAdapter;
    //load & upload pictures
    final int REQUEST_EXTERNAL_STORAGE = 100;
    final int MAIN_PIC = 101;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ArrayList<Uri> imageUriArr, stepsImagesDownloadUri, mainImagesDownloadUri;
    private ProgressDialog progressDialog;
    private int imageCount = 1;
    //    upload
    private int lastRecipeId;
    private String imageEncoded;
    private List<String> imagesEncodedList;
    private List<IngredientsItemClass> innList;

//    amounts ingredients radio button



    private Button getOut, stay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

//        title + descraption
        dropCategory = (Button) findViewById(R.id.categoryBtn);
        CheckLastId();
        recipeName = findViewById(R.id.recipeName);
        recipeName.requestFocus();
//        Main image slider
        slideModel = new ArrayList<>();
        imageSlider = findViewById(R.id.image_slider_create_recipe);
        mainPicsArrayUri = new ArrayList<>();
//        adding step
        addPictureStep = findViewById(R.id.addPictureStep);
        addPictureStep.setOnClickListener(this);
//        recipe descraption
        recipeDiscraption = findViewById(R.id.recipeDescraption);
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.showSoftInput(recipeName, InputMethodManager.SHOW_IMPLICIT);
        preparationTimeBtn = findViewById(R.id.preparationTimePicker);
        preparationTimeBtn.setOnClickListener(this);
        back = findViewById(R.id.back_recipe);
        search = findViewById(R.id.searchIngreidients);
        addStep = findViewById(R.id.addStep);
        addStep.setOnClickListener(this);
//        recycler ingredients
        listIngredietns = findViewById(R.id.listIngridiants);
        listIngredietns.setAdapter(adapterDisplay);
        listIngredietns.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewStep = findViewById(R.id.listSteps);
        ingredientsList = new ArrayList<>();
        imageIngredientsList = new ArrayList<>();
        amountList = new ArrayList<>();
        stepList = new ArrayList<>();
        stepImages = new ArrayList<>();
        etSteps = findViewById(R.id.etSteps);
        linear = findViewById(R.id.linearLayout10);
        steps = findViewById(R.id.linearStep);
        addPictureMain = findViewById(R.id.main_picture_upload);
//        upload images and data to firebase
        displays_image = findViewById(R.id.displays_image);
        //        images arr
        imageUriArr = new ArrayList<>();
        stepsImagesDownloadUri = new ArrayList<>();
        mainImagesDownloadUri = new ArrayList<>();
//        save btn
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Recipe");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading image/s....");
        progressDialog.setCancelable(false);
//        preparationTime Picker
        preparationTimeBtn = findViewById(R.id.preparationTimePicker);
        preparationTimeBtn.setOnClickListener(this);
        preparationTimeBtn.setTransformationMethod(null);

        myAdapter = new AdapterStepsDisplay(getApplication().getApplicationContext(), stepList, stepImages);



        fillCountryList();
        AutoCompleteIngredientsAdapter adapter = new AutoCompleteIngredientsAdapter(this, innList);
        search.setAdapter(adapter);

        /**
         * dialog - ingrediants scale dialog
         * cancelDialog - button => close the dialog on click
         * okDialog - button => save the inpute details on click
         * scaleIngrediantsDialog - vutton => open scale menu on click
         */
        dialog = new Dialog(CreateRecipeActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_amount_ingredients);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        okDialog = dialog.findViewById(R.id.okDialog);
        cancelDialog = dialog.findViewById(R.id.cancelDialog);
        amount = dialog.findViewById(R.id.etAmount);
        scaleIgrediantDialogBtn = dialog.findViewById(R.id.scaleIngrediantsDialog);
        scaleIgrediantDialogBtn.setOnClickListener(this);
        okDialog.setOnClickListener(this);
        cancelDialog.setOnClickListener(this);
        back.setOnClickListener(this);


//========droper vals
        cv = (CardView) findViewById(R.id.cardView);
        StepsCategory = (Button) findViewById(R.id.btn_drop_steps);
        IngredietnsCategory = (Button) findViewById(R.id.btn_drop);
        infoBtn = findViewById(R.id.infobtn);
        infoLinear = findViewById(R.id.infoLinear);
        addPictureMain.setOnClickListener(this);


        IngredietnsCategory.setOnClickListener(this);
        infoBtn.setOnClickListener(this);
        StepsCategory.setOnClickListener(this);
//================
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    for(int i = 0; i< viewGroup.getChildCount(); ++i) {
                         child = viewGroup.getChildAt(i);
                    }
                }
                TextView textView = (TextView) child;
                String str = textView.getText().toString();
                positionIngredientTemp = nameIngredient.indexOf(str);
                if(ingredientsList.contains(nameIngredient.get(positionIngredientTemp))== true)
                {
//                    Toast.makeText(getBaseContext(),getResources().getString(R.string.onList),Toast.LENGTH_SHORT).show();
                    search.setText("");
                }else
                {
                    dialog.show();
                    search.setText("");
                }

            }
        });
        ItemTouchHelper.SimpleCallback stepSwipeCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPostion = target.getAdapterPosition();
                TextView tStepNum = target.itemView.findViewById(R.id.numberStep);
                TextView vStepNum = viewHolder.itemView.findViewById(R.id.numberStep);
                tStepNum.setText(fromPosition + 1 + "");
                vStepNum.setText(toPostion + 1 + "");
                Collections.swap(stepList, fromPosition, toPostion);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPostion);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(stepSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewStep);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(deleteStep);
        itemTouchHelper2.attachToRecyclerView(recyclerViewStep);
        ItemTouchHelper ingredientsTuchHelper = new ItemTouchHelper(deleteIngredients);
        ingredientsTuchHelper.attachToRecyclerView(listIngredietns);

        dropCategory.setOnClickListener(this);
    }


    String tempStep = null;
    String imageTemp = "";
    ItemTouchHelper.SimpleCallback deleteStep = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.LEFT:

                    tempStep = stepList.get(position);
                    imageTemp = stepImages.get(position);
                    Snackbar.make(recyclerViewStep, tempStep, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    stepList.add(position, tempStep);
                                    stepImages.add(position, imageTemp);
                                    myAdapter.notifyItemInserted(position);
                                }
                            }).show();
                    stepList.remove(position);
                    stepImages.remove(position);
                    myAdapter.notifyItemRemoved(position);
                    break;

            }
        }

    };
    ItemTouchHelper.SimpleCallback deleteIngredients = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.LEFT:

                    String tempIngName = ingredientsList.get(position),tempAmount = amountList.get(position),imageIngTemp = imageIngredientsList.get(position);
                    Snackbar.make(listIngredietns, tempIngName, Snackbar.LENGTH_LONG).setText(tempIngName)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    amountList.add(position, tempAmount);
                                    imageIngredientsList.add(position, imageIngTemp);
                                    ingredientsList.add(position, tempIngName);
                                    adapterDisplay = new AdapterIngredientsDisplay(getBaseContext(), ingredientsList, imageIngredientsList, amountList);
                                    listIngredietns.setAdapter(adapterDisplay);
                                }
                            }).show();
                    amountList.remove(position);
                    imageIngredientsList.remove(position);
                    ingredientsList.remove(position);
                    adapterDisplay = new AdapterIngredientsDisplay(getBaseContext(), ingredientsList, imageIngredientsList, amountList);
                    listIngredietns.setAdapter(adapterDisplay);
                    break;

            }
        }

    };

    @Override
    public void onClick(View v) {
        if (v == dropCategory)
        {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(CreateRecipeActivity.this, dropCategory);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.category_recipe, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    dropCategory.setText(item.getTitle());
                    return true;
                }
            });

            popup.show();//showing popup menu
        }


//        details droper =>ingrediants , info, steps
        if (v == IngredietnsCategory)
        {
            if (listIngredietns.getVisibility() == View.GONE) {
                if (recyclerViewStep.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(cv, new AutoTransition());
                    recyclerViewStep.setVisibility(View.GONE);
                    steps.setVisibility(View.GONE);
                    StepsCategory.setBackgroundResource(R.drawable.down_24);
                }
                if (infoLinear.getVisibility() == View.VISIBLE) {
                    infoLinear.setVisibility(View.GONE);
                    infoBtn.setBackgroundResource(R.drawable.down_24);
                }
                TransitionManager.beginDelayedTransition(cv, new AutoTransition());
                listIngredietns.setVisibility(View.VISIBLE);
                linear.setVisibility(View.VISIBLE);
                IngredietnsCategory.setBackgroundResource(R.drawable.up_24);
            } else {
                TransitionManager.beginDelayedTransition(cv, new AutoTransition());
                listIngredietns.setVisibility(View.GONE);
                linear.setVisibility(View.GONE);
                IngredietnsCategory.setBackgroundResource(R.drawable.down_24);
            }
        }
        if (v == infoBtn)
        {
            if (infoLinear.getVisibility() == View.GONE) {
                if (listIngredietns.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(cv, new AutoTransition());
                    listIngredietns.setVisibility(View.GONE);
                    linear.setVisibility(View.GONE);
                    IngredietnsCategory.setBackgroundResource(R.drawable.down_24);
                }

                if (recyclerViewStep.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(cv, new AutoTransition());
                    recyclerViewStep.setVisibility(View.GONE);
                    steps.setVisibility(View.GONE);
                    StepsCategory.setBackgroundResource(R.drawable.down_24);
                }
                infoBtn.setBackgroundResource(R.drawable.up_24);
                infoLinear.setVisibility(View.VISIBLE);
            } else {
                TransitionManager.beginDelayedTransition(cv, new AutoTransition());
                infoLinear.setVisibility(View.GONE);
                infoBtn.setBackgroundResource(R.drawable.down_24);
            }
        }

        if (v == StepsCategory){
            if (recyclerViewStep.getVisibility() == View.GONE) {
                if (listIngredietns.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(cv, new AutoTransition());
                    listIngredietns.setVisibility(View.GONE);
                    linear.setVisibility(View.GONE);
                    IngredietnsCategory.setBackgroundResource(R.drawable.down_24);
                }

                if (infoLinear.getVisibility() == View.VISIBLE) {
                    infoLinear.setVisibility(View.GONE);
                    infoBtn.setBackgroundResource(R.drawable.down_24);
                }

                TransitionManager.beginDelayedTransition(cv, new AutoTransition());
                recyclerViewStep.setVisibility(View.VISIBLE);
                steps.setVisibility(View.VISIBLE);
                StepsCategory.setBackgroundResource(R.drawable.up_24);
            } else {
                TransitionManager.beginDelayedTransition(cv, new AutoTransition());
                recyclerViewStep.setVisibility(View.GONE);
                steps.setVisibility(View.GONE);
                StepsCategory.setBackgroundResource(R.drawable.down_24);
            }
        }
//        ===========================================

        if (v == scaleIgrediantDialogBtn){
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(CreateRecipeActivity.this, scaleIgrediantDialogBtn);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.dialog_scale_ingrediants, popup.getMenu());
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    scaleIgrediantDialogBtn.setText(item.getTitle());
                    return true;
                }
            });
            popup.show();//showing popup menu
        }


        if (v == okDialog) {
            if (scaleIgrediantDialogBtn.getText().toString().equals("Units"))
            {
                MyToast.showShort(this,"Select a unit of measure");
                return;
            }else
            {
                if(!amount.getText().toString().equals(""))
                {
                    ingredientsList.add(nameIngredient.get(positionIngredientTemp));
                    imageIngredientsList.add(uriIngredient.get(positionIngredientTemp));
                    amountList.add(amount.getText().toString()+" "+scaleIgrediantDialogBtn.getText());
                    adapterDisplay = new AdapterIngredientsDisplay(this, ingredientsList, imageIngredientsList, amountList);
//            adapterDisplay.notifyDataSetChanged();
                    listIngredietns.setAdapter(adapterDisplay);
                    dialog.dismiss();
                }
                else
                {
                    MyToast.showShort(this,"Please enter amount");
                }

            }

        }

        if (v == cancelDialog) {
            dialog.dismiss();
        }

        if (v == addStep) {
            if (!etSteps.getText().toString().isEmpty()) {
                stepList.add(etSteps.getText().toString());
                if (stepImageUri != null) {
                    stepImages.add(stepImageUri);
                    stepImageUri=null;
                    bitmap = null;
                } else {
                    stepImages.add("null");
                }
                recyclerViewStep.setAdapter(myAdapter);
                recyclerViewStep.setLayoutManager(new LinearLayoutManager(this));
                displays_image.setImageDrawable(getResources().getDrawable(R.drawable.image_workshop));
                etSteps.setText("");
            }

        }

        if (addPictureStep == v) {
            stepImageUri = null;
            if (ActivityCompat.checkSelfPermission(CreateRecipeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CreateRecipeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                launchGalleryIntent(REQUEST_EXTERNAL_STORAGE);
            }
        }

        if (saveBtn == v) {
            if (recipeName.getEditText().getText() != null
                    && recipeDiscraption.getEditText().getText() != null
                    && stepList.size() > 0
                    && ingredientsList.size() > 0
                    && dropCategory.getText().toString() != "Category")
            {

                insertRecipe();
            }else{
                if (recipeName.getEditText().getText().toString().equals(""))
                {
                    recipeName.setError("please enter name recipe");
                    recipeName.setEndIconActivated(true);
                }
                else
                {
                    recipeName.setError(null);
                }


                if (recipeDiscraption.getEditText().getText().toString().equals(""))
                {
                    recipeDiscraption.setError("please enter discraption recipe");
                }
                else
                {
                    recipeDiscraption.setError(null);
                }

                if (stepList.size() == 0)
                {
                    MyToast.showLong(this,"Unable to save without any recipe steps");
                    return;
                }

                if (ingredientsList.size() == 0)
                {
                    MyToast.showLong(this,"Unable to save without any recipe ingredients");
                    return;
                }


            }

        }

        if (v == back) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateRecipeActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.create_recipe_backpress_dialog, null);
            getOut = mView.findViewById(R.id.getOut);
            stay = mView.findViewById(R.id.stay);
            mBuilder.setView(mView);
            dialog = mBuilder.create();
            getOut.setOnClickListener(this);
            stay.setOnClickListener(this);
            dialog.show();
        }

        if(v == stay)
        {
            dialog.dismiss();
        }
        if(v == getOut)
        {
            super.onBackPressed();
            this.finish();
        }


        if (preparationTimeBtn == v) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = 0;
            int mMinute = 30;
            String txtTime;
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            preparationTimeBtn.setText(hourOfDay + ":" + minute);
                            preparationTime = hourOfDay + ":" + minute;
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.getWindow().setTitle("Preparation Time");
            timePickerDialog.show();
        }

        if (addPictureMain == v) {
            if (ActivityCompat.checkSelfPermission(CreateRecipeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CreateRecipeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                    return;
            } else {
                launchGalleryIntent(MAIN_PIC);
            }
        }

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
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    launchGalleryIntent(REQUEST_EXTERNAL_STORAGE);
                } else {
                    // permission denied
                    MyToast.showLong(this, "Permission denied check permission in phone settings");
                }
                return;
            }
            case MAIN_PIC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchGalleryIntent(MAIN_PIC);
                } else {
                    // permission denied
                    MyToast.showLong(this, "Permission denied check permission in phone settings");
                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
        {
            Glide.with(this).load(R.drawable.image_workshop).into(displays_image);

        }
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK) {
//            Load the image into bitmaps arr
            final List<String> bitmaps = new ArrayList<>();
            //single image selected (accepted)
            stepImageUri = null;
            if (data.getData() != null) {
                Uri imageUri = data.getData();
                stepImageUri = imageUri.toString();
//                imageUriArr.add(imageUri);

                Glide.with(this).load(imageUri).into(displays_image);
            }else{
                MyToast.showShort(this,"You can select one image");
            }
        } else {
            if (requestCode == MAIN_PIC && resultCode == RESULT_OK) {
                mainPicsArrayUri.clear();
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    imagesEncodedList = new ArrayList<>();
                    slideModel.clear();
                    if (mClipData.getItemCount() <= 4) {
                        for (int i = 0; (i < mClipData.getItemCount() && i < 4); i++) {
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mainPicsArrayUri.add(uri);
                            slideModel.add(new SlideModel(item.getUri().toString(), ScaleTypes.FIT));
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();
                        }
                        imageSlider.setImageList(slideModel);
                        Log.v("LOG_TAG", "Selected Images" + mainPicsArrayUri.size());
                    } else {
//                        if the user try to load more then 4 pics make Toast
                        MyToast.showShort(this,"You can only select 4 images");

                    }
                } else {

                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        slideModel.clear();
                        mainPicsArrayUri.clear();
                        mainPicsArrayUri.add(uri);
                        slideModel.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
                        imageSlider.setImageList(slideModel);
                    }
                }
            }
        }
    }

    public interface FirebaseCallback {
        void onCallback(ArrayList<Uri>  uri,int flag,int index ,String image);
    }

    public void uploadPicture(ArrayList<String> imageUriArr, ArrayList<Uri> mainPicsArrayUri,FirebaseCallback firebaseCallback) {
        progressDialog.show();
        if (imageUriArr.size() != 0)
        {
            for (int i = 0; i < imageUriArr.size(); i++) {

                storageReference = storage.getReference("Recipe");
                StorageReference riversRef = storageReference.child(lastRecipeId + 1 + "").child("StepsImages").child(i + "");
                int finalI = i;
                riversRef.putFile(Uri.parse(imageUriArr.get(i)))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                imageCount += 1;
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
//                                        stepsImagesDownloadUri.add(finalI,uri);
                                        firebaseCallback.onCallback(stepsImagesDownloadUri,1, finalI,uri.toString());
                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
//                                stepsImagesDownloadUri.add(finalI,Uri.parse("null"));
                                firebaseCallback.onCallback(stepsImagesDownloadUri,1,finalI , "null");

                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progressPrecent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Main Image: " + imageCount + " :" + (int) progressPrecent + "%");
                    }
                });
            }
        }




        for (int i = 0; i < mainPicsArrayUri.size(); i++) {
            StorageReference riversRef = storageReference.child(lastRecipeId + 1 + "").child("MainImages").child(i + "");
            int finalI = i;
            riversRef.putFile(mainPicsArrayUri.get(i))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            MyToast.showShort(CreateRecipeActivity.this, "Seccess");
                            imageCount += 1;
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mainImagesDownloadUri.add(uri);
                                    firebaseCallback.onCallback(mainImagesDownloadUri,2, finalI,null);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            MyToast.showShort(getBaseContext(),"Error");
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progressPrecent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Steps Image: " + imageCount + " :" + (int) progressPrecent + "%");
                }
            });
        }

    }


    public void CheckLastId() {
        /*here we check the last recipe id
          and load into lastRecipeId
          we call it after save button cliced
         */
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipe");
        myRef.child("lastRecipeId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue().toString();
                lastRecipeId = Integer.parseInt(value);
                Log.d("here:", "" + lastRecipeId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void insertRecipe() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef,myRef2;
//        myRef:to Recipe details
//        myRef2:to user info
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        HashMap<String, HashMap<String, String>> mainHushMap = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> detailsMap = new HashMap<String, String>();
        detailsMap.put("email", fUser.getEmail().toString());
        detailsMap.put("RecipeName", recipeName.getEditText().getText().toString());
        detailsMap.put("Descraptions", recipeDiscraption.getEditText().getText().toString());
        detailsMap.put("Category", dropCategory.getText().toString());
        detailsMap.put("preparationTime", preparationTime);
        detailsMap.put("id", fUser.getUid());
        detailsMap.put("dateMake", currentDate);
        HashMap<String, HashMap<String, String>> ingredientsMap = new HashMap<String, HashMap<String, String>>();
        for (int i = 0; i < ingredientsList.size(); i++) {
            HashMap<String, String> ingridientsInfo = new HashMap<String, String>();
            ingridientsInfo.put("ingredientName", ingredientsList.get(i));
            ingridientsInfo.put("ingredientDownloadUri", imageIngredientsList.get(i));
            ingridientsInfo.put("ingredientAmount", amountList.get(i));
            ingredientsMap.put(i + "", ingridientsInfo);
        }
        HashMap<String, String> stepsMap = new HashMap<String, String>();

        mainHushMap.put("Details", detailsMap);

        lastRecipeId = (lastRecipeId + 1);
        Log.d("lastId:", "" + lastRecipeId);
        myRef = database.getReference("recipe");


        myRef.child(lastRecipeId + "").setValue(mainHushMap);
//                myRef.child(lastRecipeId + "").child("RecipeImages").child("StepsImages").setValue(stepsImagesDownloadUri);
        myRef.child(lastRecipeId + "").child("ingredients").setValue(ingredientsMap);


        for (int i = 0; i < stepList.size(); i++) {
            stepsMap.put(i + "", stepList.get(i));
        }
        myRef.child(lastRecipeId + "").child("Steps").child("StepsList").setValue(stepsMap);

        myRef.child(lastRecipeId + "").child("views").setValue("0");
//                Likes
        myRef.child(lastRecipeId + "").child("Likes").child("Amount").setValue("0");
        //        Hash map for the steps pics DownloadsUri

        HashMap<String, String> stepsImagesDownloadUri = new HashMap<String, String>();
        //        Hash map for the main pics DownloadsUri
        HashMap<String, String> mainImagesDownloadUri = new HashMap<String, String>();
        ArrayList<String> imgs = new ArrayList<>();



        uploadPicture(stepImages, mainPicsArrayUri, new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Uri> uri,int flag , int index ,String image) {
                /**
                 * flag = 1 - steps images upload complete
                 * flag = 2 - main images upload complete
                 * save the links in firebase recipe details collections
                 */
                if (flag == 1 ) {

                    stepsImagesDownloadUri.put(index + "", image);
                    if (stepImages.size() == stepsImagesDownloadUri.size())
                        {
                            myRef.child(lastRecipeId + "").child("Steps").child("StepsImages").setValue(stepsImagesDownloadUri);
                        }else{
                        return;
                    }
                }
                if (flag == 2) {
                    for (int i = 0; i < CreateRecipeActivity.this.mainImagesDownloadUri.size(); i++) {
                        mainImagesDownloadUri.put(i + "", CreateRecipeActivity.this.mainImagesDownloadUri.get(i).toString());
                        Log.d("URIIIIIIIIIIIIIIIIIIII", CreateRecipeActivity.this.mainImagesDownloadUri.get(i).toString());
                    }
                    myRef.child(lastRecipeId + "").child("MainImages").setValue(mainImagesDownloadUri);
                }
            }
        });


        myRef2 = database.getReference("users");
        myRef2.child(fUser.getUid()).child("recipes").child(lastRecipeId+"").setValue(lastRecipeId);
        myRef.child("lastRecipeId").setValue(lastRecipeId);
        myRef.child(lastRecipeId+"").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /**
                 * checking if all images uploaded and finish Dialog & activity
                 */
                if (snapshot.child("MainImages").child("0").exists() && snapshot.child("Steps").child("StepsList").exists()){
                    if (stepsImagesDownloadUri.size() == 0 && snapshot.child("MainImages").exists())
                    {
                        if (snapshot.child("MainImages").getChildrenCount() == mainImagesDownloadUri.size()){
                            progressDialog.dismiss();
                            finish();
                        }
                    }else
                    {
                        if (snapshot.child("Steps").child("StepsImages").exists() && snapshot.child("MainImages").exists() )
                        {
                            if (snapshot.child("Steps").child("StepsImages").getChildrenCount() == stepsImagesDownloadUri.size() && snapshot.child("MainImages").getChildrenCount() == mainImagesDownloadUri.size() )
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CreateRecipeActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.create_recipe_backpress_dialog, null);
        getOut = mView.findViewById(R.id.getOut);
        stay = mView.findViewById(R.id.stay);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        getOut.setOnClickListener(this);
        stay.setOnClickListener(this);
        dialog.show();    }

    private void fillCountryList() {
        innList = new ArrayList<>();
        nameIngredient = Arrays.asList(getResources().getStringArray(R.array.ingredients));
        uriIngredient = Arrays.asList(getResources().getStringArray(R.array.uri));
        for (int i = 0; i <nameIngredient.size() ; i++) {
            innList.add(new IngredientsItemClass(nameIngredient.get(i),uriIngredient.get(i)));
        }

    }

//    public  int getPosition(String nameIngredientTemp)
//    {
//        int position = 0;
//        for (int i = 0; i < nameIngredient.size(); i++) {
//            if(nameIngredientTemp.equals(nameIngredient.get(i)))
//            {
//                return position = i;
//
//            }
//        }
//        return 0;
//    }
}
