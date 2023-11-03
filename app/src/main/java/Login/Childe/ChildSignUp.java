package Login.Childe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import Login.ParentSignUp;
import Models.ParentAccount;

public class ChildSignUp extends AppCompatActivity {

    Button signUpBtn;
    TextView logInTV;
    EditText mailETxt, passETxt, passREETxt, parentETxt;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String mailStr, passStr, parentMailStr;
    Boolean RegistedFlg;
    LottieAnimationView btnLoadingAnime;
    LottieDialog sendingMailDialog, mailSentDialog;
    Button lottiebtn;
    DocumentReference parentDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_sign_up);
        init();

        parentETxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setParentRef();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                    }else if(TextUtils.isEmpty(parentETxt.getText().toString())){
                        parentETxt.setError("Please Enter Your Parent Email");
                        signUpBtn.setVisibility(View.VISIBLE);
                        btnLoadingAnime.setVisibility(View.GONE);
                    } else if (passETxt.getText().toString().trim().equals(passREETxt.getText().toString().trim())) {

                        if(parentDocument != null){
                        mailStr = mailETxt.getText().toString().trim();
                        passStr = passETxt.getText().toString().trim();
                        parentMailStr = parentETxt.getText().toString().trim();


                        mAuth.createUserWithEmailAndPassword(mailStr, passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(ChildSignUp.this, "Registed", Toast.LENGTH_SHORT).show();
                                    signUpBtn.setText("Verify");
                                    //below 2 line code will store the data in firebase firestore
                                    setUserID();
                                    setRelation(parentMailStr);
                                    //
                                    RegistedFlg = true;
                                    signUpBtn.setVisibility(View.VISIBLE);
                                    btnLoadingAnime.setVisibility(View.GONE);
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ChildSignUp.this);
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
                                        Toast.makeText(ChildSignUp.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();

                                    } else {
                                        signUpBtn.setVisibility(View.VISIBLE);
                                        btnLoadingAnime.setVisibility(View.GONE);
                                        sendingMailDialog.cancel();
                                        Toast.makeText(ChildSignUp.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        mailETxt.setError("Invalid Mail");
                                        Log.d("shubham", task.getException().toString());
                                    }
                                }
                            }
                        });
                    }else{
                            signUpBtn.setVisibility(View.VISIBLE);
                            btnLoadingAnime.setVisibility(View.GONE);
                            parentETxt.setError("Parent Account Is Not Created With This ID");
                        }
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

    private void setRelation(String parentMailStr) {
    /*    db.collection("Relation").whereEqualTo("Mail",parentMailStr).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    Log.e("tag","Parent Found");
                    for(QueryDocumentSnapshot obj:task.getResult())
                    {
                        parentDocument = obj.getReference();
                    }
                }else{
                    Log.e("tag","Unable To Find Parent");
                }
            }
        }); */
        HashMap<String,Object> map = new HashMap<>();
        map.put("LinkChild",mailStr);
        parentDocument.update(map);
    }

    void setUserID()
    {
        HashMap<String, String> userData = new HashMap<>();
        userData.put("Email", mAuth.getCurrentUser().getEmail().toString().trim());
        userData.put("User", getIntent().getStringExtra("user").trim());
        db.collection("User").add(userData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.e("tag", "User ID set");
                } else {
                    Log.e("tag", "Unable to set user ID");
                }
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

                        Toast.makeText(ChildSignUp.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChildSignUp.this);
            builder.setTitle("Try to login").setMessage("try to login and come back to this page for verification");
            builder.show();
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    dialog.cancel();
                }
            });
        }

    }
    void setParentRef()
    {
        db.collection("Relation").whereEqualTo("Mail",parentETxt.getText().toString().trim()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot obj:task.getResult())
                    {
                        parentDocument = obj.getReference();
                        signUpBtn.setVisibility(View.VISIBLE);
                        btnLoadingAnime.setVisibility(View.GONE);
                    }
                }else{
                    parentETxt.setError("Parent Not Created Their Account Yet");
                    Log.e("tag","Parent id is not present");
                    signUpBtn.setVisibility(View.VISIBLE);
                    btnLoadingAnime.setVisibility(View.GONE);
                }
            }
        });
    }
    void init() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        RegistedFlg = false;
        signUpBtn = findViewById(R.id.SignUpBtn);
        mailETxt = findViewById(R.id.SignUpMailETxt);
        passETxt = findViewById(R.id.SignUpPassETxt);
        passREETxt = findViewById(R.id.SignUpREPassETxt);
        logInTV = findViewById(R.id.loginTV);
        sendingMailDialog = new LottieDialog(ChildSignUp.this);
        mailSentDialog = new LottieDialog(ChildSignUp.this);
        lottiebtn = new Button(ChildSignUp.this);
        lottiebtn.setText("OK");
        btnLoadingAnime = findViewById(R.id.btnLoading);
        parentETxt = findViewById(R.id.ParentMailETxt);
        sendingMailDialog.setTitle("Sending Mail").setAnimation(R.raw.sending_mail_animation).setAutoPlayAnimation(true).setAnimationRepeatCount(LottieDialog.INFINITE);
        mailSentDialog.setAnimation(R.raw.mailsent).setAutoPlayAnimation(true).setAnimationRepeatCount(0).addActionButton(lottiebtn).setTitle("Mail Sent").setMessage("Please check your In-Box");

    }
}