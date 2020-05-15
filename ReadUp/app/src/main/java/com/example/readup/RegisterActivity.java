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
import android.widget.TextView;
import android.widget.Toast;

import com.example.readup.model.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnRegister;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etCPassword;
    private EditText etName;
    private TextView tvSignIn;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private Member member;
    private DatabaseReference databaseReference;

    private long maxId = 0;

    private Vector<String> vEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnRegister = findViewById(R.id.btnRegister);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etCPassword = findViewById(R.id.etCPassword);
        etName = findViewById(R.id.etName);
        tvSignIn = findViewById(R.id.tvSignIn);

        btnRegister.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Member");

        databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                        maxId = dataSnapshot.getChildrenCount();
                    vEmail = new Vector<>();
                    for(long i = 1 ; i <= maxId ; i++){
                        String email = dataSnapshot.child(String.valueOf(i)).child("email").getValue(String.class);
                        vEmail.add(email);
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void registerUser(){
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString();
        final String cpassword = etCPassword.getText().toString();
        final String name = etName.getText().toString().trim();
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

            for (String x : vEmail) {
                if (x.equals(email)) {
                    progressDialog.hide();
                    Toast.makeText(RegisterActivity.this, R.string.used_email, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            member = new Member(email,password,name,"");
            databaseReference.child(String.valueOf(maxId + 1)).setValue(member);
            Toast.makeText(RegisterActivity.this, R.string.success_register, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnRegister){
            registerUser();
        }
        if(v == tvSignIn){
            finish();
        }
    }
}
