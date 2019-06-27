package com.icapps.flutter.notifications.example;

import com.icapps.flutter.notifications.FCMPlugin;
import io.flutter.app.FlutterApplication;

/**
 * @author Koen Van Looveren
 */
public class MyApplication extends FlutterApplication {

  @Override public void onCreate () {

    super.onCreate();

    FCMPlugin.init(this, true);
  }
}
