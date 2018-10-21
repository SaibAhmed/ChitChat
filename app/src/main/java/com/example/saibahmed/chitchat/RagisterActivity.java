package com.example.saibahmed.chitchat;

import android.app.ProgressDialog;
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
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class RagisterActivity extends AppCompatActivity {

    public static final String CHAT_PREF = "ChatPref";
    public static final String DISPLAY_NAME = "UserName";

    //ref to fields
    private AutoCompleteTextView myUserNameView;
    private EditText myEmail;
    private EditText myPassword;
    private EditText myPasswordConfirm;
    private ProgressDialog mRegProgress;
    private Toolbar mToolbar;

    //firebase Ref
    private FirebaseAuth myAuth;
    public DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ragister);

        //setting appbar
        mToolbar = findViewById(R.id.Register_page_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        getSupportActionBar().setTitle("Make a new Account");

        //get values on create
        myUserNameView = findViewById(R.id.username_editText);
        myEmail = findViewById(R.id.email_editText);
        myPassword = findViewById(R.id.pass_editText);
        myPasswordConfirm = findViewById(R.id.confirmPass_editText);
        mRegProgress = new ProgressDialog(this);

        //Get a hold of firebase instance
        myAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //methods called by tapping
    public void signUp(View v) {
        String userName = myUserNameView.getText().toString();
        String email = myEmail.getText().toString();
        String password = myPassword.getText().toString();
        String confPassword = myPasswordConfirm.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confPassword) || TextUtils.isEmpty(userName)) {
            showErrorBox("Please Enter the credentials");
        } else {
            mRegProgress.setTitle("Registering user");
            mRegProgress.setMessage("Please wait while we creating your Account");
            mRegProgress.show();
            mRegProgress.setCanceledOnTouchOutside(false);
            registerUser(userName, email, password);
        }

    }

    //actual registration happens here
    private void registerUser(final String userName, String email, String password) {

        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.i("FINDCODE", "user creation was" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    mRegProgress.hide();
                    showErrorBox("oops registration failed");
                } else {

//                    saveUserName();
                    SharedPreferences preferences=getSharedPreferences(CHAT_PREF, 0);
                    preferences.edit().putString(DISPLAY_NAME,userName).apply();

                    FirebaseUser currentUser=myAuth.getCurrentUser();
                    String uid = currentUser.getUid();
                    mDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name",userName);
                    userMap.put("status","Hi there, I'am Using ChitChat App");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");
                    mDatabase.setValue(userMap);

                    mRegProgress.dismiss();
                    Toast.makeText(RagisterActivity.this, "registration successful", Toast.LENGTH_SHORT).show();


                    //move user to login screen on success
                    Intent intent = new Intent(RagisterActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

        myEmail.setError(null);
        myPassword.setError(null);
        myPasswordConfirm.setError(null);
        myUserNameView.setError(null);

        //grab values
        email = myEmail.getText().toString();
        password = myPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //password validation
        if (!TextUtils.isEmpty(password) && !checkPassword(password)) {
            myPassword.setError(getString(R.string.invalid_password));
            focusView = myPassword;
            cancel = true;
        }

        //email validation
        if (!TextUtils.isEmpty(email) && !checkEmail(email)) {
            myEmail.setError(getString(R.string.invalid_email));
            focusView = myEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }


    }

//    private void saveUserName(){
//        String userName=myUserNameView.getText().toString();
//        SharedPreferences preferences=getSharedPreferences(CHAT_PREF, 0);
//        preferences.edit().putString(DISPLAY_NAME,userName).apply();
//    }

    //validation for email
    private boolean checkEmail(String email) {
        return email.contains("@");
    }

    //validation for password
    private boolean checkPassword(String password) {
        String confPassword = myPasswordConfirm.getText().toString();
        return confPassword.equals(password) && password.length() > 4;
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
