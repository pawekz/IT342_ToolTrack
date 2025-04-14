# ToolTrack Mobile App

This repository contains the Android mobile application for the ToolTrack system, built with Jetpack Compose.

## Project Overview

ToolTrack is a tool inventory management system that allows users to track tools using QR codes. The mobile app provides functionality for scanning QR codes, viewing tool information, and managing user profiles.

## Prerequisites

Before you begin, ensure you have the following installed:

- [Android Studio](https://developer.android.com/studio) (Latest stable version recommended)
- JDK 17 or newer
- Git

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/pawekz/IT342_ToolTrack.git
cd IT342_ToolTrack
```

### 2. Open the Project

Open Android Studio and select "Open an Existing Project". Navigate to the `frontend_mobile` directory within the cloned repository and open it.

### 3. Sync Gradle

Once the project is opened, Android Studio will automatically start syncing the Gradle files. If it doesn't, you can manually sync by clicking on the "Sync Project with Gradle Files" button in the toolbar.

### 4. Configure SDK

Ensure you have the correct SDK installed:
- Compile SDK: 35
- Target SDK: 34
- Min SDK: 29

You can check and modify these settings in Android Studio:
1. Go to File > Project Structure
2. Select "Modules" > "app"
3. Navigate to the "Properties" tab
4. Verify the SDK settings match the requirements

### 5. Install Dependencies

The project uses Gradle to manage dependencies. All required dependencies are specified in the `build.gradle.kts` files and will be downloaded automatically during the Gradle sync.

Key dependencies include:
- Jetpack Compose (UI framework)
- CameraX (Camera functionality)
- ML Kit (QR code scanning)
- Coil (Image loading)
- Accompanist (Compose utilities)

### 6. Run the Application

Connect an Android device or use an emulator to run the application:

1. Select a device from the dropdown menu in the toolbar
2. Click the "Run" button (green triangle) or press Shift+F10

## Project Structure

- `app/src/main/java/edu/cit/tooltrack/` - Main source code
  - `screens/` - UI screens
  - `navigation/` - Navigation components
  - `ui/` - UI components and theme
  - `viewmodels/` - ViewModels for state management
  - `data/` - Data models and repositories

## Features

- QR code scanning for tool identification
- Tool information display
- User profile management
- Dashboard for quick access to common functions

## Development Guidelines

### Coding Standards

- Follow Kotlin coding conventions
- Use Jetpack Compose best practices
- Implement MVVM architecture pattern

### Git Workflow

1. Create a feature branch from `main`
2. Make your changes
3. Submit a pull request to `main`
4. Request code review

## Troubleshooting

### Common Issues

1. **Gradle Sync Failed**
   - Check your internet connection
   - Verify Gradle version compatibility
   - Try invalidating caches (File > Invalidate Caches / Restart)

2. **Build Errors**
   - Ensure you have the correct SDK versions installed
   - Check for missing dependencies
   - Verify that all plugins are compatible

3. **Runtime Errors**
   - Check logcat for detailed error messages
   - Verify device compatibility (API level)