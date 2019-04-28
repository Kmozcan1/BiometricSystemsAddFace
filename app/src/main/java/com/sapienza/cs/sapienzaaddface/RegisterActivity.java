package com.sapienza.cs.sapienzaaddface;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sapienza.cs.sapienzaaddface.Helpers.CreatePersonGroupHelper;
import com.sapienza.cs.sapienzaaddface.Helpers.FirebaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    EditText email,password;
    Button registerButton,loginButton;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressBar = findViewById(R.id.progressBar);
        email = findViewById(R.id.mailText);
        password = findViewById(R.id.PasswordText);
        registerButton = findViewById(R.id.RegisterButton);
        loginButton =  findViewById(R.id.loginPageButton);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                if(TextUtils.isEmpty(emailText)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passwordText)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                }

                if(passwordText.length()<6){
                    Toast.makeText(getApplicationContext(),"Password must be at least 6 characters",Toast.LENGTH_SHORT).show();
                }

                firebaseAuth.createUserWithEmailAndPassword(emailText,passwordText)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                    String groupId = ft.format(new Date());
                                    FirebaseHelper.createUser(task.getResult().getUser().getUid(), groupId);
                                    new CreatePersonGroup(RegisterActivity.this).execute(groupId, task.getResult().getUser().getDisplayName());
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"E-mail or password is wrong",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),ViewPersonGroupActivity.class));
        }

    }
    private class CreatePersonGroup extends CreatePersonGroupHelper {
        public CreatePersonGroup(Context context){
            super(context);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }
}
