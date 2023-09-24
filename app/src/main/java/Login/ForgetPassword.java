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

import com.airbnb.lottie.LottieAnimationView;
import com.amrdeveloper.lottiedialog.LottieDialog;
import com.example.jspm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    EditText mailETxt;
    Button submitBtn;
    Button lottiebtn;
    FirebaseAuth mAuth;
    LottieAnimationView btnLoading;
    LottieDialog mailDialog,mailSentAnime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        init();

        mailDialog.setAnimation(R.raw.sending_mail_animation).setTitle("Sending Mail").setAnimationRepeatCount(LottieDialog.INFINITE).setAutoPlayAnimation(true);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBtn.setVisibility(submitBtn.GONE);
                btnLoading.setVisibility(btnLoading.VISIBLE);
                mailDialog.show();
                if(TextUtils.isEmpty(mailETxt.getText().toString()))
                {
                    submitBtn.setVisibility(submitBtn.VISIBLE);
                    btnLoading.setVisibility(btnLoading.GONE);
                    mailDialog.dismiss();
                    mailETxt.setError("Please Enter E-Mail");
                } else{
                    mAuth.sendPasswordResetEmail(mailETxt.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            submitBtn.setVisibility(submitBtn.VISIBLE);
                            btnLoading.setVisibility(btnLoading.GONE);
                            lottiebtn.setText("OK");
                            mailSentAnime = new LottieDialog(ForgetPassword.this).setAnimation(R.raw.mailsent).setAutoPlayAnimation(true).setAnimationRepeatCount(0).addActionButton(lottiebtn).setTitle("Mail Sent").setMessage("Please check your In-Box");
                            mailSentAnime.show();
                            mailDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            submitBtn.setVisibility(submitBtn.VISIBLE);
                            btnLoading.setVisibility(btnLoading.GONE);
                            mailDialog.dismiss();
                            mailETxt.setError("Mail is not valid");
                        }
                    });
                }
            }
        });
        lottiebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailSentAnime.dismiss();
                finish();
            }
        });
    }
    void init()
    {
        btnLoading = findViewById(R.id.btnLoading);
        mailETxt = findViewById(R.id.ForgetMailETxt);
        submitBtn = findViewById(R.id.submitBtn);
        mAuth = FirebaseAuth.getInstance();
        mailDialog = new LottieDialog(ForgetPassword.this);
        lottiebtn = new Button(ForgetPassword.this);
    }
}