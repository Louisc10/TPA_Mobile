package com.example.readup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.readup.model.Preferences;
import com.example.readup.model.Subs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.util.GAuthToken;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class ComicDetailActivity extends AppCompatActivity {
    StorageReference sr;
    NotificationCompat.Builder ncb;

    Integer Id;
    String Title,Author;

    Context c;

    ArrayList<Uri> uris;

    ImageView iv;
    Integer pageIndex;

    Button next,prev,update,insert;
    Uri ur;

    Boolean updating = false;
    Integer maxPage;

    ToggleButton tb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);
        c = this;
        createNotificationChannel();


        Id = getIntent().getIntExtra("Comic_Id",1);
        Title = getIntent().getStringExtra("Comic_Name");
        Author = getIntent().getStringExtra("Comic_Author");
        iv = findViewById(R.id.comic_content);
        pageIndex = 0;

        uris = new ArrayList<>();
        setImage();

        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageIndex++;
                setImage();
            }
        });

        prev = findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageIndex--;
                setImage();
            }
        });


        update = findViewById(R.id.comic_update);
        insert = findViewById(R.id.comic_insert);
        if(Author.equals(LoginActivity.Current_Member)){
            update .setVisibility(View.VISIBLE);
            insert.setVisibility(View.VISIBLE);
        }

        ncb = new NotificationCompat.Builder(c,"readup")
                .setSmallIcon(R.drawable.ic_comic)
                .setContentText("Hey "+Title+" Got updated")
                .setContentTitle("ReadUp")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updating = true;
                pickImage();
                notificationManager.notify(100,ncb.build());
            }
        });
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updating = false;
                pickImage();
            }
        });

        tb = findViewById(R.id.ctb);
        tb.setTextOff("Subscribe");
        tb.setTextOn("UnSubscribe");
        tb.setChecked(false);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,final boolean isChecked) {

                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Subcription").child("Comic");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            if(isChecked){
                                final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Subcription").child("Comic").child(getIntent().getIntExtra("Comic_Id",1)+"");
                                ref2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        maxChildd = dataSnapshot.getChildrenCount();

                                        if(barbar){
                                            ref.child(getIntent().getIntExtra("Comic_Id",1)+"").child((maxChildd+1)+"").setValue(new Subs(Preferences.getKeyUserId(getBaseContext())));
                                            barbar = false;
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }else{
                                //ref.child(getIntent().getIntExtra("Novel_Id",1)+"").child().removeValue();
                                barbar = true;

                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        getMaxChild2(0);
    }
    Long maxChildd;
    Boolean barbar = true;

    private void setImage(){
        StorageReference reff = FirebaseStorage.getInstance().getReference("Images").child(Title);
        reff.child("Page"+pageIndex+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(c)
                        .load(uri)
                        .into(iv);
                TextView pages = findViewById(R.id.pages);
                if(temp2 == null) temp2 = 2;
                pages.setText(pageIndex+"/"+(temp2-2));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(c,"thats the end",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void pickImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,1);

    }

    private String getExtention(Uri uri){
        if(uri == null) return null;
        ContentResolver cr = getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EditText et = findViewById(R.id.title);
        if (resultCode == RESULT_OK && requestCode == 1 && data!=null && data.getData()!=null ) {
            if(getExtention(data.getData()).equals("jpg")){
                ur = data.getData();
                if(updating)Fileupload();
                else insertPage();
                updateNotify();
            }
            else Toast.makeText(c,"Extention must be jpg", Toast.LENGTH_LONG).show();
        }
    }

    Integer maxChild,temp;
    Boolean loop;
    public void insertPage(){
        temp = getMaxChild(0);


    }

    public void Fileupload(){
        sr = FirebaseStorage.getInstance().getReference("Images/"+Title);

        final StorageReference SR = sr.child("Page"+pageIndex+"."+getExtention(ur));
        SR.putFile(ur).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ComicDetailActivity.this,"Success",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ComicDetailActivity.this,ur+" "+e,Toast.LENGTH_LONG).show();
            }
        });

    }
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ReadUp";
            String description = "Comeback theres a new update";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("readup", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    DatabaseReference notif;
    public void updateNotify(){
        final HashMap<String,String> hm = new HashMap<>();
        hm.put("x","x");

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Subcription").child("Comic");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if(!ds.hasChild(Id+"")) return;
                notif = FirebaseDatabase.getInstance().getReference().child("Notif");
                for(long i=1; i <= ds.child(Id+"").getChildrenCount(); i++){
                    notif.child(ds.child(Id+"").child(i+"").child("name").getValue().toString()).setValue(hm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public Integer getMaxChild(final int page){
        StorageReference reff = FirebaseStorage.getInstance().getReference("Images").child(Title);
        reff.child("Page"+page+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                temp = getMaxChild(page+1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                sr = FirebaseStorage.getInstance().getReference("Images/"+Title);
                final StorageReference SR = sr.child("Page"+page+"."+getExtention(ur));
                SR.putFile(ur).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ComicDetailActivity.this,"Success",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ComicDetailActivity.this,ur+" "+e,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        int max = page+1;
        return max;
    }
    Integer temp2;
    public Integer getMaxChild2(final int page){
        StorageReference reff = FirebaseStorage.getInstance().getReference("Images").child(Title);
        reff.child("Page"+page+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                temp2 = getMaxChild2(page+1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                TextView pages = findViewById(R.id.pages);
                pages.setText(pageIndex+"/"+(temp2-2));
            }
        });
        int max = page+1;
        return max;
    }
}
