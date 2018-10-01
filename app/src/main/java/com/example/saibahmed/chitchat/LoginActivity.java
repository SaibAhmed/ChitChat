package com.example.saibahmed.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //Ref to firebase
    private FirebaseAuth myAuth;
    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;

    //UI refs
    private TextInputEditText myEmail;
    private TextInputEditText myPassword;
    View focusView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setting appbar
        mToolbar =findViewById(R.id.login_page_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        getSupportActionBar().setTitle("Login");

        //Grab data
        myEmail=(TextInputEditText)findViewById(R.id.email_login);
        myPassword=(TextInputEditText)findViewById(R.id.pass_login);

        //get firebase instance
        myAuth=FirebaseAuth.getInstance();

        mRegProgress = new ProgressDialog(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = myAuth.getCurrentUser();
        if (currentUser !=null ){
            sendToMainActivity();
        }

    }
    private void  sendToMainActivity(){
        Intent intent = new Intent(LoginActivity.this,main_activity.class);
        startActivity(intent);
        finish();
    }




    //Sign in button was tapped
    public void signinUser(View v){
        String email = myEmail.getText().toString();
        String password = myPassword.getText().toString();
        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            focusView = myPassword;
            focusView = myEmail;
            myPassword.setError("please enter your password");
            myEmail.setError("please enter your email");
            return;
        }
        else {
            mRegProgress.setTitle("logging you in..");
            mRegProgress.setMessage("please wait while we check your credentials");
            mRegProgress.setCanceledOnTouchOutside(false);
            mRegProgress.show();
            loginUserWithFireBase(email,password);
        }

    }

    //Login user with firebase
    private void loginUserWithFireBase(String email,String password){
        //Todo implement a check like in register activity

        if (email.equals("") || password.equals("")){
            focusView = myPassword;
            focusView = myEmail;
            myPassword.setError("please enter your password");
            myEmail.setError("please enter your email");
            return;
        }

        myAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i("FINDCODE-L", "was user logged in" + task.isSuccessful());

                if (!task.isSuccessful()){
                    mRegProgress.hide();
                    showErrorBox("there was a problem in loggin in!! please check your credentials");
                    Log.i("FINDCODE-L","MESSAGE : "+task.getException());

                }else{

                    mRegProgress.dismiss();
                    Intent intent =  new Intent(LoginActivity.this,main_activity.class);
                    startActivity(intent);
                    finish();

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
