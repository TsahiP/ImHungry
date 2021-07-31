package com.example.imhungry3.Activity.Chat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imhungry3.Adapter.AdapterMessageUser;
import com.example.imhungry3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView msgRecycler;
    AdapterMessageUser adapterMessagesUser;
    //    arrays
    ArrayList<String> sendersUidArr, userNames, usersImages, chatId, lastMsg,lastTime;
    ImageButton back;
    //    FireBase
    FirebaseAuth firebaseAuth;
    FirebaseUser myUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference chatsRef;
    //    fragment displaychatpage
    private FrameLayout fragmentChatContainer;
    private Button sendBtn;
    private EditText textToSend;
    //test
    String tempUser = null;
    String imageTemp = "";
    String tempChatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        msgRecycler = findViewById(R.id.usersMessagesRecycler_xml);
        msgRecycler.setAdapter(adapterMessagesUser);
        msgRecycler.setLayoutManager(new LinearLayoutManager(this));
        firebaseAuth = FirebaseAuth.getInstance();
        back = findViewById(R.id.back_chatDisplay);
        back.setOnClickListener(this);
        myUser = firebaseAuth.getCurrentUser();

        DisplayActiveMessages(new GetSendersUidsCallback() {
            @Override
            public void onCallback(ArrayList<String> uid, ArrayList<String> chatId, ArrayList<String> lastMsg, ArrayList<String> lastTime) {
                getSendersDetails(new GetActiveChatCallBack() {
                    @Override
                    public void onCallback(ArrayList<String> uris, ArrayList<String> names ) {
//                        here notify recycler
                        msgRecycler.setAdapter(null);
                        adapterMessagesUser = new AdapterMessageUser(getApplication().getApplicationContext(), getSupportFragmentManager(), names, uris, sendersUidArr,lastMsg,lastTime);
                        msgRecycler.setAdapter(adapterMessagesUser);

                    }
                }, uid);
            }
        }, myUser.getUid());


        fragmentChatContainer = (FrameLayout) findViewById(R.id.chat_framelayout);

    }



    private void DisplayActiveMessages(GetSendersUidsCallback getSendersUidsCallback, String uid) {
        /**
         * otherUser = Uid of the other user we want to conversation
         */
        msgRecycler.setAdapter(null);
        firebaseDatabase = FirebaseDatabase.getInstance();
        chatsRef = firebaseDatabase.getReference("chats");
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    sendersUidArr = new ArrayList<>();
                    chatId = new ArrayList<>();
                    lastMsg = new ArrayList<>();
                    lastTime = new ArrayList<>();
                    for (DataSnapshot i : snapshot.getChildren()) {
                        if ((i.child("userOne").getValue().toString().equals(myUser.getUid()) || i.child("userTwo").getValue().toString().equals(myUser.getUid()))) {
                            if (i.child("userOne").getValue().toString().equals(myUser.getUid()) ) {
                                if (!chatId.contains(i.getKey())) {
                                    sendersUidArr.add(i.child("userTwo").getValue().toString());
                                    chatId.add(i.getKey());
                                    if (i.child("lastMsg").exists()&& i.child("lastTime").exists()){
                                        lastMsg.add(i.child("lastMsg").getValue().toString());
                                        lastTime.add(i.child("lastTime").getValue().toString());
                                    }else{
                                        lastMsg.add("");
                                        lastTime.add("");

                                    }
                                }
                            } else if (i.child("userTwo").getValue().toString().equals(myUser.getUid()) ) {
                                if (!chatId.contains(i.getKey())) {
                                    sendersUidArr.add(i.child("userOne").getValue().toString());
                                    chatId.add(i.getKey());
                                    if (i.child("lastMsg").exists()&& i.child("lastTime").exists()) {
                                        lastMsg.add(i.child("lastMsg").getValue().toString());
                                        lastTime.add(i.child("lastTime").getValue().toString());
                                    }else{
                                        lastMsg.add("");
                                        lastTime.add("");

                                    }
                                }
                            }

                        }
                    }
                    getSendersUidsCallback.onCallback(sendersUidArr, chatId, lastMsg, lastTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void getSendersDetails(GetActiveChatCallBack getActiveChatCallBack, ArrayList<String> sendersUid) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        chatsRef = firebaseDatabase.getReference().child("users");
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNames = new ArrayList<>();
                usersImages = new ArrayList<>();
                for (int i = 0; i < sendersUid.size(); i++) {
                    if (snapshot.child(sendersUid.get(i)).exists()) {
                        userNames.add(snapshot.child(sendersUid.get(i)).child("firstName").getValue().toString());
                        if (snapshot.child(sendersUid.get(i)).child("profilePicture").exists()) {
                            usersImages.add(snapshot.child(sendersUid.get(i)).child("profilePicture").getValue().toString());
                        } else {
                            usersImages.add("null");
                        }
                    }
                }
                getActiveChatCallBack.onCallback(usersImages, userNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == back)
        {
            msgRecycler.setVisibility(View.VISIBLE);
            onBackPressed();
        }
    }


    public interface GetSendersUidsCallback {
        void onCallback(ArrayList<String> uid, ArrayList<String> chatId , ArrayList<String> lastMsg, ArrayList<String> lastTime);
    }

    public interface GetActiveChatCallBack {
        void onCallback(ArrayList<String> uris, ArrayList<String> names );
    }
}