package com.example.imhungry3.Activity.Category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Random;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.Activity.Recipe.RecipeDisplayActivity;
import com.example.imhungry3.Adapter.AdapterRecipe;
import com.example.imhungry3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecipeCategoryActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<String> name, imageURL, recipeIds;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference myRef;
    private String[] category;
    private Intent intent;
    private TextView Beef, Chicken, Fish, Pasta, Soups, Saleds, Cakes, Cookies, pizza, Bread, vegetarian, Spread,todayRecipe_title;
    private ImageView todayRecipe_image;
    private ValueEventListener valueEventListener;
    private ImageButton back;
    private int randId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_category);
        category = getResources().getStringArray(R.array.category);
        Beef = findViewById(R.id.BeefMore);
        Chicken = findViewById(R.id.ChickenMore);
        Fish = findViewById(R.id.FishMore);
        Pasta = findViewById(R.id.PasataMore);
        Soups = findViewById(R.id.SoupsMore);
        Saleds = findViewById(R.id.SaladsMore);
        Cakes = findViewById(R.id.CakesMore);
        Cookies = findViewById(R.id.CookiesMore);
        pizza = findViewById(R.id.pizzaMore);
        back = findViewById(R.id.back_category);
        Bread = findViewById(R.id.BreadMore);
        vegetarian = findViewById(R.id.vegetarianMore);
        Spread = findViewById(R.id.SpreadMore);
        Beef.setOnClickListener(this);
        Chicken.setOnClickListener(this);
        Fish.setOnClickListener(this);
        Pasta.setOnClickListener(this);
        Soups.setOnClickListener(this);
        Saleds.setOnClickListener(this);
        Cakes.setOnClickListener(this);
        Cookies.setOnClickListener(this);
        pizza.setOnClickListener(this);
        Bread.setOnClickListener(this);
        vegetarian.setOnClickListener(this);
        Spread.setOnClickListener(this);
        intent = new Intent(RecipeCategoryActivity.this, RecipeListActivity.class);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        initRecyclerView();
    }

    private void initRecyclerView() {

        RecyclerView[] arrayReycler = new RecyclerView[12];
        arrayReycler[0] = findViewById(R.id.recycler_beef);
        arrayReycler[1] = findViewById(R.id.recycler_chicken);
        arrayReycler[2] = findViewById(R.id.recycler_fish);
        arrayReycler[3] = findViewById(R.id.recycler_pasta);
        arrayReycler[4] = findViewById(R.id.recycler_soup);
        arrayReycler[5] = findViewById(R.id.recycler_saled);
        arrayReycler[6] = findViewById(R.id.recycler_cake);
        arrayReycler[7] = findViewById(R.id.recycler_cookies);
        arrayReycler[8] = findViewById(R.id.recycler_pizza);
        arrayReycler[9] = findViewById(R.id.recycler_bread);
        arrayReycler[10] = findViewById(R.id.recycler_vegetarian);
        arrayReycler[11] = findViewById(R.id.recycler_spread);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        myRef = databaseReference.child("recipe");

        for (int i = 0; i < arrayReycler.length; i++) {
            int index = i;
            readData(new FirebaseCallback() {
                @Override
                public void onCallback(ArrayList<String> Names, ArrayList<String> Uri) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(RecipeCategoryActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    arrayReycler[index].setLayoutManager(layoutManager);
                    AdapterRecipe adapter = new AdapterRecipe(RecipeCategoryActivity.this, name, imageURL,recipeIds);
                    arrayReycler[index].setAdapter(adapter);

                }
            }, index);


        }



    }


    private void readData(FirebaseCallback firebaseCallback, int index) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    name = new ArrayList<>();
                    imageURL = new ArrayList<>();
                    recipeIds = new ArrayList<>();
                    int count = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if ((!ds.getKey().equals("lastRecipeId")) && ds.child("Details").child("Category").getValue().toString().equals(category[index])&&ds.child("MainImages").child("0").exists()) {
                            count++;
                            if (count != 10) {
                                recipeIds.add(ds.getKey());
                                name.add(ds.child("Details").child("RecipeName").getValue().toString());
                                imageURL.add(ds.child("MainImages").child("0").getValue().toString());
                            } else {
                                break;
                            }

                        }
                    }

                    firebaseCallback.onCallback(name, imageURL);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DatabaseError", error.getMessage());
            }
        };

        myRef.addValueEventListener(valueEventListener);

    }

    @Override
    public void onClick(View v) {
        if (v == todayRecipe_image)
        {
            intent = new Intent(RecipeCategoryActivity.this, RecipeDisplayActivity.class);
            intent.putExtra("recipeId",randId+"");
            startActivity(intent);

        }else {

            switch (v.getTag().toString()) {
                case "Beef":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
                case "Chicken":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
                case "Fish":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
                case "Pasta":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
                case "Soups":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
                case "Saleds":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
                case "Cakes":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
                case "Cookies":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
                case "pizza":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
                case "Bread":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
                case "vegetarian":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
                case "Spread":
                    intent.putExtra("category", v.getTag().toString());
                    startActivity(intent);
                    break;
            }
        }

    }

    public interface FirebaseCallback {
        void onCallback(ArrayList<String> Names, ArrayList<String> Uri);
    }

    @Override
    public void onBackPressed()
    {
        myRef.removeEventListener(valueEventListener);
        super.onBackPressed();
        this.finish();
    }

}


//        recomanded recipe values
//        todayRecipe_image = findViewById(R.id.todayRecipe_image);
//        todayRecipe_title = findViewById(R.id.todayRecipe_title);
//        todayRecipe_image.setOnClickListener(this);
//    public void todayRecomendedRecipe(){
//        Random rand = new Random();
//        int arrLen = name.size();
//        randId = rand.nextInt(arrLen);
//        randId = 1;
//        todayRecipe_title.setText(name.get(randId));
//        RequestOptions requestOptions = new RequestOptions();
//        try {
//            Glide.with(this).setDefaultRequestOptions(requestOptions)
//                    .load(imageURL.get(randId))
//                    .into(todayRecipe_image);
//        }catch (Exception e)
//        {
//
//        }
//
//
//    }