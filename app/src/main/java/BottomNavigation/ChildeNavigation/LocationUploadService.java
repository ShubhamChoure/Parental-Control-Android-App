package BottomNavigation.ChildeNavigation;

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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import Locks.PopUpPatternLock;

public class LocationUploadService extends Service {


    final int FOREGROUND_ID = 1001;
    final String NOTIFICATION_CHANNEL_ID = "Upload Location Notification";

    String appName;
    public static SharedPreferences childlockSharedPreference;
    public static final String PREF_LOCK = "ChildLockStatus";
    Handler handler,locationHandler;
    NotificationChannel notificationChannel;

    NotificationManager notificationManager;

    Notification.Builder notificationBuilder;
    PackageManager packageManager;
    static String tempCurrentApp = "Not Null";
    ApplicationInfo applicationInfo;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        init();
        showNotification();
        handler = new Handler();
        locationHandler = new Handler();
          new Thread(new Runnable() {
            @Override
            public void run() {

               detectApp(LocationUploadService.this);
               //Log.e("6969","Foreground Service Is Running");
                handler.postDelayed(this,2000);
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void init()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Live Location Update", NotificationManager.IMPORTANCE_HIGH);
            notificationBuilder = new Notification.Builder(this,NOTIFICATION_CHANNEL_ID);
        }
        notificationManager = getSystemService(NotificationManager.class);
        childlockSharedPreference = getSharedPreferences(PREF_LOCK, Context.MODE_PRIVATE);
    }
    void showNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationBuilder.setContentText("Live Location Uploading Is Happening")
                .setContentTitle("Parental Control App");
        startForeground(FOREGROUND_ID,notificationBuilder.build());
    }
    void detectApp(Context context){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usageStatsManager = (UsageStatsManager)  context.getSystemService(USAGE_STATS_SERVICE);
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
                            if(childlockSharedPreference.getBoolean(appName,false)){
                                Log.e("tagAppOpen",appName + " is locked");
                                Intent intent = new Intent(context, PopUpPatternLock.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        } catch (Exception e){
                            Log.e("tagAppOpen",e.toString());
                        }
                    }
                }
            }
        }
    }
}
