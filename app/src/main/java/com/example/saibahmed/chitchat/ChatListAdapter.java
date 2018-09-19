package com.example.saibahmed.chitchat;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Activity myActivity;
    private DatabaseReference myDatabaseRef;
    private String myUserName;
    private ArrayList<DataSnapshot> mySnapShot;

    //Child event listener
    private ChildEventListener myListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            mySnapShot.add(dataSnapshot);
            notifyDataSetChanged();
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
    };

    //Constructor goes here

    public ChatListAdapter(Activity activity , DatabaseReference ref , String name){
         myActivity=activity;
         myDatabaseRef=ref.child("chats");
         myUserName=name;
         mySnapShot = new ArrayList<>();

         //add listener
        myDatabaseRef.addChildEventListener(myListener);
    }

    //static class
    static class ViewHolder{
        TextView senderName;
        TextView chatBody;
        LinearLayout.LayoutParams layoutParams;

    }


    @Override
    public int getCount() {
        return mySnapShot.size();
    }

    @Override
    public InstantMessage getItem(int i) {

        DataSnapshot snapshot = mySnapShot.get(i);
        return  snapshot.getValue(InstantMessage.class);
         
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
