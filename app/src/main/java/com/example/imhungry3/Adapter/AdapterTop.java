package com.example.imhungry3.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.imhungry3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterTop extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> id;
    ArrayList<String> titleRecipes;
    ArrayList<String> descriptionRecipes;
    ArrayList<String> watch;
    ArrayList<String> like;
    ArrayList<String> imageRecipes;
    DatabaseReference dataRef;
    FirebaseUser user;
    CircleImageView circleImageView;
    private Object constraintLayout;


    public AdapterTop(Context c, ArrayList<String> titleRecipes, ArrayList<String> descriptionRecipes, ArrayList<String> watch, ArrayList<String> like, ArrayList<String> imageRecipes ,ArrayList<String> id) {
        super(c, R.layout.adapter_top, R.id.recipeName, titleRecipes);
        this.context = c;
        this.titleRecipes = titleRecipes;
        this.descriptionRecipes = descriptionRecipes;
        this.watch = watch;
        this.like = like;
        this.imageRecipes = imageRecipes ;
        this.id = id;
    }




    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        @SuppressLint("RestrictedApi") LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.adapter_top, parent, false);
        TextView textViewTitleRecipes = row.findViewById(R.id.recipeName);
        TextView textViewDescriptionRecipes = row.findViewById(R.id.descraptionRecipe);
        TextView textViewWatch = row.findViewById(R.id.shows);
        TextView like = row.findViewById(R.id.like);
        ImageView circleImageView = row.findViewById(R.id.imageRecpie);


        textViewTitleRecipes.setText(titleRecipes.get(position));
        textViewDescriptionRecipes.setText(descriptionRecipes.get(position));
        textViewWatch.setText(watch.get(position));
        like.setText(this.like.get(position));
        if(imageRecipes.size() != 0)
        {
            Glide.with(getContext()).load(this.imageRecipes.get(position)).into(circleImageView);
        }

//        Uri myUri = Uri.parse(imageRecipes.get(position));
//        imageViewRecpie.setImageURI(myUri);
        user = FirebaseAuth.getInstance().getCurrentUser();

        return row;
    }



}