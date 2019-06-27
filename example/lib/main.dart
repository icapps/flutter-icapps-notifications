import 'package:flutter/material.dart';
import 'package:icapps_notifications_example/screen/home_screen.dart';

void main() {
  runApp(
    MaterialApp(
      theme: ThemeData(primaryColor: Colors.blueGrey.shade900),
      home: HomeScreen(),
    ),
  );
}
