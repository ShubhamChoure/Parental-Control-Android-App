package Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jspm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    EditText mailETxt;
    Button submitBtn;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        init();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Sending Mail");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                if(TextUtils.isEmpty(mailETxt.getText().toString()))
                {
                    mailETxt.setError("Please Enter E-Mail");
                } else{
                    mAuth.sendPasswordResetEmail(mailETxt.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                         progressDialog.cancel();
                            AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPassword.this);
                            builder.setTitle("Verification Mail Sent");
                            builder.setMessage("Please check your InBox");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
                            builder.show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.cancel();
                            mailETxt.setError("Mail is not valid");
                        }
                    });
                }
            }
        });
    }
    void init()
    {
        mailETxt = findViewById(R.id.ForgetMailETxt);
        submitBtn = findViewById(R.id.submitBtn);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(ForgetPassword.this);
    }
}