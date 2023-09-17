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

import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import Models.ParentAccount;

public class ParentSignUp extends AppCompatActivity {

    Button signUpBtn;
    EditText mailETxt,passETxt;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String mailStr,passStr;
    Boolean RegistedFlg;
    ParentAccount parentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_sign_up);
        init();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegistedFlg==false) {
                    if (TextUtils.isEmpty(mailETxt.getText().toString())) {
                        mailETxt.setError("Please Enter Your Mail");
                    } else if (TextUtils.isEmpty(passETxt.getText().toString())) {
                        passETxt.setError("Please Set Password");
                    } else {
                        mailStr = mailETxt.getText().toString().trim();
                        passStr = passETxt.getText().toString().trim();
                        mAuth.createUserWithEmailAndPassword(mailStr, passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ParentSignUp.this, "Registed", Toast.LENGTH_SHORT).show();
                                    signUpBtn.setText("Verify");
                                    RegistedFlg = true;
                                } else {
                                    Toast.makeText(ParentSignUp.this, "Mail is Already Registed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else if(RegistedFlg==true)
                {
                    ProgressDialog progressDialog = new ProgressDialog(ParentSignUp.this);
                    progressDialog.setTitle("Sending Mail");
                    progressDialog.setMessage("Please Wait");
                    progressDialog.show();
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.cancel();
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ParentSignUp.this, "Email Send", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(ParentSignUp.this);
                                builder.setTitle("Verification Mail Sent");
                                builder.setMessage("Please Verify Your Mail To Log In");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        parentAccount.setMail(mailStr);
                                        setRelation(parentAccount);
                                        dialog.cancel();
                                        Intent LogInIntent = new Intent(ParentSignUp.this,ParentLogin.class);
                                        startActivity(LogInIntent);
                                        finish();
                                    }
                                });
                                builder.show();
                            }
                        }
                    });
                }
            }
        });
        }
        void init()
        {
            parentAccount = new ParentAccount();
            db = FirebaseFirestore.getInstance();
         mAuth = FirebaseAuth.getInstance();
         RegistedFlg = false;
         signUpBtn = findViewById(R.id.SignUpBtn);
         mailETxt = findViewById(R.id.SignUpMailETxt);
         passETxt = findViewById(R.id.SignUpPassETxt);
        }
        void setRelation(ParentAccount parentAccount)
        {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("Mail",parentAccount.getMail());
            hashMap.put("LinkChild",parentAccount.getLinkchild());
            db.collection("Relation").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(ParentSignUp.this, "Mail Added To FireStore", Toast.LENGTH_SHORT).show();
            }
        });
        }
    }