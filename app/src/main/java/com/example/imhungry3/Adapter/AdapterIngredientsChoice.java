package com.example.imhungry3.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.R;

import java.util.ArrayList;
import java.util.List;


public class AdapterIngredientsChoice extends ArrayAdapter<String> {
    Context context;
    List<String> titleIngredients;
    List<String>imageIngredients;




    public AdapterIngredientsChoice(Context c, List<String> titleIngredients, List<String> imageIngredients) {
        super(c, R.layout.adapter_ingredients_choice, titleIngredients);
        this.context = c;
        this.titleIngredients = titleIngredients;
        this.imageIngredients = imageIngredients;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        @SuppressLint("RestrictedApi") LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.adapter_ingredients_choice, parent, false);
        TextView textViewTitleIngredients = row.findViewById(R.id.ingredientsName);
        ImageView imageViewIngredients = row.findViewById(R.id.imagIngredients);

        Glide.with(context)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.logo).error(R.drawable.logo)).load(imageIngredients.get(position)).into(imageViewIngredients);
        textViewTitleIngredients.setText(titleIngredients.get(position));


        return row;
    }



}