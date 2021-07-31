package com.example.imhungry3.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.imhungry3.Activity.Recipe.RecipeDisplayActivity;
import com.example.imhungry3.Adapter.AdapterHome;
import com.example.imhungry3.Adapter.AdapterTop;
import com.example.imhungry3.Class.RecipeClass;
import com.example.imhungry3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FragmentTopRate extends Fragment {


    AdapterHome adapterHome;
    RecyclerView topRateRecyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    static ArrayList<RecipeClass> recipeClasses;
    private int index;
    private RecipeClass max;
    //DrawableCompat.setTint(progress, Color.WHITE);
    ArrayList<String> idRecipe;

    public FragmentTopRate() {
        recipeClasses = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fregment_top_rate, container, false);
        topRateRecyclerView = (RecyclerView) v.findViewById(R.id.top_rate_list);

        getRecipeData(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<RecipeClass> recipeClasses) {
                ArrayList<String> titleRecipes = new ArrayList<>();
                ArrayList<String> descriptionRecipes = new ArrayList<>();
                ArrayList<String> watch = new ArrayList<>();
                ArrayList<String> like = new ArrayList<>();
                ArrayList<String> imageRecpie = new ArrayList<>();
                idRecipe = new ArrayList<>();


                ArrayList<RecipeClass> topRate = sortTop(recipeClasses);

                for (int k = 0; k < topRate.size(); k++) {
                    titleRecipes.add(topRate.get(k).getNameRecipe());
                    descriptionRecipes.add(topRate.get(k).getDescription());
                    watch.add(topRate.get(k).getWatch());
                    like.add(topRate.get(k).getLike());
                    imageRecpie.add(topRate.get(k).getUrlImage());
                    idRecipe.add(topRate.get(k).getId());
                }


                adapterHome = new AdapterHome(getContext(), titleRecipes, descriptionRecipes, watch, like, imageRecpie, idRecipe);
                adapterHome.notifyDataSetChanged();
                topRateRecyclerView.setAdapter(adapterHome);
                topRateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            }
        });


//        send request to get the data


        return v;
    }

    private void getRecipeData(FirebaseCallback firebaseCallback) {
//        for tests only

//        set path
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("recipe");
//        send request to get the data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                get Details
                DataSnapshot numRecipe = snapshot;
                Iterable<DataSnapshot> numberCildren = numRecipe.getChildren();

                recipeClasses = new ArrayList<>();

                for (DataSnapshot data : numberCildren) {
                    if (!data.getKey().equals("lastRecipeId") && (
                            data.child("views").exists() &&
                                    data.child("Likes").exists() &&
                                    data.child("MainImages").exists() &&
                                    data.child("Details").child("RecipeName").exists() &&
                                    data.child("Details").child("Descraptions").exists()
                    )) {

                        RecipeClass temp = new RecipeClass();
                        temp.setWatch(data.child("views").getValue().toString());
                        temp.setLike(data.child("Likes").child("Amount").getValue().toString());

                        temp.setUrlImage(data.child("MainImages").child("0").getValue().toString());

                        temp.setNameRecipe(data.child("Details").child("RecipeName").getValue().toString());
                        temp.setDescription(data.child("Details").child("Descraptions").getValue().toString());
                        temp.setId(data.getKey());
                        recipeClasses.add(temp);

                    }
                }
                if(recipeClasses != null) {
                    firebaseCallback.onCallback(recipeClasses);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }


    public interface FirebaseCallback {
        void onCallback(ArrayList<RecipeClass> recipeClasses);

    }

    public ArrayList<RecipeClass> sortTop(ArrayList<RecipeClass> recipeClasses) {
        ArrayList<RecipeClass> topRate = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            index = 0;
            if (recipeClasses.size() > 0) {
                max = recipeClasses.get(0);
                for (int j = 0; j < recipeClasses.size(); j++) {
                    if (Integer.parseInt(recipeClasses.get(j).getLike()) > Integer.parseInt(max.getLike())) {
                        max = recipeClasses.get(j);
                        index = j;
                    }
                }
                topRate.add(max);
                recipeClasses.remove(index);
            }


        }


        return topRate;
    }
}