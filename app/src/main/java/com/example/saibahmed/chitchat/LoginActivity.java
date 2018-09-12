package com.example.saibahmed.chitchat;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    //Ref to firebase
    private FirebaseAuth myAuth;

    //UI refs
    private EditText myEmail;
    private EditText myPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Grab data
        myEmail=findViewById(R.id.email_login);
        myPassword=findViewById(R.id.pass_login);

        //get firebase instance
        myAuth=FirebaseAuth.getInstance();
    }

    //move user to ragister activity
    public void ragisterNewUser(View v){
        Intent intent=new Intent(this,RagisterActivity.class);
        finish();
        startActivity(intent);
    }


    //create error box for errors
    private void showErrorBox(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Heyyyy")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
