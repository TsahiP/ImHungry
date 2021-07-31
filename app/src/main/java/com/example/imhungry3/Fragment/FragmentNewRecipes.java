package com.example.imhungry3.Fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imhungry3.Adapter.AdapterHome;
import com.example.imhungry3.Class.RecipeClass;
import com.example.imhungry3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;


public class FragmentNewRecipes extends Fragment {


    private AdapterHome adapterHome;
    private RecyclerView recyclerNewRecipe;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    static ArrayList<RecipeClass> recipeClasses;
    LocalDate date;
    //DrawableCompat.setTint(progress, Color.WHITE);
    private ArrayList<String> idRecipe;

    public FragmentNewRecipes() {
        recipeClasses = new ArrayList<>();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fregment_new_recipe, container, false);
        recyclerNewRecipe =  v.findViewById(R.id.new_recipe_list);


        getRecipeData(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<RecipeClass> recipeClasses) {
                ArrayList<String> titleRecipes = new ArrayList<>();
                ArrayList<String> descriptionRecipes = new ArrayList<>();
                ArrayList<String> watch = new ArrayList<>();
                ArrayList<String> like = new ArrayList<>();
                ArrayList<String> imageRecpie = new ArrayList<>();
                idRecipe = new ArrayList<>();


                for (int k = 0; k < recipeClasses.size(); k++) {
                    titleRecipes.add(recipeClasses.get(k).getNameRecipe());
                    descriptionRecipes.add(recipeClasses.get(k).getDescription());
                    watch.add(recipeClasses.get(k).getWatch());
                    like.add(recipeClasses.get(k).getLike());
                    imageRecpie.add(recipeClasses.get(k).getUrlImage());
                    idRecipe.add(recipeClasses.get(k).getId());
                }


                adapterHome = new AdapterHome(getContext(), titleRecipes, descriptionRecipes, watch, like, imageRecpie, idRecipe);
                recyclerNewRecipe.setAdapter(adapterHome);
                recyclerNewRecipe.setLayoutManager(new LinearLayoutManager(getContext()));

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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                get Details
                DataSnapshot numRecipe = snapshot;
                Iterable<DataSnapshot> numberCildren = numRecipe.getChildren();
                recipeClasses = new ArrayList<>();
                int count = 0;

                for (DataSnapshot data : numberCildren) {
                    if (!data.getKey().equals("lastRecipeId") && (
                            data.child("views").exists() &&
                                    data.child("Likes").exists() &&
                                    data.child("MainImages").exists() &&
                                    data.child("Details").child("RecipeName").exists() &&
                                    data.child("Details").child("Descraptions").exists()
                    )) {
                        String arrTemp[] = data.child("Details").child("dateMake").getValue().toString().split("-");
                        date = LocalDate.of(Integer.valueOf(arrTemp[2]),Integer.valueOf(arrTemp[1]),Integer.valueOf(arrTemp[0]));
                        LocalDate date2 = LocalDate.now().minusDays(7);

                        if (date.isAfter(date2)) {
                            RecipeClass temp = new RecipeClass();
                            temp.setWatch(data.child("views").getValue().toString());
                            temp.setLike(data.child("Likes").child("Amount").getValue().toString());
                            temp.setUrlImage(data.child("MainImages").child("0").getValue().toString());
                            temp.setNameRecipe(data.child("Details").child("RecipeName").getValue().toString());
                            temp.setDescription(data.child("Details").child("Descraptions").getValue().toString());
                            temp.setId(data.getKey());
                            count++;
                            if (count == 10) {
                                break;
                            }
                            recipeClasses.add(temp);
                        }
                    }
                }
                firebaseCallback.onCallback(recipeClasses);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public interface FirebaseCallback {
        void onCallback(ArrayList<RecipeClass> recipeClasses);
    }
}