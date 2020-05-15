package com.example.readup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readup.model.ComicModel;
import com.example.readup.model.Genre;
import com.example.readup.model.NovelModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class NovelActivity extends AppCompatActivity {
    Context c;
    public static Boolean upating;

    ImageButton bold,italic,underscore;
    Boolean toggleB,toggleI,toggleU;

    Button displayMode,editMode;
    Boolean displaying,editing;

    Button upload,tumbnail;

    TextView display;
    EditText input;

    DatabaseReference reff ;
    StorageReference sr;
    Long maxChild;

    Uri ur;
    EditText et1;

    String initial;
    Spinner spinner;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    TextView lp;

    Integer id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel);
        c = this;

        et1 = findViewById(R.id.ntitle);
        input = findViewById(R.id.input);
        spinner = findViewById(R.id.nspinner);
        getGenre();
        et1.setText("");
        spinner.setSelection(0);

        if(upating){
            input.setText(getIntent().getStringExtra("Novel_Content"));
            et1.setText(getIntent().getStringExtra("Novel_Title"));
            initial = getIntent().getStringExtra("Novel_Content");
            id = getIntent().getIntExtra("Novel_Id",0);
        }

        toggleB = toggleI = toggleU = true;

        display = findViewById(R.id.display);


        bold = findViewById(R.id.bbold);
        italic = findViewById(R.id.bitalic);
        underscore = findViewById(R.id.bunderline);


        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = input.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    display.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    display.setText(Html.fromHtml(content));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = input.getText().toString();
                if(toggleB) content+="<b>";
                else content+="</b>";
                toggleB =!toggleB;
                input.setText(content);
                input.setSelection(input.length());
            }
        });
        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = input.getText().toString();
                if(toggleI) content+="<i>";
                else content+="</i>";
                toggleI =!toggleI;
                input.setText(content);
                input.setSelection(input.length());
            }
        });
        underscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = input.getText().toString();
                if(toggleU) content+="<u>";
                else content+="</u>";
                toggleU =!toggleU;
                input.setText(content);
                input.setSelection(input.length());
            }
        });

        editing = true;
        displaying = false;

        displayMode = findViewById(R.id.bdisplay);
        editMode = findViewById(R.id.bedit);

        displayMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!displaying){
                    displaying = true;
                    display.setVisibility(View.VISIBLE);
                    input.setVisibility(View.GONE);
                    editing = false;
                }
            }
        });
        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editing){
                    editing = true;
                    display.setVisibility(View.GONE);
                    input.setVisibility(View.VISIBLE);
                    displaying = false;
                }
            }
        });

        upload = findViewById(R.id.bupload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lp = findViewById(R.id.loading_novel);
                lp.setVisibility(View.VISIBLE);
                Fileuploader();
            }
        });

        reff = FirebaseDatabase.getInstance().getReference().child("Novel");
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

        tumbnail = findViewById(R.id.btumbnail);
        tumbnail.setOnClickListener(new View.OnClickListener() {
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


    }

    private void Fileuploader(){
        Genre genre = (Genre) spinner.getSelectedItem();
        if(et1.getText().toString().equals("") || ((Genre)spinner.getSelectedItem()).getGenreName().equals("-") || (initial != null && initial.equals(input.getText().toString()))){
            Toast.makeText(NovelActivity.this,"Cannot be empty or the same",Toast.LENGTH_LONG).show();

        }else{
            int genreId=0;
            genreId=spinner.getSelectedItemPosition();
            String content = input.getText().toString();
            if(!upating) reff.child((maxChild+1)+"").setValue(new NovelModel(et1.getText().toString(),genreId,LoginActivity.Current_Member,content));
            else reff.child(getIntent().getIntExtra("Novel_Id",1)+"").setValue(new NovelModel(et1.getText().toString(),genreId,LoginActivity.Current_Member,content));
            try {
                Thread.sleep(2000);
            }catch (Exception e){

            }
            sr = FirebaseStorage.getInstance().getReference("Images/"+et1.getText().toString());
            StorageReference SR = sr.child("Tumbnail."+getExtention(ur));
            if(ur == null) return;
            SR.putFile(ur).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    //Uri downloadUrl = SR.getDownloadUrl().getResult();
                    if(upating) updateNotify();
                    Toast.makeText(NovelActivity.this,"Success",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle unsuccessful uploads
                    // ...
                    Toast.makeText(NovelActivity.this,ur+" "+e,Toast.LENGTH_LONG).show();
                }
            });

            finish();

        }
        lp.setVisibility(View.GONE);
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
                if(upating){
                    int index = 0;
                    String g = "";
                    do{
                        spinner.setSelection(index);
                        index++;

                        if(getIntent().getStringExtra("Novel_Genre").equals("1")) g = "Comedy";
                        else g = "Horror";
                    }while(!((Genre)spinner.getSelectedItem()).getGenreName().equals(g));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    DatabaseReference notif;
    public void updateNotify(){
        final HashMap<String,String> hm = new HashMap<>();
        hm.put("x","x");
        DatabaseReference refft = FirebaseDatabase.getInstance().getReference().child("Subcription").child("Novel");
        refft.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if(!ds.hasChild(id+"")){
                  return;
                }
                notif = FirebaseDatabase.getInstance().getReference().child("Notif");
                for(long i=1; i <= ds.child(id+"").getChildrenCount(); i++){
                    notif.child(ds.child(id+"").child(i+"").child("name").getValue().toString()).setValue(hm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}









