package BottomNavigation.ParentNavigation.ParentListAdapter;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class GeofenceService extends Service {

    NotificationManager notificationManager;
    NotificationChannel notificationChannel,enteredHomeNotificationChannel;
    NotificationCompat.Builder notificationBuilder;
    FirebaseDatabase firebaseDatabase;
    Boolean isInSchool,isInHome;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    static SharedPreferences sharedPreferences;
    Location currentLocation;
    public static final String GeoFence_Shared_Pref_Id ="GEOFENCE_DATA";
    public static final String Home_Notifiaction_Id ="HOME_NOTIFICATION";
    public static final int Geo_Notifiaction_Id_INT = 801;
    public static final String School_Notifiaction_Id ="SCHOOL_NOTIFICATION";
    String childTemp,childName;
    double latitude,longitude;

    static double a,al,b,bl,c,cl,d,dl,as,asl,bs,bsl,cs,csl,ds,dsl;

    public static final String ENTRED_HOME_ID = "ENTRED_HOME";
    public static final String EXITED_HOME_ID = "EXITED_HOME";
    public static final String ENTRED_SCHOOL_ID = "ENTRED_SCHOOL";
    public static final String EXITED_SCHOOL_ID = "EXITED_SCHOOL";
    public static final String GEOFENCE_NOTIFICATION_ID = "GEOFENCE_NOTIFICATION";
    public static final int GEOFENCE_NOTIFICATION_ID_INT = 5002;
    public static final String FOREGROUND_SERVICE_ID = "FOREGROUND_SERVICE_ID";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        init();
        setGeofenceCoordinate();
        showNotification();
        getChildName();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void init(){
        notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(GEOFENCE_NOTIFICATION_ID,"Geofence Service Is Running",NotificationManager.IMPORTANCE_HIGH);
            notificationBuilder = new NotificationCompat.Builder(this,FOREGROUND_SERVICE_ID);

            setHomeEntredAudio();
            setHomeExitedAudio();
            setSchoolEnteredAudio();
            setSchoolExitedAudio();
        }
        firebaseDatabase = FirebaseDatabase.getInstance("https://parent-control-eb1f8-default-rtdb.asia-southeast1.firebasedatabase.app/");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = this.getSharedPreferences(GeoFence_Shared_Pref_Id, Context.MODE_PRIVATE);
        currentLocation = new Location("");
        isInHome = false;
        isInSchool = false;

    }

    private void setHomeEntredAudio() {
        //setting sound to child entred home notification
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enteredHomeNotificationChannel = new NotificationChannel(ENTRED_HOME_ID,"Child Entred Home",NotificationManager.IMPORTANCE_DEFAULT);
            Uri homeEntredUri = Uri.parse("android.resource://"
                    + this.getPackageName() + "/" + R.raw.entered_home);

            enteredHomeNotificationChannel.setSound(homeEntredUri,audioAttributes);
            notificationManager.createNotificationChannel(enteredHomeNotificationChannel);
        }

    }
    private void setHomeExitedAudio() {
        //setting sound to child exited home notification
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           NotificationChannel  exitedHomeNotificationChannel = new NotificationChannel(EXITED_HOME_ID,"Child Exited Home",NotificationManager.IMPORTANCE_DEFAULT);
            Uri homeEntredUri = Uri.parse("android.resource://"
                    + this.getPackageName() + "/" + R.raw.exited_home);

            exitedHomeNotificationChannel.setSound(homeEntredUri,audioAttributes);
            notificationManager.createNotificationChannel(exitedHomeNotificationChannel);
        }

    }
    private void setSchoolEnteredAudio() {
        //setting sound to child entred School notification
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel enteredSchoolNotificationChannel = new NotificationChannel(ENTRED_SCHOOL_ID,"Child Exited Home",NotificationManager.IMPORTANCE_DEFAULT);
            Uri homeEntredUri = Uri.parse("android.resource://"
                    + this.getPackageName() + "/" + R.raw.entered_school);

            enteredSchoolNotificationChannel.setSound(homeEntredUri,audioAttributes);
            notificationManager.createNotificationChannel(enteredSchoolNotificationChannel);
        }

    }

    private void setSchoolExitedAudio() {
        //setting sound to child entred School notification
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel exitedSchoolNotificationChannel = new NotificationChannel(ENTRED_SCHOOL_ID,"Child Exited Home",NotificationManager.IMPORTANCE_DEFAULT);
            Uri homeEntredUri = Uri.parse("android.resource://"
                    + this.getPackageName() + "/" + R.raw.entered_school);

            exitedSchoolNotificationChannel.setSound(homeEntredUri,audioAttributes);
            notificationManager.createNotificationChannel(exitedSchoolNotificationChannel);
        }

    }

    void showNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationBuilder.setContentText("Geofence Service").setContentTitle("Parent Control App Is Running").setSmallIcon(R.drawable.parent_control_app_icon).setChannelId(GEOFENCE_NOTIFICATION_ID);
        startForeground(GEOFENCE_NOTIFICATION_ID_INT,notificationBuilder.build());
    }
    void checkGeofence(){
          firebaseDatabase.getReference(childName).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  String locationStr = (String) snapshot.getValue();
                  Log.e("6969","Location is fetched from database : "+locationStr);
                  String latStr = locationStr.substring(0,locationStr.indexOf('a')).trim();
                  String lonStr = locationStr.substring(locationStr.indexOf('d')+1).trim();
                  latitude = Double.parseDouble(latStr);
                  longitude = Double.parseDouble(lonStr);
                  currentLocation.setLatitude(latitude);
                  currentLocation.setLongitude(longitude);

                  //check for Home
                  if(a!=0){
                      if(currentLocation.getLatitude()<a && currentLocation.getLatitude()>c && currentLocation.getLongitude() > al && currentLocation.getLongitude() < bl){
                          if(!isInHome) {
                              Log.e("loc69", "Child is Entered home");
                              isInHome = true;
                              showHomeNotification();
                          }
                      }else{
                          if(isInHome){
                              Log.e("loc69", "Child is Exited home");
                              isInHome =false;
                              showHomeNotification();
                          }
                      }
                  }
                  if(as!=0){
                      if(currentLocation.getLatitude()<as && currentLocation.getLatitude()>cs && currentLocation.getLongitude() > asl && currentLocation.getLongitude() < bsl){
                          if(!isInSchool) {
                              Log.e("loc69", "Child is Entered school");
                              isInSchool = true;
                              showSchoolNotification();
                          }
                      }else{
                          if(isInSchool){
                              Log.e("loc69", "Child is Exited school");
                              isInSchool = false;
                              showSchoolNotification();
                          }
                      }
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
    }
    void getChildName() {
        db.collection("Relation").whereEqualTo("Mail", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        childTemp = document.getString("LinkChild");
                    }
                    int dotIndex = childTemp.indexOf('.');
                    childName = childTemp.substring(0, dotIndex);
                    Log.e("6969", "Geofence Forground Child Name = " + childName);
                    checkGeofence();
                }else{
                    Log.e("6969", "Fetching Child Name Was Unsuccessful");
                }
            }
        });
    }
    public static void setGeofenceCoordinate(){
        a = sharedPreferences.getFloat("HomeALat",0);
        al = sharedPreferences.getFloat("HomeALong",0);
        if(a!=0){
            b = sharedPreferences.getFloat("HomeBLat",0);
            bl = sharedPreferences.getFloat("HomeBLong",0);
            c = sharedPreferences.getFloat("HomeCLat",0);
            cl = sharedPreferences.getFloat("HomeCLong",0);
            d = sharedPreferences.getFloat("HomeDLat",0);
            dl = sharedPreferences.getFloat("HomeDLong",0);
        }
        as = sharedPreferences.getFloat("SchoolALat",0);
        asl = sharedPreferences.getFloat("SchoolALong",0);
        if(as!=0){
            bs = sharedPreferences.getFloat("SchoolBLat",0);
            bsl = sharedPreferences.getFloat("SchoolBLong",0);
            cs = sharedPreferences.getFloat("SchoolCLat",0);
            csl = sharedPreferences.getFloat("SchoolCLong",0);
            ds = sharedPreferences.getFloat("SchoolDLat",0);
            dsl = sharedPreferences.getFloat("SchoolDLong",0);
        }
    }
    void showHomeNotification(){
        if(isInHome) {
            Notification homeNotification = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                homeNotification = new NotificationCompat.Builder(this, ENTRED_HOME_ID)
                        .setContentTitle("Reached Home").setContentText("Child Entered Home Area").setSmallIcon(R.drawable.baseline_location_on_24).setChannelId(ENTRED_HOME_ID).build();
            }
            notificationManager.notify(Geo_Notifiaction_Id_INT,homeNotification);
        }else{
            Notification homeNotification = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                homeNotification = new NotificationCompat.Builder(this, EXITED_HOME_ID)
                        .setContentTitle("Left Home").setContentText("Child Exited Home Area").setSmallIcon(R.drawable.baseline_location_on_24).setChannelId(EXITED_HOME_ID).build();
            }
            notificationManager.notify(Geo_Notifiaction_Id_INT,homeNotification);
        }
    }
    void showSchoolNotification(){
        if(isInSchool) {
            Notification schoolNotification = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                schoolNotification = new NotificationCompat.Builder(this, ENTRED_SCHOOL_ID)
                        .setContentTitle("Reached School").setContentText("Child Entered School Area").setSmallIcon(R.drawable.baseline_location_on_24).setChannelId(ENTRED_SCHOOL_ID).build();
            }
            notificationManager.notify(Geo_Notifiaction_Id_INT,schoolNotification);
        }else{
            Notification schoolNotification = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                schoolNotification = new NotificationCompat.Builder(this, EXITED_SCHOOL_ID)
                        .setContentTitle("Left School").setContentText("Child Exited School Area").setSmallIcon(R.drawable.baseline_location_on_24).setChannelId(EXITED_SCHOOL_ID).build();
            }
            notificationManager.notify(Geo_Notifiaction_Id_INT,schoolNotification);
        }
    }
}
