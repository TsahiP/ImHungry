package com.example.imhungry3.Activity.WorkShop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imhungry3.Activity.UserPlace.PersonalAreaActivity;
import com.example.imhungry3.Adapter.AdapterWhoIn;
import com.example.imhungry3.Activity.Chat.DisplayChatFragment;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.Class.WorkShopClass;
import com.example.imhungry3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DisplayWorkShopActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView titleWorkshop, guests_min, guests_max, description, date, time, price, location,whatPrepare;
    private ImageView imageWorkshop;
    private Bundle bundle;
    private Intent intent;
    private ImageButton back;
    private String userInfo, userImage;
    //    ============= FireBase =================
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private WorkShopClass wk;
    private int currentGuests;
    private Activity c;
    private Dialog d;
    private FloatingActionButton sendMessage, edit, join, fab4;
    private boolean flag = true;
    private Button yes, no;
    private String id = "";
    int index = 1;
    private CountDownTimer mCountDownTimer;
    private ProgressDialog progressBar;
    //    Droper vars Who IN
    private AdapterWhoIn adapterWhoIn;
    private Button whoInDroper, username;
    private RecyclerView whoInRecycler;
    private ArrayList<Uri> uriRegisterdImages;
    private ArrayList<String> registerdList;
    private LinearLayout linearLayout;
//    open chat with creator


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_workshop);
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);//you can cancel it by pressing back button
        progressBar.setMessage("Registerd...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setCancelable(false);
        registerdList = new ArrayList<>();
        uriRegisterdImages = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            id = (String) b.get("workShopId");
        }

        databaseReference = firebaseDatabase.getReference("classess").child(id).child("guests");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot i : snapshot.getChildren()) {
                    if (i.getKey().equals(user.getUid())) {
                        join.setImageResource(R.drawable.ic_cancel_join);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getUserInfoForRegister(new UserCallBack() {
            @Override
            public void onCallback(String registerdUser, String uri) {
//                when user relevant data here set join click able
                userInfo = registerdUser;
                userImage = uri;
            }
        });
        whatPrepare = findViewById(R.id.tv_what_prepare);
        titleWorkshop = findViewById(R.id.tv_title_workshop);
        linearLayout = findViewById(R.id.linearout);
        username = findViewById(R.id.tv_username_workshop);
        username.setOnClickListener(this);
        imageWorkshop = findViewById(R.id.imageView_workshop);
        sendMessage = findViewById(R.id.send_message);
        sendMessage.setOnClickListener(this);
        description = findViewById(R.id.tv_description);
        date = findViewById(R.id.tv_date);
        time = findViewById(R.id.tv_time);
        price = findViewById(R.id.tv_price);
        guests_min = findViewById(R.id.tv_min_people);
        guests_max = findViewById(R.id.tv_max_people);
        location = findViewById(R.id.tv_location);
        join = findViewById(R.id.btn_join_workshop);
        edit = findViewById(R.id.Edit);
        edit.setOnClickListener(this);
        join.setOnClickListener(this);
        back = findViewById(R.id.back_displayWorkshop);
        back.setOnClickListener(this);
//        output id of the work shop
        intent = getIntent();
        bundle = intent.getExtras();
        if (bundle != null) {
            wk = (WorkShopClass) bundle.get("wk");

        }

        addWorkShopToList();
//        droper
        whoInDroper = findViewById(R.id.whoInDroperBtn);
        whoInDroper.setOnClickListener(this);
        whoInRecycler = findViewById(R.id.whoInRecycler);
        whoInRecycler.setAdapter(adapterWhoIn);
        whoInRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        checkGuests();
        updateRegisterdUsers(new RegisterdCallBack() {
            @Override
            public void onCallback(ArrayList<String> names, ArrayList<Uri> uri) {
                registerdList = names;
                uriRegisterdImages = uri;
                adapterWhoIn = new AdapterWhoIn(getApplication().getApplicationContext(), registerdList, uriRegisterdImages);
                whoInRecycler.setAdapter(adapterWhoIn);
            }
        });

        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    checkCreator();


                    fab4.setImageResource(R.drawable.ic_close_black);
                    flag = false;

                } else {
                    edit.hide();
                    sendMessage.hide();
                    join.hide();
                    edit.animate().translationY(0);
                    sendMessage.animate().translationY(0);
                    join.animate().translationY(0);

                    fab4.setImageResource(R.drawable.ic_add_black);
                    flag = true;

                }
            }
        });

    }

    public void checkGuests() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("classess").child(wk.getWkId()).child("Registerd");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                currentGuests = Integer.parseInt(snapshot.getValue() + "");
                guests_min.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public void updateRegisterdUsers(RegisterdCallBack registerdCallBack) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("classess").child(wk.getWkId()).child("guests");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> temp_names = new ArrayList<>();
                ArrayList<Uri> temp_images = new ArrayList<>();
                if (snapshot != null) {
                    for (DataSnapshot i : snapshot.getChildren()) {
                        temp_names.add(i.child("Name").getValue().toString());
                        if (i.child("ProfileImage").exists()) {
                            temp_images.add(Uri.parse(i.child("ProfileImage").getValue().toString()));
                        }

                    }
                }
                registerdCallBack.onCallback(temp_names, temp_images);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserInfoForRegister(UserCallBack userCallBack) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users").child(user.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo = snapshot.child("firstName").getValue().toString();
                if (snapshot.child("profilePicture").exists()) {
                    userImage = snapshot.child("profilePicture").getValue().toString();
                    userCallBack.onCallback(userInfo, userImage);

                } else {
                    userImage = "null";
                    userCallBack.onCallback(userInfo, "null");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface RegisterdCallBack {
        void onCallback(ArrayList<String> registerdUsers, ArrayList<Uri> uri);
    }

    public interface UserCallBack {
        void onCallback(String users, String uri);
    }


    public void addWorkShopToList() {
        String cutEmail = wk.getWkUsrEmail();
        username.setText(cutEmail.substring(0, cutEmail.indexOf('@')));

        titleWorkshop.setText(wk.getWkName());
        description.setText(wk.getWkDescraption());
        date.setText(wk.getWkDate());
        time.setText(wk.getWkStartTime() + " - " + wk.getWkEndTime());
        price.setText(wk.getWkPrice());
        guests_max.setText(wk.getWkMaxGuests());
        location.setText(wk.getWkLocation());
        guests_min.setText("" + currentGuests);
        whatPrepare.setText(wk.getWkWhatPrepare());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.image_workshop);
        Glide.with(this).setDefaultRequestOptions(requestOptions).load(wk.getWkPicUrl()).into(imageWorkshop);
    }

    public void checkCreator() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (wk.getWkUsrEmail().equals(user.getEmail())) {
//            if i create this workshop then give me Edit option
//            and set visble to edit button

            edit.show();
            edit.animate().translationY(-(fab4.getCustomSize()) + 25);
        } else {
            join.show();
            join.animate().translationY(-(fab4.getCustomSize()) + 25);
            sendMessage.show();
            sendMessage.animate().translationY(-(join.getCustomSize() + fab4.getCustomSize()));


        }
    }

    private void GetDataToSendMessage(send_message_interface msgInterface) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid, uri = "null";
                for (DataSnapshot i : snapshot.getChildren()) {
                    if (i.child("email").getValue().toString().equals(wk.getWkUsrEmail())) {
                        uid = i.getKey();
                        if (i.child("profilePicture").exists()) {
                            uri = i.child("profilePicture").getValue().toString();
                        }
                        msgInterface.onCallback(uid, uri);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if( v == back)
        {
            onBackPressed();
        }
        if (v == username) {
            Intent intentPersonalInfo = new Intent(DisplayWorkShopActivity.this, PersonalAreaActivity.class);
            intentPersonalInfo.putExtra("ifIsCreator", true);
            intentPersonalInfo.putExtra("idUser", wk.getWkIdUser());
            startActivity(intentPersonalInfo);
        }

        if (v == sendMessage) {
                GetDataToSendMessage(new send_message_interface() {
                    @Override
                    public void onCallback(String uid, String uri) {
                        Fragment displayChatFragment = DisplayChatFragment.newInstance(uid, uri);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_right, R.anim.enter_to_right, R.anim.exit_to_right);
                        transaction.addToBackStack(null);
                        transaction.add(R.id.chat_framelayout, displayChatFragment).commit();
                    }
                });
        }

        if (v == whoInDroper) {
            if (whoInRecycler.getVisibility() == View.VISIBLE) {
                whoInDroper.setBackgroundResource(R.drawable.down_24);
                whoInRecycler.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);

            } else {

                whoInRecycler.setVisibility(View.VISIBLE);
                whoInDroper.setBackgroundResource(R.drawable.up_24);
                linearLayout.setVisibility(View.GONE);

            }
        }

        if (join == v && join.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_join).getConstantState()) {
            progressBar.show();

//            checkGuests();
            int min = currentGuests, max = Integer.valueOf(wk.getWkMaxGuests());
            if (min < max) {
                id = wk.getWkId();
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("classess").child(id).child("guests");
                int min_temp = currentGuests;
                min_temp++;
                guests_min.setText(String.valueOf(min_temp));
                databaseReference = firebaseDatabase.getReference("classess").child(id).child("guests");
                databaseReference.child(user.getUid()).child("Name").setValue(userInfo);
                databaseReference.child(user.getUid()).child("ProfileImage").setValue(userImage);

                databaseReference = firebaseDatabase.getReference("classess").child(id);
                databaseReference.child("Registerd").runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        int registerd = Integer.parseInt(currentData.getValue() + "");
                        currentData.setValue(registerd + 1);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                    }
                });
