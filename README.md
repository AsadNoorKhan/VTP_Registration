# Registration App

A modern Android application built with Jetpack Compose for user registration and management, developed by Vals Group.

## 🚀 Features

- **Modern UI**: Built with Jetpack Compose and Material 3 design
- **User Registration**: Complete user registration flow
- **API Integration**: RESTful API integration using Retrofit
- **Network Handling**: Robust network state management
- **Clean Architecture**: Well-structured codebase following Android best practices

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with ViewModel
- **Networking**: Retrofit + OkHttp
- **JSON Parsing**: Gson
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)
- **Java Version**: 11

## 📱 Screenshots

*Screenshots will be added here*

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/valsgroup/registration/
│   │   ├── api/           # API service and models
│   │   ├── ui/            # Compose UI components
│   │   ├── viewmodel/     # ViewModels
│   │   └── MainActivity.kt
│   ├── res/               # Resources (layouts, strings, etc.)
│   └── AndroidManifest.xml
├── build.gradle.kts        # App-level build configuration
└── proguard-rules.pro     # ProGuard rules
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK API 35
- Java 11 or later
- Kotlin 1.9.0 or later

### Installation

1. **Clone the repository**
   ```bash
   git clone <your-repository-url>
   cd Registration
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory and select it

3. **Sync Gradle**
   - Wait for Gradle sync to complete
   - Resolve any dependency issues if they arise

4. **Configure the app**
   - Copy `app/src/main/assets/config.properties.template` to `app/src/main/assets/config.properties`
   - Update the configuration values with your actual API endpoints and settings

5. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift+F10

### Build Configuration

The project uses Gradle with Kotlin DSL. Key configurations:

- **Compile SDK**: 35
- **Min SDK**: 24
- **Target SDK**: 35
- **Version Code**: 1
- **Version Name**: "1.0"

## 🔧 Dependencies

### Core Dependencies
- `androidx.core:core-ktx` - Kotlin extensions for Android
- `androidx.appcompat:appcompat` - App compatibility library
- `com.google.android.material:material` - Material Design components

### Jetpack Compose
- `androidx.compose:compose-bom:2024.01.00` - Compose BOM
- `androidx.compose.ui:ui` - Compose UI core
- `androidx.compose.material3:material3` - Material 3 design
- `androidx.activity:activity-compose` - Compose activity integration

### Networking
- `com.squareup.retrofit2:retrofit:2.9.0` - HTTP client
- `com.squareup.retrofit2:converter-gson:2.9.0` - JSON converter
- `com.squareup.okhttp3:logging-interceptor:4.11.0` - HTTP logging

## 📡 API Integration

The app integrates with a REST API for user management:

- **Base URL**: Configured in the API service
- **Endpoints**: User status, registration, and authentication
- **Authentication**: Token-based authentication
- **Error Handling**: Comprehensive error handling and user feedback

## ⚙️ Configuration Management

The app uses a centralized configuration system for secure management of sensitive data:

### **Configuration Files**
- `app/src/main/assets/config.properties` - Contains actual configuration values (not tracked in Git)
- `app/src/main/assets/config.properties.template` - Template file for developers to copy and customize

### **Configuration Object**
- `Config.kt` - Singleton object that reads configuration values and provides them throughout the app
- Supports fallback to default values if configuration file is missing
- Automatically initialized in MainActivity

### **Security Features**
- Configuration file is excluded from Git tracking via `.gitignore`
- API URLs and endpoints are stored as configurable variables
- Easy to update configuration without code changes
- Supports different environments (dev, staging, production)

### **Usage Example**
```kotlin
// Get API base URL
val baseUrl = Config.apiBaseUrl

// Get full API URL with endpoint
val fullUrl = Config.getFullApiUrl(Config.endpointUserStatus)

// Get network timeout
val timeout = Config.networkTimeout
```

## 🔐 Permissions

The app requires the following permissions:
- `INTERNET` - For API communication
- `ACCESS_NETWORK_STATE` - For network state monitoring

## 🧪 Testing

- **Unit Tests**: JUnit for business logic testing
- **Instrumented Tests**: Espresso for UI testing
- **Test Runner**: `androidx.test.runner.AndroidJUnitRunner`

## 📦 Build Variants

- **Debug**: Development build with debugging enabled
- **Release**: Production build with ProGuard optimization

## 🚀 Deployment

### Release Build

1. **Generate signed APK/AAB**
   ```bash
   ./gradlew assembleRelease
   ```

2. **ProGuard Optimization**
   - Release builds use ProGuard for code optimization
   - Custom rules defined in `proguard-rules.pro`

### Play Store Deployment

1. Generate signed AAB file
2. Test thoroughly on multiple devices
3. Upload to Google Play Console
4. Complete store listing and rollout

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

<!-- ## 📄 License

This project is proprietary software developed by Vals Group. -->

<!-- ## 👥 Team

- **Vals Group** - Development Team
- **Contact**: [Your contact information]

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation -->

## 🔄 Changelog

### Version 1.0.0
- Initial release
- User registration functionality
- Jetpack Compose UI
- API integration
- Basic error handling

---


