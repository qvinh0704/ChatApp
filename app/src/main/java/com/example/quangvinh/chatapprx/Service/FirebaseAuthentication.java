package com.example.quangvinh.chatapprx.Service;

import android.app.Activity;
import android.support.annotation.NonNull;


import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by QuangVinh on 3/6/2017.
 */

public class FirebaseAuthentication {
    public static FirebaseAuth mFirebaseAuth;
    public static Activity mActivity;

    public FirebaseAuthentication(Activity tempActivity) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mActivity = tempActivity;
    }

    public Observable<FirebaseUser> signupFirebase(User user) {
        return Observable.create(new Observable.OnSubscribe<FirebaseUser>() {
            @Override
            public void call(Subscriber<? super FirebaseUser> subscriber) {
                mFirebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            subscriber.onNext(task.getResult().getUser());
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                subscriber.onError(new Throwable("Email is exist."));
                            } catch (Exception e) {
                                subscriber.onError(new Throwable(Const.ERROR_SIGNUP_FIREBASE));
                                LogUtil.error(Const.ERROR_SIGNUP_FIREBASE + e.getMessage());
                            }
                        }
                    }
                });
            }
        });
    }

    public Observable<Boolean> sendVerify(FirebaseUser firebaseUser) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        subscriber.onNext(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        subscriber.onError(new Throwable(Const.ERROR_SENDVERIFY_FIREBASE));
                        LogUtil.error(Const.ERROR_SENDVERIFY_FIREBASE + e.getMessage());
                    }
                });
            }
        });
    }

    public Observable<FirebaseUser> signinUserFirebase(User user) {
        return Observable.create(new Observable.OnSubscribe<FirebaseUser>() {
            @Override
            public void call(Subscriber<? super FirebaseUser> subscriber) {
                mFirebaseAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            if (firebaseUser.isEmailVerified()) {
                                subscriber.onNext(firebaseUser);

                            } else {
                                subscriber.onError(new Throwable("Email is not verified"));
                            }
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                LogUtil.error(e.getMessage());
                                subscriber.onError(new Throwable("Your email has not been registered"));
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogUtil.error(e.getMessage());
                                subscriber.onError(new Throwable(e.getMessage()));
                            }
                        }
                    }
                });
            }
        });
    }

    public Observable<Boolean> deleteUser_Firebase(FirebaseUser user) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public Observable<Boolean> resetPassword(String email) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                mFirebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(mActivity, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        subscriber.onNext(true);
                    }
                }).addOnFailureListener(mActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        subscriber.onError(new Throwable(e.getMessage()));
                    }
                });
            }
        });
    }

    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Observable<Boolean> updateUser(String password) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseUser.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}
