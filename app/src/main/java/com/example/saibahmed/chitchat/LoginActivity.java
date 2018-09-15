package com.example.saibahmed.chitchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    //Ref to firebase
    private FirebaseAuth myAuth;

    //UI refs
    private EditText myEmail;
    private EditText myPassword;
    View focusView = null;


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

    //Sign in button was tapped
    public void signinUser(View v){
        loginUserWithFireBase();
    }

    //Login user with firebase
    private void loginUserWithFireBase(){
        String email = myEmail.getText().toString();
        String password = myPassword.getText().toString();

        //Todo implement a check like in register activity

        if (email.equals("") || password.equals("")){
            focusView = myPassword;
            focusView = myEmail;
            myPassword.setError("please enter your password");
            myEmail.setError("please enter your email");
            return;
        }
        Toast.makeText(this,"Loggin you in..",Toast.LENGTH_SHORT).show();

        myAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i("FINDCODE-L", "was user logged in" + task.isSuccessful());

                if (!task.isSuccessful()){
                    showErrorBox("there was a problem in loggin in");
                    Log.i("FINDCODE-L","MESSAGE : "+task.getException());
                }else{
                    Intent intent =  new Intent(LoginActivity.this,MainChatActivity.class);
                    finish();
                    startActivity(intent);
                }

            }
        });
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
