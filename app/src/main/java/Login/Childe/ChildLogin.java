package Login.Childe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import HomeActivity.ChildeHomeActivity.ChildDetailActivity;
import HomeActivity.ChildeHomeActivity.ChildHomeActivity;

public class ChildLogin extends AppCompatActivity {

    EditText mailEDtxt,passEDtxt;
    TextView forgetTV,signUpTV;
    Button continueBtn;

    SharedPreferences preferences;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String mailStr,passStr;
    LottieAnimationView btnLoadAni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_login);
        init();
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser = mAuth.getCurrentUser();
                Log.d("tag","inside onClick");
                if(currentUser!=null)
                {
                    mAuth.signOut();
                    Log.d("tag","Sign Out the current user");
                }
                continueBtn.setVisibility(continueBtn.GONE);
                btnLoadAni.setVisibility(btnLoadAni.VISIBLE);
                if(TextUtils.isEmpty(mailEDtxt.getText().toString()))
                {
                    continueBtn.setVisibility(continueBtn.VISIBLE);
                    btnLoadAni.setVisibility(btnLoadAni.GONE);
                    mailEDtxt.setError("Please Enter Mail");
                } else if (TextUtils.isEmpty(passEDtxt.getText().toString())) {
                    continueBtn.setVisibility(continueBtn.VISIBLE);
                    btnLoadAni.setVisibility(btnLoadAni.GONE);
                    passEDtxt.setError("Please Enter Password");
                }
                else{
                    Log.d("tag","inside else all fields are filled");
                    mailStr = mailEDtxt.getText().toString().trim();
                    passStr = passEDtxt.getText().toString().trim();
                    mAuth.signInWithEmailAndPassword(mailStr,passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("tag","Inside on complete");
                            if(task.isSuccessful())
                            {
                                currentUser = mAuth.getCurrentUser();
                                if(currentUser!=null)
                                {
                                    if(currentUser.isEmailVerified())
                                    {
                                        boolean isDetailSubmited = preferences.getBoolean("isDetail",false);
                                        if(!isDetailSubmited) {
                                            Log.e("tag","inside verified mail");
                                            Intent detailIntent = new Intent(ChildLogin.this, ChildDetailActivity.class);
                                            startActivity(detailIntent);
                                            finish();
                                        }else{
                                            Log.e("tag","inside verified mail");
                                            Intent homeIntent = new Intent(ChildLogin.this, ChildHomeActivity.class);
                                            startActivity(homeIntent);
                                            finish();
                                        }

                                    }else {
                                        Log.e("tag","mail is unverified");
                                        continueBtn.setVisibility(continueBtn.VISIBLE);
                                        btnLoadAni.setVisibility(btnLoadAni.GONE);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ChildLogin.this);
                                        builder.setMessage("Please Check Your In-Box");
                                        builder.setTitle("Mail Is Unverified");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        builder.show();
                                    }
                                }
                                else {
                                    Log.d("tag","current user is null");
                                }
                            }else{
                                Log.d("tag","mail sent task unsuccessful");
                                continueBtn.setVisibility(continueBtn.VISIBLE);
                                btnLoadAni.setVisibility(btnLoadAni.GONE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(ChildLogin.this);
                                builder.setTitle("LogIn Failed");
                                builder.setMessage("E-Mail or Password Incorrect");

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag","inside on failure");
                            continueBtn.setVisibility(continueBtn.VISIBLE);
                            btnLoadAni.setVisibility(btnLoadAni.GONE);
                            Toast.makeText(ChildLogin.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        signUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent childSignUpIntent = new Intent(ChildLogin.this, ChildSignUp.class);
                childSignUpIntent.putExtra("user",getIntent().getStringExtra("user"));
                startActivity(childSignUpIntent);
            }
        });
        forgetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgetIntent = new Intent(ChildLogin.this, ChildForgetPassword.class);
                startActivity(forgetIntent);
            }
        });
    }
    void init()
    {
        preferences = getSharedPreferences("childstate", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        signUpTV = findViewById(R.id.signupTV);
        forgetTV = findViewById(R.id.ForgetTV);
        mailEDtxt = findViewById(R.id.LoginMailETxt);
        continueBtn = findViewById(R.id.continueBtn);
        passEDtxt = findViewById(R.id.LoginPassETxt);
        btnLoadAni = findViewById(R.id.btnLoading);
    }
}