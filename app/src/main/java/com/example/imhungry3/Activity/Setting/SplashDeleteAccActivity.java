package com.example.imhungry3.Activity.Setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.imhungry3.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SplashDeleteAccActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    String idToDelete;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Bundle bundle = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        idToDelete = user.getUid();
        deleteChets(new EndDeleteCallBack() {
            @Override
            public void onCallback(Boolean flag) {
                deleteRecipe(new EndDeleteCallBack() {
                    @Override
                    public void onCallback(Boolean flag) {
                        deleteWorkShop(new EndDeleteCallBack() {
                            @Override
                            public void onCallback(Boolean flag) {
                                deleteAccount(new EndDeleteCallBack() {
                                    @Override
                                    public void onCallback(Boolean flag) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                finishAffinity();

                                            }
                                        }, 3000);
                                    }
                                }, user);
                            }
                        }, idToDelete);
                    }
                }, idToDelete);
            }
        }, idToDelete);


    }


    private void deleteAccount(EndDeleteCallBack endDeleteCallBack, FirebaseUser user) {
        String uid = user.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference deleteAccountFromUsersRef = firebaseDatabase.getReference("users").child(uid);
        deleteImagFromStorage("usersImages", uid);
        deleteAccountFromUsersRef.removeValue();
        endDeleteCallBack.onCallback(true);
        user.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {
                                              Log.d("user deleted? : ", "User account deleted.");
                                          }
                                      }
                ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("user deleted? : ", "" + e);

            }
        });

    }

    private void deleteImagFromStorage(String folderName, String id) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to the file to delete
        StorageReference desertRef = storageRef.child(folderName).child(id);

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Error
            }
        });
    }

    private void deleteWorkShop(EndDeleteCallBack endDeleteCallBack, String uid) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference deleteActiveWorkShop = firebaseDatabase.getReference("classess");
        deleteActiveWorkShop.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot workShop : snapshot.getChildren()) {
                    if (!workShop.getKey().equals("lastClassId")) {
                        if (workShop.child("id").getValue().toString().equals(uid)) {
                            String tempId = workShop.getKey();
                            deleteActiveWorkShop.child(tempId).removeValue();
                            deleteImagFromStorage("workShopImages", tempId);
                        }
                    }

                }
                endDeleteCallBack.onCallback(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deleteChets(EndDeleteCallBack endDeleteCallBack, String uid) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference deleteActiveChets = firebaseDatabase.getReference("chats");
        deleteActiveChets.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot chat : snapshot.getChildren()) {
                    if (chat.child("userOne").getValue().toString().equals(uid) || chat.child("userTwo").getValue().toString().equals(uid)) {
                        deleteActiveChets.child(chat.getKey()).removeValue();
                    }
                }
                endDeleteCallBack.onCallback(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteRecipe(EndDeleteCallBack endDeleteCallBack, String uid) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference deleteActiveRecipe = firebaseDatabase.getReference("recipe");
        deleteActiveRecipe.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot recipe : snapshot.getChildren()) {
                    if (recipe.child("Details").child("id").exists()) {
                        if (recipe.child("Details").child("id").getValue().toString().equals(uid)) {
                            deleteImagFromStorage("Recipe", recipe.getKey());
                            deleteActiveRecipe.child(recipe.getKey()).removeValue();
                        }
                    }
                }
                endDeleteCallBack.onCallback(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public interface EndDeleteCallBack {
        void onCallback(Boolean flag);
    }
}
