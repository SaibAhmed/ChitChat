package com.example.saibahmed.chitchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void ragisterNewUser(View v){
        Intent intent=new Intent(this,RagisterActivity.class);
        finish();
        startActivity(intent);
    }
}
