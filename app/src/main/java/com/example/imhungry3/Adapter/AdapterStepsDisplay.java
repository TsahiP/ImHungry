package com.example.imhungry3.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;

import java.util.ArrayList;


public class AdapterStepsDisplay extends RecyclerView.Adapter<AdapterStepsDisplay.MyViewHolder>  {
    Context context;
    ArrayList<String> imageSteps;
    ArrayList<String> infoStep;
    TextView numberStep_xml,infoStep_xml;
    static int RESULT_LOAD_IMAGE = 5;

//, ArrayList<String> number
    public AdapterStepsDisplay(Context c, ArrayList<String> infoStep, ArrayList<String> imageSteps) {
        this.context = c;
        this.imageSteps = imageSteps;
        this.infoStep = infoStep;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_steps_display,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.infoStep_xml.setText(infoStep.get(position));
        holder.numberStep_xml.setText(position+1+"");
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.image_workshop);
        if(position<imageSteps.size()) {
            Glide.with(context).setDefaultRequestOptions(requestOptions)
                    .load(imageSteps.get(position))
                    .into(holder.imageStep_xml);


        }else{

        }

    }

    @Override
    public int getItemCount() {
        return infoStep.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView numberStep_xml, infoStep_xml;
        ImageView imageStep_xml;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            numberStep_xml = itemView.findViewById(R.id.numberStep);
            infoStep_xml = itemView.findViewById(R.id.infoStep);
            imageStep_xml = itemView.findViewById(R.id.imageStep);
            imageStep_xml.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyToast.showShort(context, "Here");

                }
            });

        }




    }




}