package com.example.imhungry3.Activity.Chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.Adapter.AdapterChat;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class DisplayChatFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String UID = "uid";
    private static final String IMAGE_URI = "imageUri";
    private static final String OTHER_USER_NAME = "imageUri";

    // TODO: Rename and change types of parameters
    private String mUid, mUri, msg_pointer;
    private EditText textToSend;
    private Button send;
    ImageView barTitle;
    RecyclerView chatMsgRecycler;
    private Boolean createPathFlag = false;

    //    firebase vars
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //    adapter
    AdapterChat adapterChat;
    List<ChatClass> messages;
    RecyclerView recyclerView;

    public DisplayChatFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DisplayChatFragment newInstance(String uid, String uri) {
        DisplayChatFragment fragment = new DisplayChatFragment();
        Bundle args = new Bundle();
        args.putString(UID, uid);
        args.putString(IMAGE_URI, uri);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUid = getArguments().getString(UID);
            mUri = getArguments().getString(IMAGE_URI);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_display_chat, container, false);
        textToSend = v.findViewById(R.id.textToSend_Et);
        send = v.findViewById(R.id.send);
        send.setOnClickListener(this);
        chatMsgRecycler = v.findViewById(R.id.chatMsgRecycler);
        barTitle = v.findViewById(R.id.im_hungry_bar_title);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.image_workshop);
        try {
            Glide.with(getContext()).setDefaultRequestOptions(requestOptions)
                    .load(mUri)
                    .into(barTitle);
        } catch (Exception e) {

        }
        messages = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chackChatExist(mUid);
        recyclerView = v.findViewById(R.id.chatMsgRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        return v;
    }

    private void chackChatExist(String otherUser) {
        /**
         * otherUser = Uid of the other user we want to conversation
         */
//        msg_pointer = null;
        messages = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    for (DataSnapshot i : snapshot.getChildren()) {
                        if ((i.child("userOne").getValue().toString().equals(firebaseUser.getUid()) && i.child("userTwo").getValue().toString().equals(mUid)) || (i.child("userTwo").getValue().toString().equals(firebaseUser.getUid()) && i.child("userOne").getValue().toString().equals(mUid))) {

                            msg_pointer = i.getKey();
                            messages.clear();
                            for (DataSnapshot j : i.child("chat").getChildren()) {
                                ChatClass temp = new ChatClass(j.child("sender").getValue().toString(),
                                        j.child("reciver").getValue().toString(),
                                        j.child("msg").getValue().toString()
                                        , j.child("sendTime").getValue().toString());
                                messages.add(temp);
                            }
                            break;
                        } else {
                            continue;
                        }
                    }
                }
                if (msg_pointer == null) {
                    createPathFlag = true;
                } else {

                    adapterChat = new AdapterChat(getContext(), messages, mUri);
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void createPath(GetMsgPointerCallBack getMsgPointerCallBack   ,String otherUser) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("chats");
        HashMap<String, String> users = new HashMap<>();
        users.put("userOne", firebaseUser.getUid());
        users.put("userTwo", otherUser);
        users.put("displayToUserOne", "true");
        users.put("displayToUserTwo", "true");

        databaseReference.push().setValue(users);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    if ((i.child("userOne").getValue().toString().equals(firebaseUser.getUid()) && i.child("userTwo").getValue().toString().equals(mUid)) || (i.child("userTwo").getValue().toString().equals(firebaseUser.getUid()) && i.child("userOne").getValue().toString().equals(mUid))) {
                        msg_pointer = i.getKey();
                        getMsgPointerCallBack.onCallback(msg_pointer);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String sender, String reciver, String msg) {
        /*
        msgPointer = msg id in database table
        msg = text to send
        reciver = reciver
        sender = sender
         */
//        check if we need to create path in FB first
        if (createPathFlag == true) {
            createPathFlag = false;
            createPath(new GetMsgPointerCallBack() {
                @Override
                public void onCallback(String msgP) {
                    //set referance
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference("chats").child(msg_pointer);
                    databaseReference.child("lastMsg").setValue(msg);
                    //        set send time
                    Locale aLocale = new Locale.Builder().setLanguage("iw").setRegion("IL").build();
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm", aLocale);
                    timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                    String curTime = timeFormat.format(new Date());
                    Log.d("Time hereeeeeeeee", " " + curTime);
//        ===============================================
                    databaseReference.child("lastTime").setValue(curTime);

                    databaseReference = firebaseDatabase.getReference("chats").child(msg_pointer).child("chat");

                    HashMap<String, String> map = new HashMap<>();
                    map.put("sendTime", curTime);
                    map.put("msg", msg);
                    map.put("sender", sender);
                    map.put("reciver", reciver);
                    databaseReference.push().setValue(map);
                    textToSend.setText("");
                    textToSend.setHint("text here");
                }
            }, mUid);
        }else
        {
            //set referance
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("chats").child(msg_pointer);
            databaseReference.child("lastMsg").setValue(msg);
            //        set send time
            Locale aLocale = new Locale.Builder().setLanguage("iw").setRegion("IL").build();
            DateFormat timeFormat = new SimpleDateFormat("HH:mm", aLocale);
            timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
            String curTime = timeFormat.format(new Date());
            Log.d("Time hereeeeeeeee", " " + curTime);
//        ===============================================
            databaseReference.child("lastTime").setValue(curTime);

            databaseReference = firebaseDatabase.getReference("chats").child(msg_pointer).child("chat");

            HashMap<String, String> map = new HashMap<>();
            map.put("sendTime", curTime);
            map.put("msg", msg);
            map.put("sender", sender);
            map.put("reciver", reciver);
            databaseReference.push().setValue(map);
            textToSend.setText("");
            textToSend.setHint("text here");
        }

    }

    @Override
    public void onClick(View v) {
        if (v == send) {
            if (textToSend.getText().toString().equals("")) {
                MyToast.showLong(getContext(),"You can not send an empty message");
            } else {
                String temp = textToSend.getText().toString();
                sendMessage(firebaseUser.getUid(), mUid, temp);
            }
        }
    }


    public interface GetMsgPointerCallBack {
        void onCallback(String msgP);
    }
}