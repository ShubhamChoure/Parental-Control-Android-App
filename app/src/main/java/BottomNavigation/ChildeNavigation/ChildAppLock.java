package BottomNavigation.ChildeNavigation;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Toast;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.example.jspm.MainActivity;
import com.example.jspm.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import BottomNavigation.ChildeNavigation.AppListAdapter.AppListAdapter;
import BottomNavigation.ChildeNavigation.AppListAdapter.AppListModel;
import BottomNavigation.ParentNavigation.ParentListAdapter.ParentAppListModel;
import HomeActivity.ChildeHomeActivity.ChildHomeActivity;
import HomeActivity.ParentHomeActivity.HomeActivity;

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
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    SearchView searchView;

    FirebaseAuth mAuth;

    Button updateStatusBtn;
    AlarmManager alarmManager;
    long alarmTime;
    public static final int ALARM_REQ_CODE = 100;



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
        searchView = view.findViewById(R.id.chlidAppListSV);
        updateStatusBtn = view.findViewById(R.id.updateStatusBtn);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAppList(newText);
                return true;
            }
        });


        updateStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadLockstatus();
            }
        });

        init();
        setApplist();
        UploadAppList();
        UploadAppList();
        startAlarmManager();
        return view;
    }
    void init()
    {

        appListModels = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        mAuth = FirebaseAuth.getInstance();
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
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
        List<ApplicationInfo> packageList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo i:packageList) {

            AppListModel obj = new AppListModel();
            obj.setAppName(i.loadLabel(pm).toString());
            obj.setPackageName(i.packageName);
            obj.setAppIcon(i.loadIcon(pm));
            appListModels.add(obj);
        }

    }

    void UploadAppList(){


        Toast.makeText(getContext(), "Wait till app upload is complete", Toast.LENGTH_SHORT).show();
        for (AppListModel i : appListModels) {
            HashMap<String,Object> hashMap;
            hashMap = new HashMap<String,Object>();
            hashMap.put("AppName",i.getAppName());
            hashMap.put("PackageName",i.getPackageName());

            CollectionReference collectionReference = db.collection(ChildHomeActivity.mAuth.getCurrentUser().getEmail());
            String id = i.getAppName();
            try {
                collectionReference.document(id).set(hashMap);
            }
            catch (Exception  e)
            {
             Log.e("tag", e.toString());
            }
        storageReference.child("Icon/"+i.getAppName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
             @Override
             public void onSuccess(Uri uri) {

                 Log.e("tag","Icon Already Uploaded");

             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 StorageReference iconStrRef = storageReference.child("Icon").child(i.getAppName());
                 byte[] data = bitmapTobyteArray(drawableToBitmap(i.getAppIcon()));
                 uploadAppIcon(data,iconStrRef);

             }
         });

        }

    }


    byte[] bitmapTobyteArray(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }
    Bitmap drawableToBitmap(Drawable drawable)
    {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
    }

    void uploadAppIcon(byte[] data,StorageReference iconStrRef){
        UploadTask uploadTask = iconStrRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("tag","icon upload unsucessful");
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Log.e("tag","Icon Upload Sucessful");
            }
        });
    }
    private void filterAppList(String newText) {
        ArrayList<AppListModel> filteredList = new ArrayList<>();
        for(AppListModel i : appListModels){
            if(i.getAppName().toLowerCase().trim().contains(newText.toLowerCase().trim())){
                filteredList.add(i);
                appListAdapter.setFilteredList(filteredList);
            }
        }
    }
    void downloadLockstatus()
    {
        db.collection(mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        String appName = document.get("AppName").toString();
                       Boolean lockStatus = (Boolean)document.get("LockStatus");
                       if (lockStatus != null)
                       {
                           ChildHomeActivity.childlockEditor.putBoolean(appName,lockStatus).commit();
                           Log.e("tagStatus","lock status updated");
                       }
                        else {
                           ChildHomeActivity.childlockEditor.putBoolean(appName,false).commit();
                           Log.e("tagStatus","lock status is null");
                       }
                    }
                }
                else {
                    Log.e("tag","Download lock status task failed");
                }
            }
        });
    }
    void startAlarmManager(){
        Intent intent = new Intent(getContext(),AppLockAlarmReciver.class);
        PendingIntent pe = PendingIntent.getBroadcast(getContext(),ALARM_REQ_CODE,intent,PendingIntent.FLAG_MUTABLE);
        alarmTime = System.currentTimeMillis() + 2 * 1000;
        alarmManager.set(AlarmManager.RTC,alarmTime,pe);
    }
}