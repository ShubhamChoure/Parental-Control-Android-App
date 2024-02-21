package HomeActivity.ChildeHomeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.jspm.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import BottomNavigation.ChildeNavigation.ChildAppLock;
import BottomNavigation.ChildeNavigation.ChildeAccount;
import BottomNavigation.ChildeNavigation.LocationUploadService;

public class ChildHomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    public static FirebaseAuth mAuth;
    public static FirebaseFirestore db;
    Button childFragmentLogoutBtn;
    Fragment childAppLock,childAccount;
    public static Context MyContext;

    public static SharedPreferences childlockSharedPreference;

    public static SharedPreferences.Editor childlockEditor;

    public static final String PREF_LOCK = "ChildLockStatus";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_home);
        init();
        startLocationUploadForegroundService();
        bottomNavigationView.setSelectedItemId(R.id.nav_lock);

        switchFragments(new ChildAppLock(),true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.nav_lock){
                   switchFragments(childAppLock,false);
                }else if(id==R.id.nav_account){
                    switchFragments(childAccount,false);
                }
                return true;
            }
        });
    }

    void switchFragments(Fragment fragment,Boolean start){
        if(start)
        {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.FrameLayout, fragment);
            transaction.commit();

        }
        else {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.FrameLayout, fragment);
            transaction.commit();
        }
    }
    void init(){
        bottomNavigationView = findViewById(R.id.BottomNavigationView);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        childAppLock = new ChildAppLock();
        childAccount = new ChildeAccount();
        MyContext = ChildHomeActivity.this;
        childlockSharedPreference = getSharedPreferences(PREF_LOCK,MODE_PRIVATE);
        childlockEditor = childlockSharedPreference.edit();
    }
    void startLocationUploadForegroundService(){
        Intent intent = new Intent(this, LocationUploadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String childNameTemp = mAuth.getCurrentUser().getEmail().toString();
            intent.putExtra("user",childNameTemp);
            startForegroundService(intent);
            Log.e("6969","Starting Foreground service");
        }
    }
    public void sucide(){
        finish();
    }
}