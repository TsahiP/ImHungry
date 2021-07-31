package com.example.imhungry3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.Activity.WorkShop.DisplayWorkShopActivity;
import com.example.imhungry3.Activity.WorkShop.WorkShopActivity;
import com.example.imhungry3.R;
import com.example.imhungry3.Class.WorkShopClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class AdapterRecyclerWorkshops extends RecyclerView.Adapter<AdapterRecyclerWorkshops.MyViewHolder> {
    private Context mContext;
    private List<WorkShopClass> workShops;
    StorageReference mStorageRef;
    String imgUri;
    GoogleMap mgoogleMap;
    SlidingUpPanelLayout layout;


    public AdapterRecyclerWorkshops(Context mContext, List<WorkShopClass> workShops, WorkShopActivity workShopActivity, GoogleMap mgoogleMap, SlidingUpPanelLayout layout) {
        this.mContext = mContext;
        this.workShops = workShops;
        this.mgoogleMap=mgoogleMap;
        this.layout = layout;

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener, Serializable {

        public ImageView img;
        public TextView title, descraption, price, date;


        GoogleMap googleMap;

        public MyViewHolder(@NonNull View itemView, GoogleMap googleMap) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imageWorkShop);
            title = (TextView) itemView.findViewById(R.id.titleClasses);
            descraption = (TextView) itemView.findViewById(R.id.descriptionClasses);
            price = (TextView) itemView.findViewById(R.id.priceClasses);
            date = (TextView) itemView.findViewById(R.id.textFieldDate);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.googleMap =googleMap;


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
        }

        @Override
        public boolean onLongClick(View v) {

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getLocationFromAddress(mContext, workShops.get(getAdapterPosition()).getWkLocation()), 13));
            layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return true;
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_workshop, parent, false);
        return new MyViewHolder(view,mgoogleMap);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final WorkShopClass wShop = workShops.get(position);
        imgUri = null;
        holder.title.setText(wShop.getWkName());
        holder.descraption.setText(wShop.getWkDescraption());
        holder.price.setText(wShop.getWkPrice());
        holder.date.setText(wShop.getWkDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DisplayWorkShopActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("wk", workShops.get(position));
                intent.putExtras(bundle);
                intent.putExtra("workShopId",wShop.getWkId());


                mContext.startActivity(intent);

            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://imhungry3-e0932.appspot.com").child("workShopImages");
        mStorageRef.child(wShop.getWkId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imgUri = uri.toString();
//                imageViewClasses.setImageURI(uri);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.error(R.drawable.image_workshop);
                Glide.with(mContext)
                        .setDefaultRequestOptions(requestOptions)
                        .load(imgUri)
                        .into(holder.img);
                workShops.get(position).setWkPicUrl(imgUri);
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });



    }

    @Override
    public int getItemCount() {
        return workShops.size();
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }


}

