package com.example.imhungry3.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class AdapterWhoIn extends RecyclerView.Adapter<AdapterWhoIn.MyViewHolder> {
    Context context;
    ArrayList<String> user;
    ArrayList<Uri> imageUser;
    DatabaseReference dataRef;
    FirebaseUser fbUser;
    private Object constraintLayout;

    public AdapterWhoIn(Context c, ArrayList<String> user, ArrayList<Uri> imageUser) {
        this.context = c;
        this.user = user;
        this.imageUser = imageUser;
    }


    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_who_in,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.userName.setText(user.get(position));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.image_workshop);
        try {
            Glide.with(context).setDefaultRequestOptions(requestOptions)
                    .load(imageUser.get(position))
                    .into(holder.profileImage);
        }catch (Exception e)
        {

        }


    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }
}
