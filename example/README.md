# icapps_notifications_example

Demonstrates how to use the icapps_notificationsplugin.

```
import 'package:flutter/material.dart';
import 'package:icapps_notifications/icapps_notifications.dart';

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final _firebaseMessaging = FirebaseMessaging();

  String _content = "";
  String _token = "";

  @override
  void initState() {
    super.initState();
    _firebaseMessaging.configure(
      onMessage: (Map<String, dynamic> message) async {
        setState(() {
          _content = message.toString();
          print(_content);
        });
      },
      dismissIOSBadgeOnStartup: true,
    );
    _firebaseMessaging.requestNotificationPermissions(const IosNotificationSettings(sound: true, badge: true, alert: true));
    _firebaseMessaging.onIosSettingsRegistered.listen((IosNotificationSettings settings) {
      print("Settings registered: $settings");
      _getToken();
    });
    _getToken();
  }

  Future<void> _getToken() async {
    final token = await _firebaseMessaging.getToken();
    assert(token != null);
    setState(() {
      _token = "Push Messaging token: $token";
      print(token);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Example icapps Push Notifications'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Text(_token),
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 8),
              child: Container(
                height: 1,
                color: Colors.grey,
              ),
            ),
            Text(_content),
          ],
        ),
      ),
    );
  }
}
````