package com.example.imhungry3.Activity.Category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.imhungry3.Activity.Recipe.RecipeDisplayActivity;
import com.example.imhungry3.Adapter.AdapterTop;
import com.example.imhungry3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class RecipeListActivity extends AppCompatActivity {
    private Intent intent;
    private AdapterTop adapterCategory;
    private ListView categoryRecipeAadapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<String> titleRecipes;
    private ArrayList<String> descriptionRecipes;
    private ArrayList<String> imageRecpie;
    private ArrayList<String> watch;
    private ArrayList<String> like;
    private TextView TVlike,TVwatch,TVname;
    private String category;
    private ImageView img;
    private ImageButton back;
    private ArrayList<String> idRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        back = findViewById(R.id.back_recipe);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        intent = getIntent();
        categoryRecipeAadapter = (ListView) findViewById(R.id.listCategory);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("recipe");
//        send request to get the data
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                get Details
                DataSnapshot numRecipe = snapshot;
                Iterable<DataSnapshot> numberCildren = numRecipe.getChildren();
                category = intent.getStringExtra("category");
                ArrayList<String> titleRecipes = new ArrayList<>();
                ArrayList<String> descriptionRecipes = new ArrayList<>();
                ArrayList<String> watch = new ArrayList<>();
                ArrayList<String> like = new ArrayList<>();
                ArrayList<String> imageRecpie = new ArrayList<>();
                idRecipe = new ArrayList<>();
                int count =0;

                for (DataSnapshot data : numberCildren)
                {
                    if (data.child("Details").child("Category").getValue() != null) {
                        if (data.child("Details").child("Category").getValue().toString().equals(category)) {
                            watch.add(data.child("views").getValue().toString());
                            like.add(data.child("Likes").child("Amount").getValue().toString());
                            imageRecpie.add(data.child("MainImages").child("0").getValue().toString());
                            titleRecipes.add(data.child("Details").child("RecipeName").getValue().toString());
                            descriptionRecipes.add(data.child("Details").child("Descraptions").getValue().toString());
                            idRecipe.add(data.getKey());
                        }
                    }
                }

               Random random = new Random();
               int recipeRandom = random.nextInt(titleRecipes.size());
               TVlike = findViewById(R.id.likeRecipe);
               TVwatch = findViewById(R.id.showsRecipe);
               img = findViewById(R.id.imgRecipe);
               TVname = findViewById(R.id.titleIn);
               TVname.setText(titleRecipes.get(recipeRandom));
                TVlike.setText(like.get(recipeRandom));
                TVwatch.setText(watch.get(recipeRandom));
                Glide.with(getBaseContext()).load(imageRecpie.get(recipeRandom)).into(img);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(RecipeListActivity.this, RecipeDisplayActivity.class);
                        i.putExtra("recipeId",idRecipe.get(recipeRandom));
                        startActivity(i);
                    }
                });

                adapterCategory =new AdapterTop(getBaseContext(),titleRecipes,descriptionRecipes,watch,like,imageRecpie,null);
                categoryRecipeAadapter.setAdapter(adapterCategory);
                categoryRecipeAadapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(RecipeListActivity.this, RecipeDisplayActivity.class);
                        i.putExtra("recipeId",idRecipe.get(position));
                        startActivity(i);
                    }
                });
            }


            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });








    }


}