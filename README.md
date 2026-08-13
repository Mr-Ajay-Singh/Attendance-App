# Android Attendance App

A production-quality Android application providing privacy-first, on-device biometric attendance verification with real-time location capture, developed using Kotlin, Jetpack Compose, Material 3, and Clean Architecture.

---

## Technology Stack

* **Kotlin**: Core language
* **Jetpack Compose**: Modern declarative UI framework
* **Material 3**: System design components & styling
* **Clean Architecture**: Multi-layer separation (Presentation → Domain → Data)
* **MVVM**: StateFlow & ViewModel architecture
* **Hilt**: Dependency Injection
* **Room**: Local SQLite persistence for staff & attendance records
* **CameraX**: Camera preview & front-camera selfie capture
* **ML Kit Face Detection**: Real-time face detection & bounding box cropping
* **TensorFlow Lite / MobileFaceNet**: On-device 192-dimensional face embedding extraction & Cosine Similarity comparison
* **Fused Location Provider**: High-accuracy GPS location capture
* **Coil**: Image loading & thumbnail rendering

---

## Architecture

The project adheres strictly to **Feature-based Clean Architecture**:

```text
Presentation (Jetpack Compose, ViewModels, StateFlow)
      ↓
Domain (UseCases, Domain Models, Repository Interfaces)
      ↓
Data (Room Entities, DAOs, Repository Implementations, Mappers)
```

The Domain layer is completely decoupled from Android framework dependencies to ensure robust testability.

### Module Structure

```text
com.invictus.attendanceapp/
├── core/
│   ├── camera/           # CameraX preview & capture abstractions
│   ├── common/           # Domain error & result types (AppResult, AppError)
│   ├── database/         # Room Database & Type Converters
│   ├── face/             # ML Kit Face Detection, TFLite Embedding & Cosine Similarity
│   ├── image/            # Internal app private image storage manager
│   └── location/         # FusedLocationProviderClient wrapper
├── feature/
│   ├── auth/             # Login feature (Domain, Data, Presentation)
│   ├── staff/            # Staff directory, creation, enrollment & profile history
│   └── attendance/       # Mark attendance with face & location verification
├── di/                   # Hilt Dependency Injection modules
├── navigation/           # Navigation Compose routes & graph
├── AttendanceApp.kt      # Application class with initial data seeder
└── MainActivity.kt       # Activity container & Edge-To-Edge setup
```

---

## Key Features

1. **Dual Role Authentication**: Demo login with isolated Admin and Staff roles.
2. **Staff Management**: Admin interface to add staff members and search the employee directory.
3. **Front Camera Face Enrollment**: ML Kit single-face detection, TFLite feature embedding extraction, and private storage of enrollment selfies.
4. **On-Device Face Verification Attendance**: Staff mark attendance via front camera. Attendance is recorded **only** if the captured face matches the enrolled face vector via Cosine Similarity (`similarity >= 0.70`).
5. **GPS Location Capture**: Real-time latitude and longitude acquisition stored with each verified attendance record.
6. **Attendance History**: Admin profile view displaying staff details, enrollment photo, formatted timestamps (`13 Aug 2026 • 04:25 PM`), coordinates, and captured attendance selfies.

---

## Demo Credentials

| Role | Username | Password | Default Destination |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Staff Directory |
| **Staff** | `staff` | `staff123` | Mark Attendance |

---

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/Mission-Invictus/Attendance-App.git
   ```
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Sync project with Gradle files (`./gradlew assembleDebug`).
4. Run on a **physical Android device** (Android 8.0+ / API 26+).
   * *Note: A physical device is recommended for testing CameraX preview, front camera selfie capture, ML Kit face detection, and GPS location services.*
5. Log in as **Admin** (`admin` / `admin123`).
6. Click **Add Staff** to create a new employee and enroll their face using the front camera.
7. Log out and log in as **Staff** (`staff` / `staff123`) to mark attendance.
8. Log back in as **Admin** to inspect the attendance record with timestamp, coordinates, and selfie in the staff profile.

---

## Assumptions & Limitations

* **On-Device Biometrics**: All face detection, embedding extraction, and comparison operations run 100% locally on device. No biometric data or images are ever uploaded to external servers.
* **Authentication**: Uses local demo authentication without backend user management to focus on assignment requirements.
* **Liveness Detection**: Simple face detection is implemented for demonstration. A commercial production system should add anti-spoofing / 3D liveness detection (blink/smile checks).
* **Location Requirements**: GPS accuracy depends on location permission grants and device hardware sensor availability.

---

## Unit Testing

Run unit tests via command line:
```bash
./gradlew test
```

Includes unit tests for:
* `LoginUseCaseTest`: Valid vs invalid login handling.
* `AddStaffUseCaseTest`: Staff validation and duplicate employee ID rejection.
* `FaceMatcherTest`: Cosine similarity mathematical precision.
* `MarkAttendanceUseCaseTest`: Enforces that face mismatch **strictly prevents** attendance recording in the repository.
