package com.icapps.flutter.notifications.example;

import android.os.Bundle;

import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.app.FlutterActivity;

public class MainActivity extends FlutterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);
    }
}
