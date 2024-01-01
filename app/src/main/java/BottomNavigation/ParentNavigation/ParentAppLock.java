package BottomNavigation.ParentNavigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import BottomNavigation.ParentNavigation.ParentListAdapter.ParentAppListAdapter;
import BottomNavigation.ParentNavigation.ParentListAdapter.ParentAppListModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParentAppLock#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParentAppLock extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;

    TextView appListName;

    FirebaseAuth mAuth;

    FirebaseFirestore db;

    FirebaseStorage firebaseStorage;

    StorageReference storageReference;

    ArrayList<ParentAppListModel> arrayList;

    String childName;


    public ParentAppLock() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParentAppLock.
     */
    // TODO: Rename and change types and number of parameters
    public static ParentAppLock newInstance(String param1, String param2) {
        ParentAppLock fragment = new ParentAppLock();
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
        View view = inflater.inflate(R.layout.fragment_parent_app_lock, container, false);
        recyclerView = view.findViewById(R.id.parentAppListRV);
        appListName = view.findViewById(R.id.appListNameTV);
        init();
        setAppListName();
        getChildAppList();


        return view;

    }

    void init() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        arrayList = new ArrayList<>();
    }

    void setAppListName() {

        db.collection("Relation").whereEqualTo("Mail", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String childName = document.getString("LinkChild");
                        appListName.setText(childName + " Apps");

                    }
                }
            }
        });
    }

    void getChildAppList() {

        db.collection("Relation").whereEqualTo("Mail", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        childName = document.getString("LinkChild");
                        if (childName != null) {
                            Log.e("tag", "collection found!!!!!!!!!!!");
                            db.collection(childName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.e("tag", childName + " collecton is successful");
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            ParentAppListModel obj = new ParentAppListModel();
                                            obj.setAppName(document.getString("AppName"));
                                            Log.e("tag", "ObjAppName : " + obj.getAppName());
                                            obj.setPackageName(document.getString("PackageName"));
                                            arrayList.add(obj);
                                        }
                                        setAppListAdapter();
                                    } else {
                                        Log.e("tag", childName + " collecton is unsuccessful");
                                    }
                                }
                            });
                        } else {
                            Log.e("tag", "collection not found");
                        }
                    }

                }
            }
        });


    }

    void setAppListAdapter() {

        ParentAppListAdapter parentAppListAdapter = new ParentAppListAdapter(getContext(), arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(parentAppListAdapter);

        Log.e("tag", "AppName : " + arrayList.get(0).getAppName());
    }
}