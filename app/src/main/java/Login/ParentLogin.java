package Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jspm.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ParentLogin extends AppCompatActivity {
    EditText mailEDtxt,passEDtxt;
    TextView passTV;
    Button continueBtn;
    FirebaseFirestore db;
    CollectionReference FireRef;
    String mailStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parentlogin);
        init();

       continueBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mailStr = mailEDtxt.getText().toString();
           FireRef = db.collection("Accounts");
           FireRef.whereEqualTo("Mail",mailStr).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
               @Override
               public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                  int size = queryDocumentSnapshots.getDocuments().size();
                  if(size!=0) {
                      passTV.setVisibility(passTV.VISIBLE);
                      passEDtxt.setVisibility(passEDtxt.VISIBLE);
                      continueBtn.setText("LogIn");
                  }
                  else{
                      Toast.makeText(ParentLogin.this, "Account not exist", Toast.LENGTH_SHORT).show();
                  }
               }
           });
           }
       });
    }
    void init()
    {
        db = FirebaseFirestore.getInstance();
        passTV = findViewById(R.id.PassText);
      mailEDtxt = findViewById(R.id.LoginMailETxt);
      continueBtn = findViewById(R.id.continueBtn);
      passEDtxt = findViewById(R.id.LoginPassETxt);
    }
}