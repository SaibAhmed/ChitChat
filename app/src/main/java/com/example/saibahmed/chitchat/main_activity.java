package com.example.saibahmed.chitchat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class main_activity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        mToolbar =findViewById(R.id.main_activity_toolbar);
        mViewPager =(ViewPager)findViewById(R.id.main_tabPager);
       //setting actionBar
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        getSupportActionBar().setTitle("ChitChat");


        mSectionsPagerAdapter =new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout)findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(main_activity.this,LoginActivity.class));
                break;

            case R.id.menu_chats:
                startActivity(new Intent(main_activity.this,MainChatActivity.class));
               //     finish();
                    break;
            case R.id.menu_settings:
                startActivity(new Intent(main_activity.this,SettingsActivity.class));
                  //  finish();
                    break;

            case R.id.menu_allUsers:
                startActivity(new Intent(main_activity.this,AllUsersActivity.class));
               // finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
