package com.jaaf;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.topjohnwu.superuser.Shell;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class PackagesModule extends ReactContextBaseJavaModule {
  PackagesModule(ReactApplicationContext context) {
    super(context);
  }

  @Override
  public String getName() {
    return "PackagesModule";
  }

  @ReactMethod
  public void listPackagesEvent(Callback cb) throws PackageManager.NameNotFoundException {
    final PackageManager pm = getReactApplicationContext().getPackageManager();
    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

    WritableArray app_list = new WritableNativeArray();
    Drawable defaultActivityIcon = pm.getDefaultActivityIcon();
    for (ApplicationInfo packageInfo : packages) {
      if(getReactApplicationContext().getPackageName().equals(packageInfo.packageName)) {
        continue;  // skip own app
      }
      // add only apps with application icon
      Intent intentOfStartActivity = pm.getLaunchIntentForPackage(packageInfo.packageName);
      if(intentOfStartActivity == null)
        continue;
      Drawable applicationIcon = pm.getActivityIcon(intentOfStartActivity);
      if(applicationIcon != null && !defaultActivityIcon.equals(applicationIcon)) {
        WritableMap info = new WritableNativeMap();
        info.putString("name", packageInfo.loadLabel(pm).toString());
        info.putString("packagename", packageInfo.packageName);
        app_list.pushMap(info);
      }
    }

    cb.invoke(app_list);
  }

  @ReactMethod
  public void getSuEvent() {
    Process p;
    try {
      // Preform su to get root privledges
      p = Runtime.getRuntime().exec("su");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Refactor these methods to togglePackageEvent()
  @ReactMethod
  public void disablePackageEvent(String  packageName) {
    String disableCommand = String.format("pm disable %s", packageName);
    Shell.su(disableCommand).submit();
  }
  @ReactMethod
  public void enablePackageEvent(String packageName) {
    String disableCommand = String.format("pm enable %s", packageName);
    Shell.su(disableCommand).submit();
  }




}

