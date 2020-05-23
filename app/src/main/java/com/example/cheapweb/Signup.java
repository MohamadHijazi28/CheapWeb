package com.example.cheapweb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.Authenticator;
import java.util.concurrent.TimeUnit;

public class Signup extends AppCompatActivity  {
    EditText fullName, email, phoneNo, password, CodeV;
    Button signup, verify;
    int num=0, w;
    ProgressBar progressBar;
    CheckBox remember2;
    private FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRefUsers = database.getReference("users");
    Users users;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        //get the permission to send sms to the user phone
        if (ContextCompat.checkSelfPermission(Signup.this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

            }
            Toast.makeText(Signup.this, "You have already granted this permission", Toast.LENGTH_SHORT).show();
        }
        else {
            ActivityCompat.requestPermissions(Signup.this, new String[] {Manifest.permission.SEND_SMS} , PERMISSION_REQUEST_CODE);
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        fullName = (EditText) findViewById(R.id.editText3);
        email = (EditText) findViewById(R.id.editText4);
        phoneNo = (EditText) findViewById(R.id.editText5);
        password = (EditText) findViewById(R.id.editText6);
        signup = (Button) findViewById(R.id.button3);
        remember2 = findViewById(R.id.checkBox2);
        CodeV = (EditText) findViewById(R.id.editText7);
        verify = (Button) findViewById(R.id.button4);
        //when the user click on the first time, who don't have to sign in again
        remember2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                } else if (!buttonView.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                }
            }
        });

        //when the user click on this button the function will check if the email that he write is already registered
        //it will be make a Users object and set the info of the user in it..
        //it send to the user phone a code to check and verify that the number is for him
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = myRefUsers.orderByChild("email").equalTo(email.getText().toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        num = (int) dataSnapshot.getChildrenCount();
                        if (num == 0) {
                            users = new Users(fullName.getText().toString(), email.getText().toString(), phoneNo.getText().toString(), password.getText().toString());
                            String maill = email.getText().toString();
                            String pas = password.getText().toString();
                            if (TextUtils.isEmpty(maill)) {
                                email.setError("Email is Required");
                                return;
                            }
                            if (TextUtils.isEmpty(pas)) {
                                password.setError("Password is Required");
                                return;
                            }
                            if (pas.length() < 6) {
                                password.setError("Password must be more than 6 Characters");
                                return;
                            }

                            if (!isEmailValid(users.getEmail())){
                                Toast.makeText(Signup.this, "Please select a correct email", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                CodeV.setVisibility(View.VISIBLE);
                                verify.setVisibility(View.VISIBLE);
                                w = (int) (Math.random() * 1000000 + 100000);
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(phoneNo.getText().toString(), null, "Your Code is :" + w, null, null);
                            }

                        } else {
                            Toast.makeText(Signup.this, "the email is already registered", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        //to check if the user phone number is correct
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(CodeV.getText().toString()) == w) {
                    myRefUsers.push().setValue(users);
                    mAuth.createUserWithEmailAndPassword(users.getEmail(), users.getPassword());
                    progressBar.setVisibility(View.VISIBLE);
                                Intent m = new Intent(getApplicationContext(), MainActivity.class);
                                m.putExtra("userEmail", email.getText().toString());
                                startActivity(m);
                            } else {
                                Toast.makeText(Signup.this, "Please select a correct code..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }


    //check the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Signup.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Signup.this, "Permission Denied, to enjoy using the app Please confirm the Permission", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(Signup.this, new String[] {Manifest.permission.SEND_SMS} , PERMISSION_REQUEST_CODE);
                }
            }
        }
    }

    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
