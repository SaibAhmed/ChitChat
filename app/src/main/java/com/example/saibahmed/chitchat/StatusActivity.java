package com.example.saibahmed.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextInputLayout mStatus;

    //Firebase Database Ref
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        String status_value = getIntent().getStringExtra("status_value");
        mStatus = (TextInputLayout) findViewById(R.id.status_Text);
        mStatus.getEditText().setText(status_value);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


        mToolbar = findViewById(R.id.status_appbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void saveStatus(View v) {

        //progress bar
        mRegProgress = new ProgressDialog(StatusActivity.this);
        mRegProgress.setTitle("Saving Changes");
        mRegProgress.setMessage("please wait while we save changes");
        mRegProgress.show();
        String status = mStatus.getEditText().getText().toString();

        mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    mRegProgress.dismiss();

                } else {

                    Toast.makeText(getApplicationContext(), "There are some error in saving changes", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
