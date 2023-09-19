package com.example.jspm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import HomeActivity.HomeActivity;
import Login.ChildLogin;
import Login.ParentLogin;

public class MainActivity extends AppCompatActivity {

ImageView parentIV,childIV;
FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        parentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ParentLoginIntent = new Intent(MainActivity.this,Login.ParentLogin.class);
                startActivity(ParentLoginIntent);
            }
        });
        childIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ChildLoginIntent = new Intent(MainActivity.this,Login.ChildLogin.class);
                startActivity(ChildLoginIntent);
            }
        });
    }
    void init()
    {
        mAuth = FirebaseAuth.getInstance();
        parentIV = findViewById(R.id.parentlogin);
        childIV = findViewById(R.id.childlogin);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            if(currentUser.isEmailVerified()) {
                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        }
    }
}