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

import java.util.ArrayList;


public class AdapterIngredientsDisplayResult extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> titleIngredients;
    ArrayList<String> imageIngredients;



    public AdapterIngredientsDisplayResult(Context c, ArrayList<String> titleIngredients, ArrayList<String> imageIngredients) {
        super(c, R.layout.adapter_ingredients_display_result,titleIngredients);
        this.context = c;
        this.titleIngredients = titleIngredients;
        this.imageIngredients = imageIngredients;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        @SuppressLint("RestrictedApi") LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.adapter_ingredients_display_result, parent, false);
        TextView textViewTitleIngredients = row.findViewById(R.id.ingredientsName2);
        ImageView imageViewIngredients = row.findViewById(R.id.imagIngredients2);

        Glide.with(context).load(imageIngredients.get(position)).into(imageViewIngredients);
        textViewTitleIngredients.setText(titleIngredients.get(position));



        return row;
    }



}