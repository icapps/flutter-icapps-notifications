# Flutter icapps Notifications

This plugin will be used to handle all cases for push notifications by the icapps standard.

THIS PACKAGE HAS NO SUPPORT FOR THE ICAPPS IPNS

## What is supported?

|                             | App in Foreground | App in Background | App Terminated |
| --------------------------: | ----------------- | ----------------- | -------------- |
| **Notification on Android** | `onMessage` | Notification is delivered to system tray. | Notification is delivered to system tray.
| **Notification on iOS** | `onMessage` | Notification is delivered to system tray. | Notification is delivered to system tray. |
| **Data Message on Android** | `onMessage` | tilte, body is supported to show a notification. Otherwise you have to create a new android service to handle this case | tilte, body is supported to show a notification. Otherwise you have to create a new android service to handle this case |
| **Data Message on iOS**     | `onMessage` | Message is stored by FCM and delivered to app via `onMessage` when the app is brought back to foreground. | Message is stored by FCM and delivered to app via `onMessage` when the app is brought back to foreground. |

## What should you use

### iOS

NOTIFICATION should be used for everything

### Android

DATA should be used to handle everything

## Get started

https://medium.com/flutterpub/enabling-firebase-cloud-messaging-push-notifications-with-flutter-39b08f2ed723

Warning -> DO NOT SET `FirebaseAppDelegateProxyEnabled` to `NO`

## Dart API

### Configure

`configure(onMessage, dismissIOSBadgeOnStartup = true)`

### Token

`getToken()`

### Delete Instance ID

deletes your token from fcm.

`deleteInstanceID()`

### requestNotificationPermissions (iOS)

request permission for push notifications.

`requestNotificationPermissions()`

### setBadge (iOS)

set your badge whenever you want on iOS

`setBadge(amount)`

## Example project

If you want to run this example project you will need to add the google-services.json and plist to your android and ios project

## Questions?

contact koen.vanlooveren@icapps.com