# Call Monitor

**Call Monitor** is an Android application that runs an HTTP server in the background within a foreground service. The application functions as an HTTP server, exposing data through three REST API endpoints: **root**, **status**, and **log**.

---

## Overview

Call Monitor is designed with Clean Architecture principles. The application is divided into multiple layers:

- **Domain Layer**: Contains interfaces and use cases for handling call logs, call status, and server status.
- **Data Layer**: Implements data retrieval from the system CallLog and local contacts via the ContentResolver. It also uses a Room database to persist call log entries with additional fields such as a query counter (`timesQueried`) and a flag indicating whether the call is new relative to the last cold app launch.
- **Network Layer**: Implements the HTTP server using [Ktor](https://ktor.io). Ktor is provided in the network layer and injected as a dependency.
- **Presentation/UI Layer**: Displays the server address, a list of call logs, and provides buttons to start and stop the HTTP server.
- **Background Processing**: A WorkManager job is used to synchronize data. It marks call logs as read (deselects calls that should not be returned by the log endpoint, i.e., those that were registered at app-launch), verifies whether the HTTP server is running in the background if it was started, and refreshes the list of calls from the local data source.

---

## HTTP API Endpoints

### Root Endpoint

- **URL**: `/`
- **Description**: Returns server status information, including the server start time and a list of available services.
- **Sample Response**:

  ```json
  {
    "start": "2025-02-21T18:58:09.771Z",
    "services": [
      {
        "name": "status",
        "uri": "http://192.168.0.158:8080/status"
      },
      {
        "name": "log",
        "uri": "http://192.168.0.158:8080/log"
      }
    ]
  }
  ```

### Log Endpoint

- **URL**: `/log`
- **Description**: Returns a list of call logs that have occurred since the last app launch (cold start).
- **Sample Response**:

  ```json
  [
    {
      "beginning": "2025-02-21T19:01:35.203Z",
      "duration": "0",
      "number": "+48602641358",
      "name": "John Doe",
      "timesQueried": 0
    }
  ]
  ```

### Status Endpoint

- **URL**: `/status`
- **Description**: Returns information about the current phone call status.
- **Sample Response**:

  ```json
  {
    "ongoing": false,
    "phoneNumber": "+48602641358",
    "callerName": "John Doe"
  }
  ```

> **Note on Call Status**:
> - For Android API ≤ 30, the app uses `PhoneStateListener` to capture call state changes along with the phone number, which allows retrieving the contact name from the local contacts list.
> - For Android API ≥ 31, the app uses `TelephonyCallback`, which provides only the call state without the phone number, limiting the available data.
> - If you have 2 SIM cards installed, make sure the card you are testing is set as default.
---

## User Interface

The UI displays:
- **Server Address**: The IP address and port where the HTTP server is running.
- **Call Log List**: A list of call logs retrieved from the local database, filtered to show only those calls that have occurred since the last cold app launch.
- **Control Buttons**: Buttons to start and stop the HTTP server.

---

## Permissions

For proper functionality, the following runtime permissions must be granted:

```xml
<uses-permission android:name="android.permission.READ_CALL_LOG" />
<uses-permission 
    android:name="android.permission.READ_CONTACTS"
    android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

> **Important**:
> - The `READ_CONTACTS` permission is required only up to API 30.
> - It is highly recommended to disable battery optimization for this application, as high battery drain may cause the foreground service (running the HTTP server) to be terminated by the system.

---

## Architecture and Dependency Injection

The application uses Hilt for dependency injection. Key components include:

- **HttpServiceManager**: An interface with methods to start and stop the HTTP server.
- **CallLogRepository & Local DataSource**: Responsible for retrieving system call logs and observing call status. The repository also manages additional data (e.g., query counters and the `isNew` flag) stored in a Room database.
- **WorkManager**: Synchronizes data by marking call logs as read (or "old") upon app launch (cold start), verifying if the HTTP server is running in the background, and refreshing the list of calls from the local data source.
- **Ktor HTTP Server**: Provided in the network layer and injected into the foreground service.

---

## How to Build

1. **Clone the Repository.**
2. **Grant Permissions**: When running the application, ensure that all required runtime permissions are granted.
3. **Disable Battery Optimization**: For optimal performance, disable battery optimization for the app on your device.
4. **Run the Application**: The HTTP server will start automatically within a foreground service.

---

## License

This project is licensed under the Apache License 2.0.

---

## Acknowledgments

- [Ktor Framework](https://ktor.io)
- [Android Room Database](https://developer.android.com/jetpack/androidx/releases/room)
- [DataStore with Protocol Buffers](https://developer.android.com/topic/libraries/architecture/datastore)
- [Hilt for Dependency Injection](https://dagger.dev/hilt/)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)

---

