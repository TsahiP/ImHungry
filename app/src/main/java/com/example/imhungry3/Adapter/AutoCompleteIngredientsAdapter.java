package com.example.imhungry3.Adapter;


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
import com.example.imhungry3.Class.IngredientsItemClass;
import com.example.imhungry3.R;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteIngredientsAdapter extends ArrayAdapter<IngredientsItemClass> {
    private List<IngredientsItemClass> IngredientsListFull;

    public AutoCompleteIngredientsAdapter(@NonNull Context context, @NonNull List<IngredientsItemClass> countryList) {
        super(context, 0, countryList);
        IngredientsListFull = new ArrayList<>(countryList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return IngredientsFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.adapter_ingredients_choice, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.ingredientsName);
        ImageView imageViewIngredient = convertView.findViewById(R.id.imagIngredients);

        IngredientsItemClass imagIngredientsItem = getItem(position);

        if (imagIngredientsItem != null) {
            textViewName.setText(imagIngredientsItem.getIngredientName());
            Glide.with(getContext())
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.logo).error(R.drawable.logo)).load(imagIngredientsItem.getIngredientImage()).into(imageViewIngredient);
        }

        return convertView;
    }

    private Filter IngredientsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<IngredientsItemClass> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(IngredientsListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (IngredientsItemClass item : IngredientsListFull) {
                    if (item.getIngredientName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((IngredientsItemClass) resultValue).getIngredientName();
        }
    };
}