package com.hungryhackers.bvpal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.hungryhackers.bvpal.sharedPreferencesConstants.BVPAL;
import static com.hungryhackers.bvpal.sharedPreferencesConstants.ROLLNUMBER;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSignUp;
    private EditText editTextEmail, editTextRollNumber, editTextPassword;
    private TextView textViewSignIn;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Fade());

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        buttonSignUp = (Button) findViewById(R.id.buttonSignup);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.signuppassword);
        editTextRollNumber = (EditText) findViewById(R.id.signuprollnumber);

        textViewSignIn = (TextView) findViewById(R.id.signinhere);

        buttonSignUp.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSignUp){
            registerUser();
        }

        if (v == textViewSignIn){
            finish();
        }
    }

    private void saveUserDetails(){
        String rollnumber = editTextRollNumber.getText().toString().trim();
        rollnumber = rollnumber.replaceFirst("^0+(?!$)", "");
        String email = editTextEmail.getText().toString().trim();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("database").child(rollnumber).child("email").setValue(email);

        String uid = user.getUid();
//        Map<String, String> map = new HashMap<>();
//        map.put("EventUID", "0");
        databaseReference.child("wishlist_database").child(uid).setValue(0);

        SharedPreferences sp = getSharedPreferences(BVPAL, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ROLLNUMBER,rollnumber);
        editor.commit();
    }

    private void registerUser(){
        String rollnumber = editTextRollNumber.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(rollnumber)){
            Toast.makeText(this, "Please enter Roll Number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        //if validate then continue

        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        //if () check if acc is already registered

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //successfully registered and logged in
                            //start profile activity
                            //fetch and save profile details
                            Toast.makeText(SignupActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            saveUserDetails();

                            startActivity(new Intent(SignupActivity.this, ProfileActivity.class));
                            finish();
                            View v = getLayoutInflater().inflate(R.layout.activity_profile, null);
                            RelativeLayout view = (RelativeLayout) v.findViewById(R.id.avatarLayout);
                            view.setVisibility(View.VISIBLE);

                        }else {
                            Toast.makeText(SignupActivity.this, "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}