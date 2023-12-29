package BottomNavigation.ChildeNavigation;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.jspm.MainActivity;
import com.example.jspm.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import BottomNavigation.ChildeNavigation.AppListAdapter.AppListAdapter;
import BottomNavigation.ChildeNavigation.AppListAdapter.AppListModel;
import HomeActivity.ChildeHomeActivity.ChildHomeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChildAppLock#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChildAppLock extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView chlidAppListRV;
    AppListAdapter appListAdapter;
    ArrayList<AppListModel> appListModels;

    FirebaseFirestore db;

    StorageReference storageReference ;

    public ChildAppLock() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChildAppLock.
     */
    // TODO: Rename and change types and number of parameters
    public static ChildAppLock newInstance(String param1, String param2) {
        ChildAppLock fragment = new ChildAppLock();
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
        View view = inflater.inflate(R.layout.fragment_child_app_lock, container, false);
        chlidAppListRV = view.findViewById(R.id.chlidAppListRV);
        init();
        setApplist();
        UploadAppList();
        return view;
    }
    void init()
    {

        appListModels = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    void setApplist()
    {
        getUserApp();
        appListAdapter = new AppListAdapter(getContext(),appListModels);
        chlidAppListRV.setLayoutManager(new LinearLayoutManager(getContext()));
        chlidAppListRV.setAdapter(appListAdapter);
    }
    void getUserApp()
    {
        PackageManager pm = getActivity().getPackageManager();
        List<PackageInfo> packageList = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        for (PackageInfo i:packageList) {

            AppListModel obj = new AppListModel();
            obj.setAppName(i.applicationInfo.loadLabel(pm).toString());
            obj.setPackageName(i.applicationInfo.packageName);
            obj.setAppIcon(i.applicationInfo.loadIcon(pm));
            appListModels.add(obj);
        }

    }

    void UploadAppList()
    {



        for (AppListModel i : appListModels) {
            HashMap<String,Object> hashMap;
            hashMap = new HashMap<String,Object>();
            hashMap.put("AppName",i.getAppName());
            hashMap.put("PackageName",i.getPackageName());

            CollectionReference collectionReference = db.collection("AppList");
            String id = i.getAppName();
            try {
                collectionReference.document(id).set(hashMap);
                Log.e("tag","document already exist");
            }
            catch (Exception  e)
            {
             Log.e("tag", e.toString());
            }



        }

    }


}