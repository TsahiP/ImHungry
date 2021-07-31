package com.example.imhungry3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.Activity.Chat.ChatClass;
import com.example.imhungry3.Activity.Recipe.RecipeDisplayActivity;
import com.example.imhungry3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.ViewHolder> {

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

    public AdapterHome(Context mContext, ArrayList<String> titleRecipes, ArrayList<String> descriptionRecipes, ArrayList<String> watch,
                       ArrayList<String> like, ArrayList<String> imageRecipes , ArrayList<String> id){

        this.context = mContext;
        this.titleRecipes = titleRecipes;
        this.descriptionRecipes = descriptionRecipes;
        this.watch = watch;
        this.like = like;
        this.imageRecipes = imageRecipes ;
        this.id = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_top, parent, false);
        return new AdapterHome.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewTitleRecipes.setText(titleRecipes.get(position));
        holder.textViewDescriptionRecipes.setText(descriptionRecipes.get(position));
        holder.like.setText(like.get(position));
        holder.textViewWatch.setText(watch.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, RecipeDisplayActivity.class);
                i.putExtra("recipeId", id.get(position));
                context.startActivity(i);
            }
        });

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.image_workshop);
        try {
            Glide.with(context).setDefaultRequestOptions(requestOptions)
                    .load(imageRecipes.get(position))
                    .into(holder.circleImageView);
        } catch (Exception e) {

        }


    }

    @Override
    public int getItemCount() {
        return titleRecipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitleRecipes;
        public TextView textViewDescriptionRecipes ;
        public TextView textViewWatch;
        public TextView like ;
        public ImageView circleImageView ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitleRecipes = itemView.findViewById(R.id.recipeName);
            textViewDescriptionRecipes =itemView.findViewById(R.id.descraptionRecipe);
            textViewWatch = itemView.findViewById(R.id.shows);
            like = itemView.findViewById(R.id.like);
            circleImageView = itemView.findViewById(R.id.imageRecpie);
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        fUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (listChat.get(position).getSender().equals(fUser.getUid()))
//        {
//            return MSG_TYPE_RIGHT;
//        }else
//        {
//            return MSG_TYPE_LEFT;
//        }
//    }
}
