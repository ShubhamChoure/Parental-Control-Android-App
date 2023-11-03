package Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.amrdeveloper.lottiedialog.LottieDialog;
import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import Models.ParentAccount;

public class ParentSignUp extends AppCompatActivity {

    Button signUpBtn;
    TextView logInTV;
    EditText mailETxt, passETxt, passREETxt;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String mailStr, passStr;
    Boolean RegistedFlg;
    ParentAccount parentAccount;
    LottieAnimationView btnLoadingAnime;
    LottieDialog sendingMailDialog, mailSentDialog;
    Button lottiebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_sign_up);
        init();

        sendingMailDialog.setTitle("Sending Mail").setAnimation(R.raw.sending_mail_animation).setAutoPlayAnimation(true).setAnimationRepeatCount(LottieDialog.INFINITE);
        mailSentDialog.setAnimation(R.raw.mailsent).setAutoPlayAnimation(true).setAnimationRepeatCount(0).addActionButton(lottiebtn).setTitle("Mail Sent").setMessage("Please check your In-Box");
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RegistedFlg == false) {
                    signUpBtn.setVisibility(View.GONE);
                    btnLoadingAnime.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(mailETxt.getText().toString())) {
                        mailETxt.setError("Please Enter Your Mail");

                        signUpBtn.setVisibility(View.VISIBLE);
                        btnLoadingAnime.setVisibility(View.GONE);
                    } else if (TextUtils.isEmpty(passETxt.getText().toString())) {
                        passETxt.setError("Please Set Password");

                        signUpBtn.setVisibility(View.VISIBLE);
                        btnLoadingAnime.setVisibility(View.GONE);
                    } else if (TextUtils.isEmpty(passREETxt.getText().toString())) {
                        passREETxt.setError("Please Re-Enter The Password");

                        signUpBtn.setVisibility(View.VISIBLE);
                        btnLoadingAnime.setVisibility(View.GONE);
                    } else if (passETxt.getText().toString().trim().equals(passREETxt.getText().toString().trim())) {
                        mailStr = mailETxt.getText().toString().trim();
                        passStr = passETxt.getText().toString().trim();


                        mAuth.createUserWithEmailAndPassword(mailStr, passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(ParentSignUp.this, "Registed", Toast.LENGTH_SHORT).show();
                                    signUpBtn.setText("Verify");
                                    //below 3 line code for registeing parent in firebase firestore
                                    parentAccount.setMail(mailStr);
                                    setUserID();
                                    setRelation(parentAccount);
                                    //
                                    RegistedFlg = true;
                                    signUpBtn.setVisibility(View.VISIBLE);
                                    btnLoadingAnime.setVisibility(View.GONE);
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ParentSignUp.this);
                                        builder.setTitle("Mail Already In-Use");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                signUpBtn.setText("Verify");
                                                RegistedFlg = true;
                                                signUpBtn.setVisibility(View.VISIBLE);
                                                btnLoadingAnime.setVisibility(View.GONE);
                                            }
                                        });
                                        builder.show();
                                        signUpBtn.setVisibility(View.VISIBLE);
                                        btnLoadingAnime.setVisibility(View.GONE);
                                        Toast.makeText(ParentSignUp.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();

                                    }
                                    else {
                                        signUpBtn.setVisibility(View.VISIBLE);
                                        btnLoadingAnime.setVisibility(View.GONE);
                                        sendingMailDialog.cancel();
                                        Toast.makeText(ParentSignUp.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        mailETxt.setError("Invalid Mail");
                                        Log.d("shubham", task.getException().toString());
                                    }
                                }
                            }
                        });
                    } else {
                        signUpBtn.setVisibility(View.VISIBLE);
                        btnLoadingAnime.setVisibility(View.GONE);
                        sendingMailDialog.cancel();
                        passREETxt.setError("Password Not Match");
                    }
                } else if (RegistedFlg == true) {
                    verifyMail();
                }
            }
        });
        logInTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lottiebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailSentDialog.cancel();
                finish();
            }
        });
    }

    void setUserID()
    {
        HashMap<String,String> userData = new HashMap<>();
        userData.put("Email",mAuth.getCurrentUser().getEmail().toString().trim());
        userData.put("User",getIntent().getStringExtra("user").trim());
        db.collection("User").add(userData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    Log.e("tag","User ID set");
                }
                else{
                    Log.e("tag","Unable to set user ID");
                }
            }
        });
    }
    void setRelation(ParentAccount parentAccount) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Mail", parentAccount.getMail());
        hashMap.put("LinkChild", parentAccount.getLinkchild());
        db.collection("Relation").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("tag", "Parent mail added to firestore");
            }
        });
    }

    void verifyMail() {
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        sendingMailDialog.cancel();
                        mailSentDialog.show();
                    } else {

                        Toast.makeText(ParentSignUp.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ParentSignUp.this);
            builder.setTitle("Try to login").setMessage("try to login and come back to this page for verification");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    dialog.cancel();
                }
            });
            builder.show();
        }

    }
    void init() {
        parentAccount = new ParentAccount();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        RegistedFlg = false;
        signUpBtn = findViewById(R.id.SignUpBtn);
        mailETxt = findViewById(R.id.SignUpMailETxt);
        passETxt = findViewById(R.id.SignUpPassETxt);
        passREETxt = findViewById(R.id.SignUpREPassETxt);
        logInTV = findViewById(R.id.loginTV);
        sendingMailDialog = new LottieDialog(ParentSignUp.this);
        mailSentDialog = new LottieDialog(ParentSignUp.this);
        lottiebtn = new Button(ParentSignUp.this);
        lottiebtn.setText("OK");
        btnLoadingAnime = findViewById(R.id.btnLoading);

    }
}