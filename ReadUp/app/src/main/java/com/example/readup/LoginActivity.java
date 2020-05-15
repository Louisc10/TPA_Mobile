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
import com.example.readup.model.Preferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvSignUp;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    int RC_SIGN_IN = 0;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    public static String Current_Member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String id = Preferences.getKeyUserId(getBaseContext());
        System.out.println("ID: " + id + ";");

        if(!id.equals("")){
            finish();
            setDeviceId();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Member");

        signInButton = findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            final String id = account.getId();

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long i = 1;
                    boolean found  = false;
                    for (; i <= dataSnapshot.getChildrenCount(); i++) {
                        String d = dataSnapshot.child(String.valueOf(i)).child("googleId").getValue().toString();
                        if(d.equalsIgnoreCase(id)){
                            progressDialog.hide();
                            Preferences.setKeyUserId(getBaseContext(),String.valueOf(i));
                            found = true;
                            Toast.makeText(LoginActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    if(!found){
                        String email = account.getEmail();
                        String name = account.getDisplayName();
                        long maxId = dataSnapshot.getChildrenCount();
                        progressDialog.hide();
                        Preferences.setKeyUserId(getBaseContext(),String.valueOf(maxId + 1));
                        Member member = new Member(email,"",name,id);
                        databaseReference.child(String.valueOf(maxId + 1)).setValue(member);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        } catch (ApiException e) {
        }
    }


    private void loginUser(){
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, R.string.empty_email, Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, R.string.empty_password, Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setMessage("Please wait");
            progressDialog.show();

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        System.out.println(dataSnapshot.getChildrenCount());
                        long i = 1;
                        boolean found  = false;
                        for (; i <= dataSnapshot.getChildrenCount(); i++) {
                            String e = dataSnapshot.child(String.valueOf(i)).child("email").getValue().toString();
                            String p = dataSnapshot.child(String.valueOf(i)).child("password").getValue().toString();

                            if(e.equalsIgnoreCase(email) && p.equals(password)){
                                LoginActivity.Current_Member = dataSnapshot.child(String.valueOf(i)).child("name").getValue().toString();
                                progressDialog.hide();
                                Preferences.setKeyUserId(getBaseContext(),String.valueOf(i));
                                found = true;
                                Current_Member = dataSnapshot.child(String.valueOf(i)).child("name").getValue().toString();
                                finish();
                                Toast.makeText(LoginActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                setDeviceId();
                            }
                        }
                        if(!found){
                            progressDialog.hide();
                            Toast.makeText(LoginActivity.this, R.string.fail_login, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnLogin){
            loginUser();
        }
        if(v == tvSignUp){
            startActivity(new Intent(this, RegisterActivity.class));
            etEmail.setText("");
            etPassword.setText("");
        }
    }

    DatabaseReference memberReff;
    public void setDeviceId(){
        memberReff = FirebaseDatabase.getInstance().getReference().child("Member");

        //String uid = firebaseAuth.getCurrentUser().getUid();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        memberReff.child(Preferences.getKeyUserId(getBaseContext())).child("device_token").setValue(deviceToken);
    }
}
