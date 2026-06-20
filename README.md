# AI Vision 👁️✨

AI Vision is a premium Android application designed to analyze your environment in real-time. Built using Kotlin, **Jetpack Compose (Material 3)**, and **CameraX**, the app integrates **Google ML Kit** to provide fast and accurate on-device machine learning capabilities.

---

## 🚀 Key Features

*   **🔍 Object Identifier**: Detects and tracks objects in real-time, overlaying dynamic, scaled bounding boxes with real-time confidence scores.
*   **📝 Text Recognizer (OCR)**: Instantly scans, extracts, and lets you copy text directly from the camera stream to your clipboard using a beautiful glassmorphic overlay.
*   **🛡️ Premium Permission Onboarding**: Provides dynamic, responsive screens handling all camera permission states (Granted, Denied/Rationale, and Permanently Denied).
*   **🎨 Premium Dark Mode UI**: Modern Midnight-Violet gradient layouts with glassmorphic cards, custom badge controls, and smooth Material Design 3 elements.

---

## 🛠️ Tech Stack & Architecture

This project is built using modern Android development best practices and follows a clean, decoupled architecture.

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/compose) with Material Design 3
*   **Jetpack Navigation**: Compose Navigation for seamless screen transitions
*   **Dependency Injection**: [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for modular and clean dependency resolution
*   **Camera Integration**: [CameraX](https://developer.android.com/training/camerax) for robust camera lifecycle management and live image analysis
*   **AI & Machine Learning**: 
    *   **Google ML Kit Object Detection**: Live object identification
    *   **Google ML Kit Text Recognition**: Live OCR capabilities
    *   **Google ML Kit Image Labeling**: Real-time classification

---

## 📂 Project Structure

The project follows a clean structure grouping code by feature area and responsibility:

```
app/src/main/java/com/example/aiidentifier/
├── AIIdentifierApp.kt              # App controller initializing Dagger Hilt
├── MainActivity.kt                 # Single Activity entry point
├── CameraScreen.kt                 # Basic camera preview setup
│
├── domain/
│   └── DetectedObjectUi.kt         # Model representing a detected object in UI
│
├── presentation/
│   ├── CameraPreview.kt            # AndroidView wrapper mapping CameraX PreviewView
│   ├── CameraRoute.kt              # Handles permission checks and screen routing
│   ├── CameraXScr.kt               # Object Identifier UI & bounding box overlay
│   ├── DenyScreen.kt               # Permission explanation & rationale screens
│   ├── FrameProcessor.kt           # Interface for processing camera image frames
│   │
│   ├── camera/
│   │   ├── CameraUiState.kt        # State container for detected objects/text
│   │   ├── CameraViewModel.kt      # Orchestrates camera analysis results
│   │   ├── CameraAnalyzer.kt       # CameraX ImageAnalysis.Analyzer bridge
│   │   └── CoordinateScaleUtils.kt # Maps ML Kit coordinates to preview screen pixels
│   │
│   ├── ml/
│   │   ├── ObjectDetectionProcessor.kt # ML Kit Object Detection implementation
│   │   ├── ImageLabelingProcessor.kt   # Classifier implementation
│   │   └── OCRFrameProcessor.kt        # ML Kit Text Recognition handler
│   │
│   ├── ocr/
│   │   └── TextReaderScreen.kt     # Text Recognizer OCR screen & Clipboard Copy
│   │
│   ├── navigation/
│   │   ├── Screen.kt               # Navigation destinations config
│   │   └── MenuScreen.kt           # Main Dashboard Hub UI
│   │
│   └── permission/
│       ├── PermissionsState.kt     # Enumerated Camera Permission states
│       └── ViewModel.kt            # Business logic for checking permissions
│
└── ui/theme/                       # Premium midnight typography and color palette
```

---

## 📦 Requirements & Building

*   **Android Studio** Ladybug (2024.2.1) or newer.
*   **Minimum SDK**: `24` (Android 7.0 Nougat)
*   **Target SDK**: `36` (Android 15)
*   **JDK**: Version 17+

### Build from Command Line

To compile the application, navigate to the project directory and run:

```bash
# Build the Debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test
```

---

## 🚀 Getting Started

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/<your-username>/AIIdentifier.git
    cd AIIdentifier
    ```
2.  **Open in Android Studio**:
    *   Select **File -> Open** and choose the `AIIdentifier` root folder.
    *   Allow Gradle to sync and download all dependencies.
3.  **Run the Project**:
    *   Connect an Android device with camera capability or launch an emulator with a virtual camera.
    *   Click the **Run** button (`Shift + F10`) in Android Studio.
