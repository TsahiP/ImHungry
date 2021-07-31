package com.example.imhungry3.Activity.Search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.imhungry3.Activity.Recipe.RecipeDisplayActivity;
import com.example.imhungry3.Adapter.AdapterTop;
import com.example.imhungry3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ResultSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listRecipe;
    private ArrayList<String> idRecipe;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ImageButton back;
    Bundle bundle;
    ArrayList<String> list;
    Intent intent;
    AdapterTop adapterTop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_search);

        listRecipe = findViewById(R.id.recipeResult);
        back = findViewById(R.id.back_result);
        back.setOnClickListener(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("recipe");
//        send request to get the data
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                get Details
                DataSnapshot numRecipe = snapshot;
                intent = getIntent();
                list =intent.getStringArrayListExtra("list");
                int len = list.size();
                list.remove(len-1);
                ArrayList<String> titleRecipes = new ArrayList<>();
                ArrayList<String> descriptionRecipes = new ArrayList<>();
                ArrayList<String> watch = new ArrayList<>();
                ArrayList<String> like = new ArrayList<>();
                ArrayList<String> imageRecpie = new ArrayList<>();
                idRecipe = new ArrayList<>();

                for (int k = 0; k < list.size(); k++) {
                            titleRecipes.add(snapshot.child(list.get(k)).child("Details").child("RecipeName").getValue().toString());
                            descriptionRecipes.add(snapshot.child(list.get(k)).child("Details").child("Descraptions").getValue().toString());
                            watch.add(snapshot.child(list.get(k)).child("views").getValue().toString());
                            like.add(snapshot.child(list.get(k)).child("Likes").child("Amount").getValue().toString());
                            imageRecpie.add(snapshot.child(list.get(k)).child("MainImages").child("0").getValue().toString());
                            idRecipe.add(list.get(k));

                }

                adapterTop =new AdapterTop(getBaseContext(),titleRecipes,descriptionRecipes,watch,like,imageRecpie,null);
                listRecipe.setAdapter(adapterTop);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        listRecipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ResultSearchActivity.this, RecipeDisplayActivity.class);
                i.putExtra("recipeId",idRecipe.get(position));
                startActivity(i);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == back){
            onBackPressed();
        }
    }
}