package com.example.imhungry3.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.imhungry3.Activity.Search.ResultSearchActivity;
import com.example.imhungry3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FragmentSearchName extends Fragment implements View.OnClickListener {

    FloatingActionButton floatingActionButton;
    EditText searchNameRecipe;
    Button btnCategory;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public FragmentSearchName() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_name, container, false);
        // Inflate the layout for this fragment
        floatingActionButton = v.findViewById(R.id.floatSearchName);
        searchNameRecipe = v.findViewById(R.id.searchNameRecipe);
        btnCategory = v.findViewById(R.id.buttonCatgory);
        floatingActionButton.setOnClickListener(this);
        btnCategory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getContext(), btnCategory);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.category_recipe, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        btnCategory.setText(item.getTitle());
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method
        return v;
    }

    @Override
    public void onClick(View v) {
        if(v == floatingActionButton)
        {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("recipe");
//        send request to get the data
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                get Details
                    DataSnapshot numRecipe = snapshot;
                    Iterable<DataSnapshot> numberCildren = numRecipe.getChildren();
                    ArrayList<String> idRecipe = new ArrayList<>();
                    long count = numRecipe.getChildrenCount();
                    for (DataSnapshot data : numberCildren) {
                        count--;
                        if(count==0)
                        {
                            break;
                        }
                        if(btnCategory.getText().toString().toLowerCase().equals("category")){
                            if(data.child("Details").child("RecipeName").getValue().toString().contains(searchNameRecipe.getText().toString()))
                            {
                                idRecipe.add(data.getKey());
                            }
                        }else{
                            if(data.child("Details").child("Category").getValue().toString().equals(btnCategory.getText().toString()))
                            {
                                if(data.child("Details").child("RecipeName").getValue().toString().contains(searchNameRecipe.getText().toString()))
                                {
                                    idRecipe.add(data.getKey());
                                }

                            }
                        }
                    }

                    searchNameRecipe.setText("");
                    btnCategory.setText(getResources().getString(R.string.category));
                    Intent intent = new Intent(getContext(), ResultSearchActivity.class);
                    intent.putExtra("list",idRecipe);
                    startActivity(intent);
                }


                @Override
                public void onCancelled (@NonNull DatabaseError error){

                }
            });
        }
    }
}