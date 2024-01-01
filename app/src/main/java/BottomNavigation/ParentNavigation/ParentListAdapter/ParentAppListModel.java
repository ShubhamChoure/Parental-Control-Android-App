package BottomNavigation.ParentNavigation.ParentListAdapter;


import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

public class ParentAppListModel {
    String appName,packageName;
    Drawable AppIcon;

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

    public ParentAppListModel(String appName, String packageName, Drawable appIcon) {
        this.appName = appName;
        this.packageName = packageName;
        AppIcon = appIcon;
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


