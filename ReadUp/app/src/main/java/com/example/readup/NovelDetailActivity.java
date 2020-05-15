package com.example.readup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.readup.model.NovelModel;
import com.example.readup.model.Preferences;
import com.example.readup.model.Subs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;

import java.util.HashMap;

public class NovelDetailActivity extends AppCompatActivity {
    Context c;
    Integer novel_id;
    NotificationCompat.Builder ncb;

    DatabaseReference reff;

    TextView content,title;
    String Author,Genre,novel_content,novel_title;
    Button update;

    ToggleButton tb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_detail);
        c= this;
        createNotificationChannel();

        novel_id = getIntent().getIntExtra("Novel_Id",1);

        content = findViewById(R.id.novel_content);
        title = findViewById(R.id.novel_title);

        reff = FirebaseDatabase.getInstance().getReference().child("Novel").child(novel_id+"");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                novel_title = dataSnapshot.child("novelName").getValue().toString();
                novel_content = dataSnapshot.child("novelContent").getValue().toString();
                Author = dataSnapshot.child("novelAuthor").getValue().toString();
                Genre = dataSnapshot.child("novelGenre").getValue().toString();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    title.setText(Html.fromHtml("<h1>"+novel_title+"</h1>",Html.FROM_HTML_MODE_COMPACT));
                } else {
                    title.setText(Html.fromHtml("<h1>"+novel_title+"</h1>"));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    content.setText(Html.fromHtml(novel_content, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    content.setText(Html.fromHtml(novel_content));
                }
                if(Author.equals(LoginActivity.Current_Member)){
                    update.setVisibility(View.VISIBLE);
                }
                ncb = new NotificationCompat.Builder(c,"readup")
                        .setSmallIcon(R.drawable.ic_comic)
                        .setContentText("Hey "+novel_title+" Got updated")
                        .setContentTitle("ReadUp")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        update = findViewById(R.id.novel_update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NovelActivity.upating = true;
                Intent i = new Intent(c, NovelActivity.class);
                i.putExtra("Novel_Title",title.getText().toString());
                i.putExtra("Novel_Content",novel_content);
                i.putExtra("Novel_Genre",Genre);
                i.putExtra("Novel_Id",novel_id);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(i);
                notificationManager.notify(100,ncb.build());
            }
        });

        tb = findViewById(R.id.ntb);
        tb.setTextOff("Subscribe");
        tb.setTextOn("UnSubscribe");
        tb.setChecked(false);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,final boolean isChecked) {
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Subcription").child("Novel");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            if(isChecked){
                                final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Subcription").child("Novel").child(getIntent().getIntExtra("Novel_Id",1)+"");
                                ref2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        maxChild = dataSnapshot.getChildrenCount();

                                        if(barbar){
                                            ref.child(getIntent().getIntExtra("Novel_Id",1)+"").child((maxChild+1)+"").setValue(new Subs(Preferences.getKeyUserId(getBaseContext())));
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
        checkSub();
    }
    Long maxChild;
    Boolean barbar = true;

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

    private void checkSub(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Subcription").child("Novel").child(getIntent().getIntExtra("Novel_Id",1)+"");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index = 0;
                for(int i=1; i <= dataSnapshot.getChildrenCount(); i++){
                    if(dataSnapshot.hasChild(index+"")){
                        if(dataSnapshot.child(index+"").child("name").getValue().toString().equals(LoginActivity.Current_Member)){
                            tb.setChecked(true);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Integer getIndex(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Subcription").child("Novel").child(getIntent().getIntExtra("Novel_Id",1)+"");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index = 0;
                for(int i=0; i < dataSnapshot.getChildrenCount(); i++){
                    if(dataSnapshot.hasChild(index+"")){
                        if(dataSnapshot.child(index+"").child("name").getValue().toString().equals(LoginActivity.Current_Member)){
                            tb.setChecked(true);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return 0;
    }


}
