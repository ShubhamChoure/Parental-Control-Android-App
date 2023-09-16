package com.example.jspm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import Login.ChildLogin;
import Login.ParentLogin;

public class MainActivity extends AppCompatActivity {

ImageView parentIV,childIV;
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
        parentIV = findViewById(R.id.parentlogin);
        childIV = findViewById(R.id.childlogin);
    }
}