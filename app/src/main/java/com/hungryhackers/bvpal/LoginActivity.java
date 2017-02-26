package com.hungryhackers.bvpal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.stats.StatsEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static android.provider.Telephony.Mms.Part.FILENAME;
import static com.hungryhackers.bvpal.sharedPreferencesConstants.BVPAL;
import static com.hungryhackers.bvpal.sharedPreferencesConstants.FIRSTLOGIN;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSignIn;
    private EditText editTextRollNumber, editTextPassword;
    private TextView textViewSignUp;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            //already logged in, start profile
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        buttonSignIn = (Button) findViewById(R.id.buttonSignin);

        editTextRollNumber = (EditText) findViewById(R.id.loginrollnumber);
        editTextPassword = (EditText) findViewById(R.id.loginpassword);

        textViewSignUp = (TextView) findViewById(R.id.signuphere);

        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(BVPAL, MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    private void userLogin(){
        String rollnumber = editTextRollNumber.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();


        if (TextUtils.isEmpty(rollnumber)){
            Toast.makeText(this, "Please enter Roll Number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }



        //if validate then continue
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        rollnumber = rollnumber.replaceFirst("^0+(?!$)", "");

        if (rollnumber.split("0990").length == 2) {

            //event register login
            DatabaseReference mUserName = databaseReference.child("event_register_login").child(rollnumber);
            mUserName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String passwordCheck = "";
                    progressDialog.dismiss();
                    if (dataSnapshot.getValue()!=null) {
                        passwordCheck = dataSnapshot.getValue().toString();
                    }
                    if (password.equals(passwordCheck)){
                        finish();
                        startActivity(new Intent(LoginActivity.this, EventUploadEventActivity.class));
                    }else {
                        Toast.makeText(LoginActivity.this, "User Name or Password Incorrect !", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else {
            //if not event register login i.e. normal login
            DatabaseReference mEmailRef = databaseReference.child("database").child(rollnumber);


            //Use SharedPreferences
            SharedPreferences sp = getSharedPreferences("BVPAL", MODE_PRIVATE);
            final SharedPreferences.Editor editor = sp.edit();
            editor.putString("ROLLNUMBER", rollnumber);
            editor.commit();


            mEmailRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> map = (Map) dataSnapshot.getValue();

                    if (map == null) {
                        Toast.makeText(LoginActivity.this, "No Account found. Pls Register!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        return;
                    }
                    String email = map.get("email");
                    if (email == "") {
                        Toast.makeText(LoginActivity.this, "No Account found. Pls Register!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        return;
                    }
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //start profile
                                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Could not Sign In. Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void bypass(View view){
        //for testing purpose only
        finish();
        firebaseAuth.signInWithEmailAndPassword("manav.mj.jain@gmail.com","qwerty");
        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSignIn){
            userLogin();
        }

        if (v == textViewSignUp){
            startActivity(new Intent(this, SignupActivity.class));
        }
    }
}
