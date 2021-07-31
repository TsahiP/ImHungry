package com.example.imhungry3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.Activity.Recipe.RecipeDisplayActivity;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;

import java.util.ArrayList;


public class AdapterRecipe extends RecyclerView.Adapter<AdapterRecipe.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    private ArrayList<String> recipeIds;
    private ArrayList<String> name;
    private ArrayList<String> ImageUrls ;
    private Context mContext;

    public AdapterRecipe(Context context, ArrayList<String> names, ArrayList<String> imageUrls, ArrayList<String> recipeIds) {
        this.name = names;
        this.ImageUrls = imageUrls;
        this.recipeIds = recipeIds;
        mContext = context;
    }


    public AdapterRecipe.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recipe, parent, false);
        return new ViewHolder(view);
    }



    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext).applyDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.image_workshop)
                .error(R.drawable.image_workshop))
                .load(ImageUrls.get(position))
                .into(holder.image);

        holder.name.setText(name.get(position));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on an image: " + name.get(position));
                MyToast.showShort(mContext, name.get(position));
                Intent intent = new Intent(mContext, RecipeDisplayActivity.class);
                intent.putExtra("recipeId", recipeIds.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return ImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.thumbnail);
            name = itemView.findViewById(R.id.title);
        }
    }
}


