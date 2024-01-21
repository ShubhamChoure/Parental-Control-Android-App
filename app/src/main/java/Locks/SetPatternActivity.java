package Locks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.itsxtt.patternlock.PatternLockView;

import java.util.ArrayList;
import java.util.HashMap;

public class SetPatternActivity extends AppCompatActivity {

    ImageView imageView;
    PatternLockView patternLockView;
    Boolean firstTimeFLag;
    ArrayList<Integer> patternArray;

    LottieDialog lottieDialog;
    Button lottieDialogBtn;
    FirebaseFirestore db;
    AlertDialog.Builder builder;
    FirebaseAuth mAuth;
    String childName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pattern);

        init();
        showFirstDialouge();

        patternLockView.setOnPatternListener(new PatternLockView.OnPatternListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(@NonNull ArrayList<Integer> arrayList) {

            }

            @Override
            public boolean onComplete(@NonNull ArrayList<Integer> arrayList) {
                if(firstTimeFLag){
                    patternArray= arrayList;
                    showSecondDialouge();
                    firstTimeFLag = false;
                }else{
                    if(patternArray.equals(arrayList)){
                        uploadPattern(arrayList);
                    }else{
                        firstTimeFLag = true;
                        showFailedDialouge();
                    }
                }
                return true;
            }
        });
    }
    void init(){
        patternLockView = findViewById(R.id.parentPatternLockView);
        imageView = findViewById(R.id.parentPatternLockIV);
        patternArray= new ArrayList<>();
        firstTimeFLag = true;
        builder = new AlertDialog.Builder(this);
        db = FirebaseFirestore.getInstance();
        lottieDialog = new LottieDialog(this);
        lottieDialogBtn = new Button(this);
        mAuth = FirebaseAuth.getInstance();
    }
    void showFirstDialouge(){
        builder.setTitle("Enter New Pattern");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }
    void showSecondDialouge(){
        builder.setTitle("Please Confirm Pattern");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }
    void showFailedDialouge(){
        builder.setTitle("Oops Pattern Is Not Matching");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showFirstDialouge();
                dialog.cancel();
            }
        }).show();
    }
    void showSuccessDialouge(){
        builder.setTitle("Pattern Is Now Set");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.cancel();
            }
        }).show();
    }
    void uploadPattern(ArrayList<Integer> arrayListPattern){

        lottieDialogBtn.setVisibility(View.INVISIBLE);
        lottieDialogBtn.setText("Next");

        lottieDialog.setAnimation(R.raw.lock_loading);
        lottieDialog.setAutoPlayAnimation(true);
        lottieDialog.setAnimationRepeatCount(50);
        lottieDialog.setTitle("Please Wait Setting Up Pattern Lock");
        lottieDialog.addActionButton(lottieDialogBtn);
        lottieDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottieDialog.cancel();
                showSuccessDialouge();
            }
        });
        lottieDialog.show();

        db.collection("Relation").whereEqualTo("Mail",mAuth.getCurrentUser().getEmail().trim()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    childName = task.getResult().getDocuments().get(0).getString("LinkChild");
                    if (childName!=null) {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("Pattern",arrayListPattern);
                        db.collection(childName+" Pattern").document(childName + " Pattern").set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    lottieDialogBtn.setVisibility(View.VISIBLE);
                                    Log.e("setPattern","pattern uplaod Successful");
                                }else{
                                    lottieDialogBtn.setVisibility(View.VISIBLE);
                                    Toast.makeText(SetPatternActivity.this, "Pattern Upload Failed", Toast.LENGTH_SHORT).show();
                                    Log.e("setPattern","pattern uplaod unSuccessful");
                                }
                            }
                        });

                    }else{
                        lottieDialog.cancel();
                        Toast.makeText(SetPatternActivity.this, "Child Is Not Linked With This Account", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Log.e("tag","Child Name Fetch Failed");
                }
            }
        });

    }
}