package com.example.jspm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import HomeActivity.ChildeHomeActivity.ChildHomeActivity;
import HomeActivity.ParentHomeActivity.HomeActivity;
import Login.Childe.ChildLogin;

public class MainActivity extends AppCompatActivity {

ImageView parentIV,childIV;
FirebaseAuth mAuth;
FirebaseFirestore db;

    ArrayList<String> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        parentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ParentLoginIntent = new Intent(MainActivity.this,Login.ParentLogin.class);
                ParentLoginIntent.putExtra("user","parent");
                startActivity(ParentLoginIntent);
            }
        });
        childIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ChildLoginIntent = new Intent(MainActivity.this, ChildLogin.class);
                ChildLoginIntent.putExtra("user","childe");
                startActivity(ChildLoginIntent);
            }
        });
    }
    void init()
    {
        mAuth = FirebaseAuth.getInstance();
        parentIV = findViewById(R.id.parentlogin);
        childIV = findViewById(R.id.childlogin);
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            if(currentUser.isEmailVerified()) {
                db.collection("User").whereEqualTo("Email", mAuth.getCurrentUser().getEmail().trim()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String user = document.getString("User");
                                userList.add(user);
                            }
                            if (!userList.isEmpty()) {
                                if (userList.get(0).trim().equals("parent")) {
                                    Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(homeIntent);
                                    finish();
                                } else if (userList.get(0).trim().equals("childe")) {
                                    Intent homeIntent = new Intent(MainActivity.this, ChildHomeActivity.class);
                                    startActivity(homeIntent);
                                    finish();
                                }
                            }
                        }
                    }
                });

            }
        }
    }
}