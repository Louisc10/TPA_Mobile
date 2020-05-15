package com.example.readup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.readup.adapter.CustomComicAdapter;
import com.example.readup.model.Comic;
import com.example.readup.model.ComicModel;
import com.example.readup.model.Genre;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Locale;

public class ComicActivity extends AppCompatActivity implements Dialog.DialogListener {

    Context c;
    ArrayList<Uri> IVarray;
    ImageView iv;
    Button b;
    Button back;
    Button upload;
    CustomComicAdapter cca;
    ListView lv;
    StorageReference sr;
    Activity activity;
    Uri ur;
    Long maxChild;

    DatabaseReference reff ;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        c = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic);
        sr = FirebaseStorage.getInstance().getReference("Images");
        reff = FirebaseDatabase.getInstance().getReference().child("Comic");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    maxChild = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        activity = this;
        IVarray = new ArrayList<android.net.Uri>();

        b  = findViewById(R.id.image_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions,PERMISSION_CODE);
                    }else{
                        pickImage();
                    }
                }else{
                    pickImage();
                }
            }
        });

        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cca = new CustomComicAdapter(activity,IVarray);
        lv = findViewById(R.id.comic_list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(c,""+position,Toast.LENGTH_LONG).show();
                openDialog();
                SelectedPage = position;
            }
        });
        lv.setAdapter(cca);

        upload = findViewById(R.id.upload_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    findViewById(R.id.upload_placeholder).setVisibility(View.VISIBLE);
                    Fileuploader();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        spinner = findViewById(R.id.cspinner);
        getGenre();
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
    Spinner spinner;
    private void Fileuploader() throws InterruptedException {
        findViewById(R.id.upload_placeholder).setVisibility(View.VISIBLE);
        EditText et1 = findViewById(R.id.title);

        ArrayList<Uri> uris = cca.getAllUri();
        if(et1.getText().toString().equals("")|| ((Genre)spinner.getSelectedItem()).getGenreName().equals("-")){
            Toast.makeText(this,"Cannot be empty",Toast.LENGTH_LONG).show();
            findViewById(R.id.upload_placeholder).setVisibility(View.GONE);
        }else{
            sr = FirebaseStorage.getInstance().getReference("Images/"+et1.getText().toString());
            int page = 0;
            for (Uri u: uris) {
                final StorageReference SR = sr.child("Page"+page+"."+getExtention(u));
                SR.putFile(u).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = SR.getDownloadUrl().getResult();
                        Toast.makeText(ComicActivity.this,"Success",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(ComicActivity.this,ur+" "+e,Toast.LENGTH_LONG).show();
                    }
                });
                page++;
            }
            int genreId=0;
            genreId=spinner.getSelectedItemPosition();
            reff.child((maxChild+1)+"").setValue(new ComicModel(et1.getText().toString(),genreId,LoginActivity.Current_Member));
            try {
                Thread.sleep(2000);
            }catch (Exception e){

            }
            finish();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImage();
                }else{
                    Toast.makeText(this,"Must allow first",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EditText et = findViewById(R.id.title);
        if (resultCode == RESULT_OK && requestCode == 1 && data!=null && data.getData()!=null ) {
            ur = data.getData();
            if(getExtention(ur).equals("jpg")) cca.add(ur);
            else Toast.makeText(c,"Extention must be jpg", Toast.LENGTH_LONG).show();
        }
    }

    ArrayList<Genre> genres;
    public void getGenre(){
        String lang = Locale.getDefault().getLanguage();
        DatabaseReference genreReference = FirebaseDatabase.getInstance().getReference().child("Genre").child(lang);

        genreReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long i = 1;
                genres = new ArrayList<>();
                genres.add(new Genre("-"));
                for (; i <= dataSnapshot.getChildrenCount(); i++) {
                    String n = dataSnapshot.child(String.valueOf(i)).child("genreName").getValue().toString();
                    genres.add(new Genre(n));
                }

                ArrayAdapter<Genre> adapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, genres);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void openDialog(){
        Dialog d = new Dialog();
        d.show(getSupportFragmentManager(),"");
    }

    Integer SelectedPage;
    @Override
    public String getData(String data) {
        if(data.equals("Update")){
            cca.Delete(SelectedPage);
            pickImage();
        }
        if(data.equals("Delete")){
            cca.Delete(SelectedPage);
        }
        return data;
    }
}
