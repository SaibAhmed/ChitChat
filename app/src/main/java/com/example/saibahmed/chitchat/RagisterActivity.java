package com.example.saibahmed.chitchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

    //methods called by tapping
    public void signUp(View v){
        registerUser();
    }

    //actual registration happens here
    private void registerUser(){
        myEmail.setError(null);
        myPassword.setError(null);
        myPasswordConfirm.setError(null);
        myUserNameView.setError(null);

        //grab values
        String email = myEmail.getText().toString();
        String password = myPassword.getText().toString();

        boolean cancel= false;
        View focusView = null;

        //password validation
        if (!TextUtils.isEmpty(password)  && !checkPassword(password)){
            myPassword.setError(getString(R.string.invalid_password));
            focusView = myPassword;
            cancel = true;
        }

        //email validation
        if (!TextUtils.isEmpty(email) && !checkEmail(email)){
            myEmail.setError(getString(R.string.invalid_email));
            focusView = myEmail;
            cancel=true;
        }

        if (cancel){
            focusView.requestFocus();
        }else{
            createUser();
        }
    }


    //Use shared prefs for username
    private void saveUserName() {
        String userName = myUserNameView.getText().toString();
        SharedPreferences pref = getSharedPreferences(CHAT_PREF, 0);
        pref.edit().putString(DISPLAY_NAME, userName).apply();
    }

    //validation for email
    private boolean checkEmail(String email){
        return email.contains("@");
    }

    //validation for password
    private boolean checkPassword(String password){
        String confPassword=myPasswordConfirm.getText().toString();
        return confPassword.equals(password) && password.length() > 4;
    }


    //signUp user at firebase
    private void createUser() {
        //Grab values
        String email = myEmail.getText().toString();
        String password = myPassword.getText().toString();

        //call method from firebase
        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i("FINDCODE", "user creation was" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    showErrorBox("oops ragistration failed");
                } else {
                    saveUserName();
                    Toast.makeText(RagisterActivity.this, "Ragistration successful", Toast.LENGTH_SHORT).show();

                    //move user to login screen on success
                    Intent intent = new Intent(RagisterActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });


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
