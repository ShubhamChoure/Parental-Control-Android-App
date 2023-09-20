package Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import HomeActivity.HomeActivity;

public class ParentLogin extends AppCompatActivity {
    EditText mailEDtxt,passEDtxt;
    TextView forgetTV,signUpTV;
    Button continueBtn;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String mailStr,passStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parentlogin);
        init();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
             if(TextUtils.isEmpty(mailEDtxt.getText().toString()))
             {
                 mailEDtxt.setError("Please Enter Mail");
             } else if (TextUtils.isEmpty(passEDtxt.getText().toString())) {
                 passEDtxt.setError("Please Enter Password");
             }
             else{
                 mailStr = mailEDtxt.getText().toString().trim();
                 passStr = passEDtxt.getText().toString().trim();
                 mAuth.signInWithEmailAndPassword(mailStr,passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful())
                         {
                             currentUser = mAuth.getCurrentUser();
                             if(currentUser.isEmailVerified())
                             {
                                 Intent homeIntent = new Intent(ParentLogin.this, HomeActivity.class);
                                 startActivity(homeIntent);
                             }else if(currentUser.isEmailVerified()==false) {
                                 AlertDialog.Builder builder = new AlertDialog.Builder(ParentLogin.this);
                                 builder.setTitle("Unverified E-Mail");
                                 builder.setMessage("Please Check Your Inbox");
                             }
                         }else{
                             AlertDialog.Builder builder = new AlertDialog.Builder(ParentLogin.this);
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
                         Toast.makeText(ParentLogin.this, "something wemt wrong", Toast.LENGTH_SHORT).show();
                     }
                 });
             }
            }
        });

       signUpTV.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent parentSignUpIntent = new Intent(ParentLogin.this, ParentSignUp.class);
               startActivity(parentSignUpIntent);
           }
       });
       forgetTV.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent forgetIntent = new Intent(ParentLogin.this,ForgetPassword.class);
               startActivity(forgetIntent);
           }
       });
    }
    void init()
    {
        mAuth = FirebaseAuth.getInstance();
        signUpTV = findViewById(R.id.signupTV);
        forgetTV = findViewById(R.id.ForgetTV);
        mailEDtxt = findViewById(R.id.LoginMailETxt);
        continueBtn = findViewById(R.id.continueBtn);
        passEDtxt = findViewById(R.id.LoginPassETxt);
    }
}