package com.example.saibahmed.chitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;

public class RagisterActivity extends AppCompatActivity {

    public static final String CHAT_PREF = "ChatPref";
    public static final String DISPLAY_NAME = "UserName";

    //ref to fields
    private AutoCompleteTextView myUserNameView;
    private EditText myEmail;
    private EditText myPassword;
    private EditText myPasswordConfirm;

    //firebase Ref
    private FirebaseAuth myAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ragister);

        //get values on create
        myUserNameView = findViewById(R.id.username_editText);
        myEmail = findViewById(R.id.email_editText);
        myPassword = findViewById(R.id.pass_editText);
        myPasswordConfirm = findViewById(R.id.confirmPass_editText);

        //Get a hold of firebase instance
        myAuth = FirebaseAuth.getInstance();
    }


}
