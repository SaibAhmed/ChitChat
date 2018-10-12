package com.example.saibahmed.chitchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainChatActivity extends AppCompatActivity {

    private String myUserName;
    private ListView myChatListView;
    private EditText myChatText;
    private ImageButton mySendChatButton;
    private DatabaseReference myDatabaseRef;
    private Toolbar mToolbar;



    private ChatListAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        setUpDisplayName();

        //setting appbar
        mToolbar =findViewById(R.id.mainChat_page_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        getSupportActionBar().setTitle("ChitChat");


        myDatabaseRef = FirebaseDatabase.getInstance().getReference();

        //Get UI elements refs
        myChatListView = (ListView)findViewById(R.id.chat_list);
        myChatText =(EditText)findViewById(R.id.message_input);
        mySendChatButton = (ImageButton)findViewById(R.id.send_button);

        //push chat to firebase on button tapped
        mySendChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushChatToFirebase();
            }
        });

        //call push method on keyboardEvent

        myChatText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                pushChatToFirebase();
                return true;
            }
        });



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,main_activity.class);
        startActivity(intent);
    }

    //push chat to firebase
    private void pushChatToFirebase(){
        String chatInput = myChatText.getText().toString();
        if (!chatInput.equals("")){
            InstantMessage chat = new InstantMessage(chatInput,myUserName);
            myDatabaseRef.child("chats").push().setValue(chat);
            myChatText.setText("");

        }
    }


    private void setUpDisplayName(){
        SharedPreferences pref = getSharedPreferences(RagisterActivity.CHAT_PREF,MODE_PRIVATE);
        myUserName = pref.getString(RagisterActivity.DISPLAY_NAME,null);

        if (myUserName == null){
            myUserName = "user";
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        myAdapter = new ChatListAdapter(this,myDatabaseRef,myUserName);
        myChatListView.setAdapter(myAdapter);

    }


    @Override
    protected void onStop() {
        super.onStop();
        myAdapter.freeUpResources();
    }
}












