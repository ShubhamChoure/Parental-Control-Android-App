package BottomNavigation.ParentNavigation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jspm.MainActivity;
import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;

import HomeActivity.ParentHomeActivity.HomeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParentAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParentAccount extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth auth;

    FirebaseFirestore db;
    Button logOutBtn;

    TextView acNameTV,mailAddressTV,mobileNoTV,bloodGroupTV,childMailAddressTV,childNameTV;
    ImageView profilePicIV;

    public ParentAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParentAccount.
     */
    // TODO: Rename and change types and number of parameters
    public static ParentAccount newInstance(String param1, String param2) {
        ParentAccount fragment = new ParentAccount();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parent_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init();
            logoutUser();
            anandi(); //For setting user details from dataabse
            shubham(); //For setting child name and mail address
        }catch (Exception e){
           Log.e("tag",e.toString());
        }
    }

    void anandi() {
        //For setting user details from dataabse

        db.collection("User").whereEqualTo("Email",auth.getCurrentUser().getEmail().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                acNameTV.setText(task.getResult().getDocuments().get(0).getString("Name"));
                mailAddressTV.setText(task.getResult().getDocuments().get(0).getString("Email"));
                mobileNoTV.setText(task.getResult().getDocuments().get(0).getString("Phone No"));
                bloodGroupTV.setText(task.getResult().getDocuments().get(0).getString("Blood Type"));

                String base64Encoded = task.getResult().getDocuments().get(0).getString("uri");
                byte[] byteArray = Base64.decode(base64Encoded,Base64.DEFAULT);

                Drawable imgDrawable = byteArrayToDrawable(byteArray, "Profile Pic");
                if(imgDrawable!=null){
                    profilePicIV.setImageDrawable(imgDrawable);
                }
            }
        });

    }

    void shubham(){
        //For setting child name and mail address
        db.collection("Relation").whereEqualTo("Mail",auth.getCurrentUser().getEmail().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        childMailAddressTV.setText(task.getResult().getDocuments().get(0).getString("LinkChild"));
                        setChildName();

            }
        });
    }

    void setChildName() {
        db.collection("User").whereEqualTo("Email",childMailAddressTV.getText().toString().trim()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot document:task.getResult()){
                    childNameTV.setText(document.getString("Name"));
                }
            }
        });
    }

    void init()
    {
        auth = FirebaseAuth.getInstance();
        logOutBtn = getView().findViewById(R.id.ParentLogOut);
        db = FirebaseFirestore.getInstance();

        acNameTV = getView().findViewById(R.id.parentACName);
        mailAddressTV = getView().findViewById(R.id.mailParentACTV);
        mobileNoTV = getView().findViewById(R.id.mobileNoParentACTV);
        bloodGroupTV = getView().findViewById(R.id.bloodParentACTV);
        childMailAddressTV = getView().findViewById(R.id.childMailParentACTV);
        childNameTV = getView().findViewById(R.id.childNameParentACTV);
        profilePicIV = getView().findViewById(R.id.profilePicParentIV);
    }

    void logoutUser(){
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(HomeActivity.ParentHomeContext, MainActivity.class);
                HomeActivity.ParentHomeContext.startActivity(intent);
                ((Activity) HomeActivity.ParentHomeContext ).finish();
            }
        });
    }
    Drawable byteArrayToDrawable(byte[] bar, String appName) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bar);
        Drawable drawable = Drawable.createFromStream(byteArrayInputStream, appName);
        return drawable;
    }
}