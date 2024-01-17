package BottomNavigation.ChildeNavigation;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.USAGE_STATS_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jspm.MainActivity;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import Locks.PopUpPatternLock;

public class AppLockAlarmReciver extends BroadcastReceiver {

    static String tempCurrentApp = "Not Null";
    public static final int ALARM_REQ_CODE_IN_RECEIVER = 101;
    PackageManager packageManager;

    AlarmManager alarmManager;
    ApplicationInfo applicationInfo;
    String appName;
    public static SharedPreferences childlockSharedPreference;
    public static final String PREF_LOCK = "ChildLockStatus";
    @Override
    public void onReceive(Context context, Intent intent) {

        // Log.e("tagAppOpen", "Alarm Manager Receiver Started");
        init(context);
        detectApp(context);
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
       startAlarmManager(context);
    }
     void startAlarmManager(Context context){
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context,AppLockAlarmReciver.class);
        PendingIntent pe = PendingIntent.getBroadcast(context,ALARM_REQ_CODE_IN_RECEIVER,intent,PendingIntent.FLAG_MUTABLE);
        long alarmTime = System.currentTimeMillis() + 2 * 1000;
        alarmManager.set(AlarmManager.RTC,alarmTime,pe);
    }

    void init(Context context){
        childlockSharedPreference = context.getSharedPreferences(PREF_LOCK,Context.MODE_PRIVATE);
    }
}