package com.example.saibahmed.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    //Instance for layout
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    public static final int GALLERY_PICK=1;

    // Firebase Storage Ref
    private StorageReference mImageStorage;

    private ProgressDialog mRegProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage=(CircleImageView)findViewById(R.id.setting_img);
        mName=(TextView)findViewById(R.id.displayName);
        mStatus=(TextView)findViewById(R.id.displayText);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid=mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                final String image=dataSnapshot.child("image").getValue().toString();
                String status =dataSnapshot.child("status").getValue().toString();
                String Thumb_image =dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

                if (!image.equals("default")){
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.user).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.user).into(mDisplayImage);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void ChangeStatus(View v){

        String status_value=mStatus.getText().toString();
        Intent intent = new Intent(this,StatusActivity.class);
        intent.putExtra("status_value",status_value);
        startActivity(intent);
       // finish();
    }

    public void ChangeImage(View v){
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_PICK && resultCode==RESULT_OK){
            Uri imageUri= data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mRegProgress = new ProgressDialog(SettingsActivity.this);
                mRegProgress.setTitle("Uploading Image..");
                mRegProgress.setMessage("Please wait while we upload and process the image.");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();

                Uri resultUri = result.getUri();
                String current_user_id=mCurrentUser.getUid();

                File thumb_filepath = new File(resultUri.getPath());

                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filepath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();
                final StorageReference thumb_filePath = mImageStorage.child("Profile_Images").child("thumbs").child(current_user_id +".jpg");



                StorageReference filePath = mImageStorage.child("Profile_Images").child(current_user_id +".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            final String download_url= task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()){

                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("image",download_url);
                                        update_hashMap.put("thumb_image",thumb_downloadUrl);

                                        mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    mRegProgress.dismiss();
                                                    Toast.makeText(SettingsActivity.this,"Success uploading",Toast.LENGTH_SHORT).show();


                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(SettingsActivity.this,"Error in uploading thumbnail",Toast.LENGTH_SHORT).show();
                                        mRegProgress.dismiss();
                                    }

                                }
                            });

                            mUserDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        mRegProgress.dismiss();
                                        //Toast.makeText(SettingsActivity.this,"Success uploading",Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });


                        } else{

                            Toast.makeText(SettingsActivity.this,"Error in uploading",Toast.LENGTH_SHORT).show();
                            mRegProgress.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,main_activity.class);
        startActivity(intent);
    }
}
