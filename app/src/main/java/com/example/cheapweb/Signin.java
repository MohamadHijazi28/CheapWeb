package com.example.cheapweb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.net.ssl.SSLSessionContext;

public class Signin extends AppCompatActivity {

  EditText email, password;
  Button signin, signup, forgetpass;
  CheckBox remember;
  private FirebaseAuth mAuth;
  ProgressBar progressBar;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signin);
    // Initialize Firebase Auth

    SharedPreferences preferences=getSharedPreferences("checkBox", MODE_PRIVATE);
    String checkBox=preferences.getString("remember", "");
    if (checkBox.equals("true")){
      Intent m=new Intent(getApplicationContext(),MainActivity.class);
      startActivity(m);
    }
    else if (checkBox.equals("false")){
      Toast.makeText(Signin.this, "Please Sign In", Toast.LENGTH_SHORT).show();
    }
    mAuth = FirebaseAuth.getInstance();
    progressBar=(ProgressBar) findViewById(R.id.progressBar);
    email=(EditText) findViewById(R.id.editText);
    password=(EditText) findViewById(R.id.editText2);
    signin=(Button) findViewById(R.id.button);
    signup=(Button) findViewById(R.id.button2);
    forgetpass=(Button) findViewById(R.id.button9);
    remember= findViewById(R.id.checkBox);
    //when the user click this button this function will check
    // if this email and the password is sign up before..
    signin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String maill = email.getText().toString();
        String pas = password.getText().toString();
        if (TextUtils.isEmpty(maill)){
          email.setError("Email is Required");
          return;
        }
        if(TextUtils.isEmpty(pas)) {
          password.setError("Password is Required");
          return;
        }
        if(pas.length()<6){
          password.setError("Password must be more than 6 Characters");
          return;
        }
        mAuth.signInWithEmailAndPassword(maill,pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){
              progressBar.setVisibility(View.VISIBLE);
              Toast.makeText(Signin.this,"Logged in Successfully" ,Toast.LENGTH_SHORT).show();
              Intent m=new Intent(getApplicationContext(),MainActivity.class);
              m.putExtra("userEmail", email.getText().toString());
              startActivity(m);

            }
            else{
              Toast.makeText(Signin.this,"Error"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
            }
          }
        });
      }
    });
    //the first using of the app the user have to sign up
    //when the user click on this button the app moved to sign up class
    signup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent s= new Intent(getApplicationContext(),Signup.class);
        startActivity(s);
      }
    });
    //if the user forget his password..
    //he have to write their email and click on this button to reset the password..
    forgetpass.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String mail = email.getText().toString().trim();

        if (TextUtils.isEmpty(mail)) {
          Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
          return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(mail)

                .addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                      Toast.makeText(Signin.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                    } else {
                      Toast.makeText(Signin.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.GONE);
                  }
                });
      }
    });

    //when the user click on the first time, who don't have to sign in again
    remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.isChecked()){
          SharedPreferences preferences=getSharedPreferences("checkBox", MODE_PRIVATE);
          SharedPreferences.Editor editor=preferences.edit();
          editor.putString("remember", "true");
          editor.apply();
        }
        else if (!buttonView.isChecked()){
          SharedPreferences preferences=getSharedPreferences("checkBox", MODE_PRIVATE);
          SharedPreferences.Editor editor=preferences.edit();
          editor.putString("remember", "false");
          editor.apply();
        }
      }
    });
  }

}




