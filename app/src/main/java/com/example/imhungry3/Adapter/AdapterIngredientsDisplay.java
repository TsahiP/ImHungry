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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.R;

import java.util.ArrayList;


public class AdapterIngredientsDisplay extends RecyclerView.Adapter<AdapterIngredientsDisplay.ViewHolder> {
    Context context;
    ArrayList<String> titleIngredients;
    ArrayList<String> imageIngredients;
    ArrayList<String> amount;

    public AdapterIngredientsDisplay(Context context, ArrayList<String> titleIngredients, ArrayList<String> imageIngredients, ArrayList<String> amount) {
        this.context = context;
        this.titleIngredients = titleIngredients;
        this.imageIngredients = imageIngredients;
        this.amount = amount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_ingredients_display,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ingName.setText(titleIngredients.get(position));
        holder.ingAmount.setText(amount.get(position));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.image_workshop);
        try {
            Glide.with(context).setDefaultRequestOptions(requestOptions)
                    .load(imageIngredients.get(position))
                    .into(holder.ingImage);
        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return titleIngredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ingImage;
        TextView ingName,ingAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingName = itemView.findViewById(R.id.ingredientsName2);
            ingAmount = itemView.findViewById(R.id.etAmountDisplay);
            ingImage = itemView.findViewById(R.id.imagIngredients2);
        }
    }


//    public AdapterIngredientsDisplay(Context c, ArrayList<String> titleIngredients, ArrayList<String> imageIngredients,ArrayList<String> amount) {
//        super(c, R.layout.adapter_ingredients_display,titleIngredients);
//        this.context = c;
//        this.titleIngredients = titleIngredients;
//        this.imageIngredients = imageIngredients;
//        this.amount = amount;
//    }
//
//    @NonNull
//    @Override
//    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
//        @SuppressLint("RestrictedApi") LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View row = layoutInflater.inflate(R.layout.adapter_ingredients_display, parent, false);
//        TextView textViewTitleIngredients = row.findViewById(R.id.ingredientsName2);
//        TextView textViewAmount = row.findViewById(R.id.etAmountDisplay);
//        ImageView imageViewIngredients = row.findViewById(R.id.imagIngredients2);
//
//        Glide.with(context).load(imageIngredients.get(position)).into(imageViewIngredients);
//        textViewTitleIngredients.setText(titleIngredients.get(position));
//        textViewAmount.setText(amount.get(position));
//
//
//        return row;
//    }



}