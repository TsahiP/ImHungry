package com.example.imhungry3.Adapter;

import android.content.Context;
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
import com.example.imhungry3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<ChatClass> listChat;
    private String userImageArr;
    FirebaseUser fUser;

    public AdapterChat(Context mContext, List<ChatClass> listChat, String userImageArr) {
        this.mContext = mContext;
        this.listChat = listChat;
        this.userImageArr = userImageArr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return new ViewHolder(view);
        }else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatClass chat = listChat.get(position);

        holder.show_message.setText(chat.getTextMsg());
        holder.time.setText(chat.getSendTime());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.image_workshop);
        try {
            Glide.with(mContext).setDefaultRequestOptions(requestOptions)
                    .load(userImageArr)
                    .into(holder.userImage);
        } catch (Exception e) {

        }


    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message,time;
        public ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            time = itemView.findViewById(R.id.tv_time);
            userImage = itemView.findViewById(R.id.user_send);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (listChat.get(position).getSender().equals(fUser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
