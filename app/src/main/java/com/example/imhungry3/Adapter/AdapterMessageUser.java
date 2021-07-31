package com.example.imhungry3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.Activity.Chat.DisplayChatFragment;
import com.example.imhungry3.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

//import  android.app.Fragment;

public class AdapterMessageUser extends RecyclerView.Adapter<AdapterMessageUser.MyViewHolder>  {
    Context context;
    ArrayList<String> user;
    ArrayList<String> imageUser;
    ArrayList<String> uids;
    ArrayList<String> lastMessage;
    ArrayList<String> lastTime;
    FragmentManager f_manger;


    public AdapterMessageUser(Context c, FragmentManager manger, ArrayList<String> user, ArrayList<String> imageUser, ArrayList<String> uids, ArrayList<String> lastMessage, ArrayList<String> lastTime) {
        this.context = c;
        this.user = user;
        this.imageUser = imageUser;
        this.uids = uids;
        this.lastMessage = lastMessage;
        this.f_manger = manger;
        this.lastTime = lastTime;
    }


    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_user_message,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                holder.lastTime.setText(lastTime.get(position));
                holder.userName.setText(user.get(position));
                holder.lastMsg.setText(lastMessage.get(position));
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.error(R.drawable.image_workshop);
                try {
                    Glide.with(context).setDefaultRequestOptions(requestOptions)
                            .load(imageUser.get(position))
                            .into(holder.profileImage);
                } catch (Exception e) {

                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        openChatFragment(uids.get(position), imageUser.get(position));
                    }
                });


    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public void openChatFragment(String uid, String uri)
    {
        Fragment displayChatFragment = DisplayChatFragment.newInstance(uid,uri);
        FragmentManager fragmentManager = f_manger;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right, R.anim.enter_to_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.chat_framelayout,displayChatFragment).commit();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userName,lastMsg,lastTime;
        CircleImageView profileImage;
        RelativeLayout constraintLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lastMsg = itemView.findViewById(R.id.tv_last_chat);
            lastTime = itemView.findViewById(R.id.tv_time);
            userName = itemView.findViewById(R.id.tv_user_name);
            profileImage = itemView.findViewById(R.id.iv_user_photo);
            constraintLayout = itemView.findViewById(R.id.parrentlayout);
            constraintLayout.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
        }
    }
}
