package BottomNavigation.ChildeNavigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jspm.MainActivity;
import com.example.jspm.R;
import com.google.firebase.firestore.FirebaseFirestore;

import HomeActivity.ChildeHomeActivity.ChildHomeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChildeAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChildeAccount extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    Button logoutBtn;
    TextView acNameTV,mailAddressTV,mobileNoTV,bloodGroupTV,childMailAddressTV,childNameTV;
    ImageView profilePicIV;
    FirebaseFirestore db;
    public ChildeAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChildeAccount.
     */
    // TODO: Rename and change types and number of parameters
    public static ChildeAccount newInstance(String param1, String param2) {
        ChildeAccount fragment = new ChildeAccount();
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
        return inflater.inflate(R.layout.fragment_childe_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChildHomeActivity.mAuth.signOut();
                Intent mainIntent = new Intent(ChildHomeActivity.MyContext, MainActivity.class);
                getActivity().startActivity(mainIntent);
                ((Activity)ChildHomeActivity.MyContext).finish();
            }
        });
    }

    void init()
    {
        logoutBtn = getView().findViewById(R.id.ChildFragmentLogoutBtn);
        db = FirebaseFirestore.getInstance();

        acNameTV = getView().findViewById(R.id.parentACName);
        mailAddressTV = getView().findViewById(R.id.mailParentACTV);
        mobileNoTV = getView().findViewById(R.id.mobileNoParentACTV);
        bloodGroupTV = getView().findViewById(R.id.bloodParentACTV);
        childMailAddressTV = getView().findViewById(R.id.childMailParentACTV);
        childNameTV = getView().findViewById(R.id.childNameParentACTV);
        profilePicIV = getView().findViewById(R.id.profilePicParentIV);
    }
}