import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';
import 'package:platform/platform.dart';

typedef Future<dynamic> MessageHandler(Map<String, dynamic> message);

/// Implementation of the Firebase Cloud Messaging API for Flutter.
///
/// Your app should call [requestNotificationPermissions] first and then
/// register handlers for incoming messages with [configure].
class FirebaseMessaging {
  factory FirebaseMessaging() => _instance;

  @visibleForTesting
  FirebaseMessaging.private(MethodChannel channel, Platform platform)
      : _channel = channel,
        _platform = platform;

  static final FirebaseMessaging _instance = FirebaseMessaging.private(
      const MethodChannel('com.icapps.flutter/icapps_notifications'),
      const LocalPlatform());

  final MethodChannel _channel;
  final Platform _platform;

  MessageHandler _onMessage;

  /// On iOS, prompts the user for notification permissions the first time
  /// it is called.
  ///
  /// Does nothing on Android.
  void requestNotificationPermissions(
      [IosNotificationSettings iosSettings = const IosNotificationSettings()]) {
    if (_platform.isIOS) {
      _channel.invokeMethod<void>(
          'requestNotificationPermissions', iosSettings.toMap());
    }
  }

  final StreamController<IosNotificationSettings> _iosSettingsStreamController =
      StreamController<IosNotificationSettings>.broadcast();

  /// Stream that fires when the user changes their notification settings.
  ///
  /// Only fires on iOS.
  Stream<IosNotificationSettings> get onIosSettingsRegistered {
    return _iosSettingsStreamController.stream;
  }

  /// Sets up [MessageHandler] for incoming messages.
  void configure(
      {@required MessageHandler onMessage,
      bool dismissIOSBadgeOnStartup = true}) {
    _onMessage = onMessage;
    _channel.setMethodCallHandler(_handleMethod);
    _channel.invokeMethod<void>(
        'configure',
        ConfigureSettings(dismissIOSBadgeOnStartup: dismissIOSBadgeOnStartup)
            .toMap());
  }

  final StreamController<String> _tokenStreamController =
      StreamController<String>.broadcast();

  /// Fires when a new FCM token is generated.
  Stream<String> get onTokenRefresh {
    return _tokenStreamController.stream;
  }

  /// Returns the FCM token.
  Future<String> getToken() async {
    return await _channel.invokeMethod<String>('getToken');
  }

  /// Resets Instance ID and revokes all tokens. In iOS, it also unregisters from remote notifications.
  ///
  /// A new Instance ID is generated asynchronously if Firebase Cloud Messaging auto-init is enabled.
  ///
  /// returns true if the operations executed successfully and false if an error ocurred
  Future<bool> deleteInstanceID() async {
    return await _channel.invokeMethod<bool>('deleteInstanceID');
  }

  /// Set badge for iOS
  Future<bool> setBadge(int badgeCount) async {
    if (_platform.isIOS) {
      return await _channel.invokeMethod<bool>('setBadge', badgeCount);
    }
    return false;
  }

  Future<dynamic> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case "onToken":
        final String token = call.arguments;
        _tokenStreamController.add(token);
        return null;
      case "onIosSettingsRegistered":
        _iosSettingsStreamController.add(IosNotificationSettings._fromMap(
            call.arguments.cast<String, bool>()));
        return null;
      case "onMessage":
        return _onMessage(call.arguments.cast<String, dynamic>());
      default:
        throw UnsupportedError("Unrecognized JSON message");
    }
  }
}

class ConfigureSettings {
  final bool dismissIOSBadgeOnStartup;

  const ConfigureSettings({
    this.dismissIOSBadgeOnStartup = true,
  });

  ConfigureSettings._fromMap(Map<String, bool> settings)
      : dismissIOSBadgeOnStartup = settings['dismissIOSBadgeOnStartup'];

  @visibleForTesting
  Map<String, dynamic> toMap() {
    return <String, bool>{'dismissIOSBadgeOnStartup': dismissIOSBadgeOnStartup};
  }

  @override
  String toString() => 'ConfigureSettings ${toMap()}';
}

class IosNotificationSettings {
  const IosNotificationSettings({
    this.sound = true,
    this.alert = true,
    this.badge = true,
  });

  IosNotificationSettings._fromMap(Map<String, bool> settings)
      : sound = settings['sound'],
        alert = settings['alert'],
        badge = settings['badge'];

  final bool sound;
  final bool alert;
  final bool badge;

  @visibleForTesting
  Map<String, dynamic> toMap() {
    return <String, bool>{'sound': sound, 'alert': alert, 'badge': badge};
  }

  @override
  String toString() => 'PushNotificationSettings ${toMap()}';
}
