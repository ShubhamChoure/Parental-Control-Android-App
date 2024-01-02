package BottomNavigation.ParentNavigation.ParentListAdapter;


import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

import HomeActivity.ParentHomeActivity.HomeActivity;

public class ParentAppListModel {
    String appName,packageName;
    Drawable AppIcon;
    Boolean lockStatus;
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setAppIcon(Drawable appIcon) {
        AppIcon = appIcon;
    }

    public ParentAppListModel() {
    }

    public Boolean getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(Boolean lockStatus) {
        this.lockStatus = lockStatus;
    }

    public ParentAppListModel(String appName, String packageName, Drawable appIcon, Boolean lockStatus) {
        this.appName = appName;
        this.packageName = packageName;
        this.AppIcon = appIcon;
        this.lockStatus = lockStatus;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getAppIcon() {
        return AppIcon;
    }



}


