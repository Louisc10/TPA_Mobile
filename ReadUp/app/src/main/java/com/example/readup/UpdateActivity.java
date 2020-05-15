package com.example.readup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.readup.model.Member;
import com.example.readup.model.Preferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

public class UpdateActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private Button btnUpdate;

    private EditText etEmail;
    private EditText etName;
    private EditText etPassword;
    private EditText etCPassword;

    private Member member;

    private DatabaseReference databaseReference;

    private long maxId;

    private Vector<String> vMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        vMember = new Vector<>();

        progressDialog = new ProgressDialog(this);

        btnUpdate = findViewById(R.id.btnUpdate);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etCPassword = findViewById(R.id.etCPassword);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Member");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxId = dataSnapshot.getChildrenCount();
                vMember = new Vector<>();
                for(long i = 1 ; i <= maxId ; i++){
                    String member = dataSnapshot.child(String.valueOf(i)).child("email").getValue(String.class);
                    vMember.add(member);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updating();
            }
        });
    }

    private void updating(){
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString();
        String cpassword = etCPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, R.string.empty_email, Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.indexOf(email, "@") == -1){
            Toast.makeText(this, R.string.error_email, Toast.LENGTH_SHORT).show();
        }
        else if (!email.endsWith(".com")){
            Toast.makeText(this, R.string.error_email, Toast.LENGTH_SHORT).show();
        }
        else if (email.contains("@.") || email.contains((".@"))){
            Toast.makeText(this, R.string.error_email, Toast.LENGTH_SHORT).show();
        }
        else if (email.startsWith(".") || email.startsWith("@") ){
            Toast.makeText(this, R.string.error_email, Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, R.string.empty_password, Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 5 || 20 < password.length()){
            Toast.makeText(this, R.string.error_password, Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cpassword)){
            Toast.makeText(this, R.string.empty_cpassword, Toast.LENGTH_SHORT).show();
        }
        else if (!cpassword.equals(password)){
            Toast.makeText(this, R.string.error_cpassword, Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(name)){
            Toast.makeText(this, R.string.empty_name, Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setMessage("Please wait");
            progressDialog.show();

            for (String x : vMember) {
                if (x.equals(email)) {
                    progressDialog.hide();
                    Toast.makeText(UpdateActivity.this, R.string.used_email, Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                    return;
                }
            }
            progressDialog.hide();
            String id = Preferences.getKeyUserId(getBaseContext());

            member = new Member(email,password,name,"");


            databaseReference.child(id).setValue(member);
            Toast.makeText(UpdateActivity.this, R.string.success_update, Toast.LENGTH_SHORT).show();
            finish();

        }
    }
}
