package HomeActivity.ChildeHomeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ChildDetailActivity extends AppCompatActivity {
    ArrayList<String> list;
    Spinner spinner;
    Toolbar toolbar;
    SharedPreferences.Editor editor;
    private final int GALLERY_REQ_CODE = 100;
    Button submit;
    FloatingActionButton edit;
    ImageView pic;
    EditText username, userphoneno, emailedittext;
    FirebaseFirestore firestore;
    String logintype, useremail;
    FirebaseUser user;
    FirebaseAuth mAuth;
    CollectionReference cref;
    String encode;
    Uri newuri;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_detail);
        init();
        setToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (spinner.getSelectedItem().equals("Please Select Your Blood Type")) {
                    Toast.makeText(ChildDetailActivity.this, "Please Select Your Blood Type", Toast.LENGTH_SHORT).show();
                }
                String item = adapterView.getItemAtPosition(position).toString();

                Log.e("6969", "" + item);
                // userdetails(useremail.toString() , item , logintype , userphoneno.toString()
                // , username.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent igallery = new Intent(Intent.ACTION_PICK);
                igallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(igallery, GALLERY_REQ_CODE);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userdetails();

                startActivity(new Intent(ChildDetailActivity.this, ChildHomeActivity.class));
                editor.putBoolean("isDetail", true).commit();

            }
        });

    }

    public void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                // Toast.makeText(this, "inside if", Toast.LENGTH_SHORT).show();
                Uri pp = (data.getData());
                Bitmap bitmap;
                pic.setImageURI(pp);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pp);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                encode = Base64.encodeToString(bitmapToByteArray(bitmap), Base64.DEFAULT);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    void userdetails() {
        Map<String, Object> User = new HashMap<>();
        String blood = spinner.getSelectedItem().toString();

        User.put("Name", username.getText().toString());
        User.put("Email", useremail);
        User.put("Phone No", userphoneno.getText().toString());
        User.put("Blood Type", blood);
        User.put("uri", encode);

        firestore.collection("User").whereEqualTo("Email",mAuth.getCurrentUser().getEmail().toString().trim()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    DocumentReference userRefrence;
                    for(QueryDocumentSnapshot document:task.getResult())
                    {
                        userRefrence = document.getReference();
                        userRefrence.update(User);
                    }

                }
            }
        });

    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public void init() {
        toolbar = findViewById(R.id.userdetail);

        edit = findViewById(R.id.editpic);
        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        pic = findViewById(R.id.profilepic);
        useremail = mAuth.getCurrentUser().getEmail().toString();
        emailedittext = findViewById(R.id.emailid);
        cref = FirebaseFirestore.getInstance().collection("Users Details");
        userphoneno = findViewById(R.id.userphone);
        firestore = FirebaseFirestore.getInstance();
        spinner = findViewById(R.id.spinneruserbloodtype);
        submit = findViewById(R.id.submitbutton);
        preferences = getSharedPreferences("childstate", MODE_PRIVATE);
        editor = preferences.edit();

        emailedittext.setText(useremail);

        list = new ArrayList<>();
        list.add("Please Select Your Blood Type");
        list.add("A+");
        list.add("A-");
        list.add("B+");
        list.add("B-");
        list.add("AB+");
        list.add("AB-");
        list.add("O+");
        list.add("O-");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);
    }

}