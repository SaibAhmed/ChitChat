package com.example.saibahmed.chitchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity {
    private ImageView mProfileImageView;
    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private DatabaseReference muUserDatabase;
    private String mCurrent_state;
    private DatabaseReference mfriendsRequestDatabase;
    private FirebaseUser mCureent_user;
    private  String user_id;
    private Button mProfileSendReqBtn;
    private Button mProfileDeclineReqBtn;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
//    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user_id = getIntent().getStringExtra("user_id");
        muUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mfriendsRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCureent_user= FirebaseAuth.getInstance().getCurrentUser();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mProfileImageView =(ImageView)findViewById(R.id.profile_displayImage);
        mProfileName=(TextView)findViewById(R.id.profile_displayName);
        mProfileStatus=(TextView)findViewById(R.id.profile_displayStatus);
        mProfileFriendsCount=(TextView)findViewById(R.id.profile_displayFriends);
        mProfileSendReqBtn = (Button)findViewById(R.id.profile_requestBtn);
        mProfileDeclineReqBtn =(Button)findViewById(R.id.profile_declinerequestBtn);
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        mCurrent_state="not_friends";
/*
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading user Data");
        mProgressDialog.setMessage("please wait while we load the user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
*/

        muUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Display_name =dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(Display_name);
                mProfileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.user).into(mProfileImageView);
                //mProgressDialog.dismiss();


                //-----------------------Friends List / Request Feature-------------------------------------------
                mfriendsRequestDatabase.child(mCureent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")){

                                mCurrent_state="req_received";
                                mProfileSendReqBtn.setText("Accept friend Request");

                                mProfileDeclineReqBtn.setVisibility(View.VISIBLE);
                                mProfileDeclineReqBtn.setEnabled(true);


                            }else if (req_type.equals("sent")){
                                 mCurrent_state = "req_sent";
                                 mProfileSendReqBtn.setText("Cancel friend Request");
                                 mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                 mProfileDeclineReqBtn.setEnabled(false);
                            }
                        }

                        else{

                            mFriendDatabase.child(mCureent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)){
                                        mCurrent_state="friends";
                                        mProfileSendReqBtn.setText("UnFriend this person");

                                        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                        mProfileDeclineReqBtn.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void sendRequest(View view){
        mProfileSendReqBtn.setEnabled(false);

        //------------------------------Not friends State--------------------------------
        if (mCurrent_state.equals("not_friends")){

            mfriendsRequestDatabase.child(mCureent_user.getUid()).child(user_id).child("request_type")
                    .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        mfriendsRequestDatabase.child(user_id).child(mCureent_user.getUid()).child("request_type")
                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                HashMap<String,String> notificationData = new HashMap<>();
                                notificationData.put("from",mCureent_user.getUid());
                                notificationData.put("type","request");

                                mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mCurrent_state="req_sent";
                                        mProfileSendReqBtn.setText("Cancel friend Request");
                                        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                        mProfileDeclineReqBtn.setEnabled(false);
                                        Toast.makeText(ProfileActivity.this, "request Send", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        });
                    }else {
                        Toast.makeText(ProfileActivity.this, "Failed sending Request", Toast.LENGTH_SHORT).show();
                    }

                    mProfileSendReqBtn.setEnabled(true);
                }
            });
        }


        //-------------------------cancel request State------------------------------------------

        if (mCurrent_state.equals("req_sent")){
            mfriendsRequestDatabase.child(mCureent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mfriendsRequestDatabase.child(user_id).child(mCureent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mProfileSendReqBtn.setEnabled(true);
                            mCurrent_state="not_friends";
                            mProfileSendReqBtn.setText("Send friend Request");
                            mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                            mProfileDeclineReqBtn.setEnabled(false);
                            Toast.makeText(ProfileActivity.this, "Request cancel", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }


        //-------------------------- Request received state---------------------------------------

        if (mCurrent_state.equals("req_received")){

            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

            mFriendDatabase.child(mCureent_user.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mFriendDatabase.child(user_id).child(mCureent_user.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mfriendsRequestDatabase.child(mCureent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mfriendsRequestDatabase.child(user_id).child(mCureent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProfileSendReqBtn.setEnabled(true);
                                            mCurrent_state="friends";
                                            mProfileSendReqBtn.setText("UnFriend this person");
                                            mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                            mProfileDeclineReqBtn.setEnabled(false);
                                            Toast.makeText(ProfileActivity.this, "Request cancel", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });


                        }
                    });

                }
            });

        }

        else if(mCurrent_state.equals("friends")){
            mFriendDatabase.child(mCureent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mFriendDatabase.child(user_id).child(mCureent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mProfileSendReqBtn.setEnabled(true);
                            mCurrent_state="not_friends";
                            mProfileSendReqBtn.setText("Send friend Request");
                            Toast.makeText(ProfileActivity.this, "UnFriend Successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }


    }
}
