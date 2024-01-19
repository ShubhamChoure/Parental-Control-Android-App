package BottomNavigation.ChildeNavigation;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import Locks.PopUpPatternLock;

public class LocationUploadService extends Service {


    final int FOREGROUND_ID = 1001;
    final int REQUEST_EXCEPTION = 1003;
    final String NOTIFICATION_CHANNEL_ID = "Upload Location Notification";

    String appName;
    public static SharedPreferences childlockSharedPreference;
    public static final String PREF_LOCK = "ChildLockStatus";
    Handler handler, locationHandler;
    NotificationChannel notificationChannel;

    NotificationManager notificationManager;
    LocationSettingsRequest locationSettingsRequest;
    LocationCallback locationCallback;
    Notification.Builder notificationBuilder;
    PackageManager packageManager;
    static String tempCurrentApp = "Not Null";
    ApplicationInfo applicationInfo;
    FirebaseDatabase firebaseDatabase;
    LocationRequest locationRequest;

    SettingsClient settingsClient;
    DatabaseReference databaseReference;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        init(intent);
        showNotification();
        uploadLocation();

        handler = new Handler();
        locationHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {

                detectApp(LocationUploadService.this);
                //Log.e("6969","Foreground Service Is Running");
                handler.postDelayed(this, 2000);
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void init(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Live Location Update", NotificationManager.IMPORTANCE_HIGH);
            notificationBuilder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID);
        }
        notificationManager = getSystemService(NotificationManager.class);
        childlockSharedPreference = getSharedPreferences(PREF_LOCK, Context.MODE_PRIVATE);

        firebaseDatabase = FirebaseDatabase.getInstance("https://parent-control-eb1f8-default-rtdb.asia-southeast1.firebasedatabase.app/");
        String childNameTemp = intent.getStringExtra("user");
        int indexOfDot = childNameTemp.indexOf('.');
        databaseReference = firebaseDatabase.getReference(childNameTemp.substring(0, indexOfDot));
    }

    void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationBuilder.setContentText("Live Location Uploading Is Happening")
                .setContentTitle("Parental Control App");
        startForeground(FOREGROUND_ID, notificationBuilder.build());
    }

    void detectApp(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);
            long currentTime = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 1000 * 10, currentTime);

            // Sort the stats by the last time they were used
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    String topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    // Now topPackageName is the package name of the app that was launched most recently
                    if (!tempCurrentApp.equals(topPackageName)) {
                        tempCurrentApp = topPackageName;

                        packageManager = context.getPackageManager();
                        try {
                            applicationInfo = packageManager.getApplicationInfo(topPackageName, PackageManager.GET_META_DATA);
                            appName = (String) applicationInfo.loadLabel(packageManager);
                            Log.e("tagAppOpen", appName + " is launched");
                            if (childlockSharedPreference.getBoolean(appName, false)) {
                                Log.e("tagAppOpen", appName + " is locked");
                                Intent intent = new Intent(context, PopUpPatternLock.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        } catch (Exception e) {
                            Log.e("tagAppOpen", e.toString());
                        }
                    }
                }
            }
        }
    }


    void uploadLocation() {
        //Initialization
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocationUploadService.this);
        locationRequest = new LocationRequest.Builder(5000).setGranularity(Granularity.GRANULARITY_FINE).setPriority(Priority.PRIORITY_HIGH_ACCURACY).setMinUpdateDistanceMeters(100).build();
        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        settingsClient = LocationServices.getSettingsClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                for(Location location : locationResult.getLocations()) {
                    Log.e("6969", location.getLatitude() + " and " + location.getLongitude());
                    databaseReference.setValue(location.getLatitude() + " and " + location.getLongitude());
                    Log.e("6969", "Location  = " + locationResult);
                }
            }
        };

        settingsClient.checkLocationSettings(locationSettingsRequest).addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                if (task.isSuccessful()) {
                    if (ActivityCompat.checkSelfPermission(LocationUploadService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationUploadService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,Looper.myLooper());
                }else{
                    if(task.getException() instanceof ResolvableApiException){
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) task.getException();
                            resolvableApiException.startResolutionForResult((Activity) getApplicationContext(), REQUEST_EXCEPTION);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

}
