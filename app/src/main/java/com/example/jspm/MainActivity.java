package com.example.jspm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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

static Boolean overlayPermissionFlag;

public static final int reqCode = 169;

    ArrayList<String> userList;

    public static Context MyContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
       // askForSystemAlertWindowPermission(((AppCompatActivity)MainActivity.this),reqCode);
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
        MyContext = MainActivity.this;
        mAuth = FirebaseAuth.getInstance();
        parentIV = findViewById(R.id.parentlogin);
        childIV = findViewById(R.id.childlogin);
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        overlayPermissionFlag = false;
    }
    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        if(checkPermission()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                if (currentUser.isEmailVerified()) {
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
        } else{
           AlertDialog.Builder  builder = new AlertDialog.Builder(this);
            builder.setTitle("Permission Not Granted !!!!");
            builder.setMessage("Allow Usage Permission");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    askForSystemAlertWindowPermission(MainActivity.this,reqCode);
                    MainActivity.this.startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }

    }
    boolean checkPermission()
    {
        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager)
                MainActivity.this.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), MainActivity.this.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (MainActivity.this.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }
        return  granted;
    }
    public static void askForSystemAlertWindowPermission(AppCompatActivity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, requestCode);
            overlayPermissionFlag = true;
        } else {
            overlayPermissionFlag = false;
        }
    }

}