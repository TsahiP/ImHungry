package com.example.imhungry3.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;




public class AdapterRecyclerIngredients extends RecyclerView.Adapter<AdapterRecyclerIngredients.ViewHolder> {

        private static final String TAG = "RecyclerViewAdapter";

        //vars
        private ArrayList<String> Names;
        private ArrayList<String> amount ;
        private ArrayList<String> ImageUrls ;
        private Context mContext;

        public AdapterRecyclerIngredients(Context context, ArrayList<String> names, ArrayList<String> amount, ArrayList<String> imageUrls) {
            this.Names = names;
            this.amount = amount;
            this.ImageUrls = imageUrls;
            mContext = context;
        }


        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recipe_ingredients, parent, false);
            return new ViewHolder(view);
        }


        public void onBindViewHolder(ViewHolder holder, final int position) {
            Log.d(TAG, "onBindViewHolder: called.");

            Glide.with(mContext)
                    .asBitmap()
                    .load(ImageUrls.get(position))
                    .into(holder.image);

            holder.name.setText(Names.get(position));
            holder.amount.setText(amount.get(position));
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: clicked on an image: " + Names.get(position));
                    MyToast.showShort(mContext, Names.get(position));
                }
            });
        }

        public int getItemCount() {
            return ImageUrls.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            ImageView image;
            TextView name;
            TextView amount;

            public ViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image_view);
                name = itemView.findViewById(R.id.name);
                amount = itemView.findViewById(R.id.Amount);
            }
        }
}