//                databaseReference.child("Registerd").setValue(guests_min.getText());
                join.setImageResource(R.drawable.ic_cancel_join);


            } else {
                MyToast.showShort(getApplicationContext(), getString(R.string.workshop_full));
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    progressBar.dismiss();
                }
            }, 3000);

            return;
        }

        if (join == v && join.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_cancel_join).getConstantState()) {
            progressBar.show();
//            checkGuests();
            int min_temp = currentGuests;
            min_temp--;
            guests_min.setText(String.valueOf(currentGuests));
            databaseReference = firebaseDatabase.getReference("classess").child(id);
            databaseReference.child("Registerd").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    int registerd = currentData.getValue(Integer.class);
                    currentData.setValue(registerd - 1);
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                }
            });
//            databaseReference.child("Registerd").setValue(min_temp);

            databaseReference = firebaseDatabase.getReference("classess").child(id).child("guests");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot i : snapshot.getChildren()) {
                        if (i.getKey().equals(user.getUid())) {
                            i.getRef().removeValue();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            join.setImageResource(R.drawable.ic_join);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms

                    progressBar.dismiss();
                }
            }, 3000);

            return;
        }
        if (v == edit) {
            intent = new Intent(DisplayWorkShopActivity.this, EditWkShopActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("wk", wk);
            intent.putExtras(bundle);
            startActivity(intent);

        }


    }

    public interface send_message_interface {
        void onCallback(String uid, String uri);
    }


}
