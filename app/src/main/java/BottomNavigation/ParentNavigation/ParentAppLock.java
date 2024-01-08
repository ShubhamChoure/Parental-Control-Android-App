package BottomNavigation.ParentNavigation;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import BottomNavigation.ChildeNavigation.AppListAdapter.AppListModel;
import BottomNavigation.ParentNavigation.ParentListAdapter.ParentAppListAdapter;
import BottomNavigation.ParentNavigation.ParentListAdapter.ParentAppListModel;
import HomeActivity.ParentHomeActivity.HomeActivity;
import Locks.SetPatternActivity;

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

    Toolbar appListToolbar;

    FirebaseAuth mAuth;

    FirebaseFirestore db;

    FirebaseStorage firebaseStorage;

    StorageReference storageReference;

    ArrayList<ParentAppListModel> arrayList;
    ParentAppListAdapter parentAppListAdapter;

    android.widget.SearchView searchView;
    String childName;
    byte[] appIconDecoded;
    final long ONE_MEGABYTE = 1024 * 1024;

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
        appListToolbar = view.findViewById(R.id.appListToolbar);

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
        setAppListToolbar();
    }

    void setAppListName() {

        db.collection("Relation").whereEqualTo("Mail", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String childName = document.getString("LinkChild");
                        appListToolbar.setTitle(childName + " Apps");

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
                                            obj.setPackageName(document.getString("PackageName"));

                                            String byteArrString = HomeActivity.iconSharedPreference.getString(obj.getAppName(),"NOTSAVED");
                                            if(byteArrString.equals("NOTSAVED")){
                                                //load image
                                                StorageReference imagePath = storageReference.child("Icon/" + obj.getAppName());

                                                Boolean available =  imagePath.getDownloadUrl().isSuccessful();
                                                if(available) {
                                                    imagePath.getBytes(ONE_MEGABYTE).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("tag", e.toString());
                                                        }
                                                    }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<byte[]> task) {
                                                            obj.setAppIcon(byteArrayToDrawable(task.getResult(), obj.getAppName()));
                                                            HomeActivity.iconEditor.putString(obj.getAppName(), Base64.encodeToString(task.getResult(), android.util.Base64.DEFAULT)).commit();
                                                        }
                                                    });
                                                }
                                            }else{
                                               appIconDecoded = Base64.decode(byteArrString,Base64.DEFAULT);
                                               obj.setAppIcon(byteArrayToDrawable(appIconDecoded,obj.getAppName()));
                                            }
                                            updateLockStatus(obj.getAppName());
                                            arrayList.add(obj);
                                        }
                                       setAppListAdapter(childName);
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

    void setAppListAdapter(String childName) {
  try {
      parentAppListAdapter = new ParentAppListAdapter(getContext(), arrayList, childName);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      recyclerView.setAdapter(parentAppListAdapter);
  }catch (Exception e){
      Log.e("tag",e.toString());
  }
    }

    Drawable byteArrayToDrawable(byte[] bar,String appName) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bar);
        Drawable drawable = Drawable.createFromStream(byteArrayInputStream, appName);
        return drawable;
    }
    private void filterAppList(String newText) {
        try {

            ArrayList<ParentAppListModel> filteredList = new ArrayList<>();
            for (ParentAppListModel i : arrayList) {
                if (i.getAppName().toLowerCase().trim().contains(newText.toLowerCase().trim())) {
                    filteredList.add(i);
                    parentAppListAdapter.setFilteredList(filteredList);
                }
            }
        }
        catch(Exception e)
        {
            Log.e("tag",e.toString());
        }
    }

    void updateLockStatus(String appName){
        db.collection("Relation").whereEqualTo("Mail", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String childName = document.getString("LinkChild");

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("LockStatus",HomeActivity.lockSharedPreference.getBoolean(appName,false));
                        Log.e("tag",appName + " is app name");
                        if(childName!=null && appName!=null){
                        db.collection(childName).document(appName).update(hashMap);
                        }else{
                            Log.e("tag","child name is null");
                        }
                       }
                    }
                    catch (Exception e)
                    {
                        Log.e("tag",e.toString());
                    }

                }
            }
        });
    }
    void setAppListToolbar(){
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(appListToolbar);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_parent,menu);

        MenuItem menuItem = menu.findItem(R.id.searchViewOption);
        searchView = (android.widget.SearchView) menuItem.getActionView();

        searchView.setQueryHint("Search App Here....");
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

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.setPatternOption){
            Intent intent = new Intent(getContext(), SetPatternActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}