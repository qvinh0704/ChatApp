package com.example.quangvinh.chatapprx.Service;

import android.support.annotation.NonNull;

import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quickblox.content.model.QBFile;

import rx.Observable;
import rx.Subscriber;


/**
 * Created by QuangVinh on 3/6/2017.
 */

public class FirebaseData {
    DatabaseReference mFirebaseDatabase;
    public static final String NODE_USER = "users";

    public FirebaseData() {
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public Observable<Boolean> addUser(User user, QBFile file) {
        user.setUrlImage(file.getPublicUrl());
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                mFirebaseDatabase.child(NODE_USER).push().setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        subscriber.onNext(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        subscriber.onError(new Throwable(Const.ERROR_ADDUSER_FIREBASE));
                        LogUtil.error(Const.ERROR_ADDUSER_FIREBASE + e.getMessage());
                    }
                });
            }
        });
    }

    public Observable<User> getUserData(final String email, User tempUser) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                mFirebaseDatabase.child(NODE_USER).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.hasChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                if (user.getEmail().equals(email)) {
                                    if (tempUser == null) {
                                        user.setUserID(dataSnapshot.getKey());
                                    } else {
                                        tempUser.setUserID(dataSnapshot.getKey());
                                        tempUser.setPassQuickblox(user.getPassQuickblox());
                                        tempUser.setLoginQuickblox(user.getLoginQuickblox());
                                        tempUser.setEmail(user.getEmail());
                                        tempUser.setPassword(user.getPassword());
                                        tempUser.setUrlImage(user.getUrlImage());
                                        tempUser.setSex(user.getSex());
                                        tempUser.setName(user.getName());
                                    }
                                    subscriber.onNext(user);
                                }
                            }
                        } else {
                            subscriber.onError(new Throwable("Can not get information"));
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    public Observable<Boolean> updateUser(User user, QBFile avatar) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (avatar != null)
                    user.setUrlImage(avatar.getPublicUrl());
                mFirebaseDatabase.child(NODE_USER).child(user.getUserID()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        subscriber.onNext(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        subscriber.onError(new Throwable(e.getMessage()));
                        LogUtil.error(e.getMessage());
                    }
                });
            }
        });
    }

    public Observable<User> getUserByQBUserID(String id){
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                mFirebaseDatabase.child(NODE_USER).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.hasChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null && user.getQbuserID() != null) {
                                if (user.getQbuserID().equals(id)) {
                                    subscriber.onNext(user);
                                }
                            }
                        } else {
                            subscriber.onError(new Throwable("Can not get information"));
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        });
    }
}
