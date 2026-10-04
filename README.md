[Uploading README.md…]()
# PetSense -- Smart Care & Behavior Analyzer 🐾

PetSense is a native Android application designed to help pet owners
monitor their pet's daily health, behavior, and wellness in one place.

The application combines pet profiles, health tracking, behavior
observations, reminders, insights, and AI-assisted behavior analysis
into a simple and modern mobile experience.

> **Project Status:** Final-year project MVP\
> **Platform:** Android\
> **Development:** Java + XML

------------------------------------------------------------------------

## 📱 Project Overview

PetSense provides a centralized platform where users can:

-   Create and manage pet profiles
-   Monitor basic health and wellness information
-   Track daily activity, sleep, water, and nutrition
-   Maintain health records such as weight and symptoms
-   Record and review pet behavior
-   Analyze short pet videos using AI-assisted behavior analysis
-   View wellness insights
-   Manage preventive-care information and reminders

The application is designed to provide **observations and tracking**,
not medical diagnosis.

------------------------------------------------------------------------

## ✨ Key Features

### 🔐 User Authentication

-   User registration and login
-   Firebase Authentication
-   Persistent login session
-   Secure user-specific application flow

### 🐶 Pet Profile

Users can create and manage their pet profile with information such as:

-   Pet name
-   Breed
-   Age
-   Gender
-   Weight
-   Profile picture
-   Basic pet description

The selected pet information is used across the application.

### 🏠 Smart Dashboard

The dashboard provides a quick overview of the selected pet.

It includes:

-   Pet profile summary
-   Wellness/health score
-   Activity summary
-   Sleep information
-   Water tracking
-   Nutrition information
-   Preventive-care reminder
-   Quick access to health information

The dashboard follows a minimal and premium pet-health design with a
focus on readability and low visual clutter.

### ❤️ Health Tracking

PetSense supports basic health and wellness tracking, including:

-   Weight
-   Activity
-   Sleep
-   Water intake
-   Nutrition
-   Symptoms
-   Health records

Historical information can be used to understand changes in the pet's
routine and wellness.

### 🐕 Behavior Analysis

The Behavior module allows users to record and analyze pet behavior.

The planned MVP behavior categories include:

-   Resting / lying down
-   Walking / moving
-   Running
-   Eating / drinking
-   Unclear / insufficient evidence

Behavior analysis is based on a short sequence of video frames rather
than a single image.

### 🤖 AI-Assisted Behavior Analysis

PetSense includes an AI-assisted behavior analysis concept.

The intended flow is:

``` text
Camera / Video
      ↓
Short Video Clip
      ↓
Frame Sampling
      ↓
Dog Detection
      ↓
Behavior Analysis
      ↓
Behavior Observation
      ↓
Save Observation
```

The system is designed to describe **visible behavior** rather than
diagnose medical conditions.

For example:

> "The pet appears to be walking and actively moving."

The system should not make unsupported claims such as diagnosing
anxiety, pain, illness, or other medical conditions.

### 📷 Camera / Video Demonstration

The behavior-analysis prototype can work with:

-   Live webcam input
-   Pre-recorded dog videos

Pre-recorded videos allow repeatable testing and demonstration when a
physical dog is not available.

### 💡 Insights

The Insights section is intended to summarize recorded health and
behavior information and present useful patterns in an
easy-to-understand format.

### 🔔 Preventive Care & Reminders

The application can provide reminders for upcoming preventive-care
activities and important pet-care events.

------------------------------------------------------------------------

## 🛠️ Technology Stack

### Android

-   **Java**
-   **XML**
-   **Android Studio**
-   **Material Components**
-   **View Binding**
-   **MVVM-style architecture**

### Backend / Cloud

-   **Firebase Authentication**
-   **Firebase Realtime Database**

### AI / Behavior Prototype

-   **Python**
-   **OpenCV**
-   Pre-trained computer-vision model for dog detection
-   AI/vision model for behavior interpretation where configured

------------------------------------------------------------------------

## 🏗️ High-Level Architecture

``` text
                    ┌─────────────────────┐
                    │      PetSense       │
                    │    Android App      │
                    └──────────┬──────────┘
                               │
             ┌─────────────────┼─────────────────┐
             │                 │                 │
             ▼                 ▼                 ▼
       Authentication      Pet Profile       Dashboard
             │                 │                 │
             └─────────────────┼─────────────────┘
                               │
                               ▼
                     Firebase Database
                               │
             ┌─────────────────┼─────────────────┐
             │                 │                 │
             ▼                 ▼                 ▼
          Health           Behavior          Insights
                               │
                               ▼
                     Camera / Video Input
                               │
                               ▼
                     AI Behavior Analysis
```

------------------------------------------------------------------------

## 📂 Main Application Modules

``` text
PetSense
│
├── Authentication
│   ├── Login
│   └── User Session
│
├── Pet Management
│   ├── Add Pet
│   ├── Pet Profile
│   └── Pet Information
│
├── Dashboard
│   ├── Wellness Summary
│   ├── Activity
│   ├── Sleep
│   ├── Water
│   └── Nutrition
│
├── Health
│   ├── Weight
│   ├── Symptoms
│   └── Health Records
│
├── Behavior
│   ├── Behavior Logs
│   ├── Camera / Video Analysis
│   └── Behavior History
│
├── Insights
│   └── Health & Behavior Summaries
│
└── Profile
    └── Pet/User Profile Management
```

------------------------------------------------------------------------

## 🎨 UI / UX Design

PetSense follows a:

-   Minimal
-   Premium
-   Modern
-   Calm
-   Pet-health focused

design approach.

The UI emphasizes:

-   Clear visual hierarchy
-   Rounded cards
-   Consistent spacing
-   Readable typography
-   Subtle elevation
-   Restrained use of colors
-   Easy navigation
-   Reduced unnecessary scrolling

The application uses a bottom navigation structure:

``` text
Home | Health | Behavior | Insights | Profile
```

------------------------------------------------------------------------

## 🚀 Getting Started

### Requirements

Before running the project, install:

-   Android Studio
-   Android SDK
-   Java/JDK compatible with the project's Gradle configuration
-   An Android emulator or physical Android device

For the AI behavior-analysis prototype:

-   Python 3.x
-   Required Python packages listed in its `requirements.txt`

### Android Setup

1.  Clone or open the PetSense project in Android Studio.
2.  Open the project directory.
3.  Allow Gradle to synchronize.
4.  Configure Firebase for the Android application.
5.  Verify Firebase Authentication and Realtime Database configuration.
6.  Connect an Android device or start an emulator.
7.  Build and run the application.

------------------------------------------------------------------------

## 🔥 Firebase

PetSense uses Firebase for cloud-based application data.

The main Firebase components are:

### Firebase Authentication

Used for:

-   User registration
-   Login
-   User session management

### Firebase Realtime Database

Used for application data such as:

-   Pet information
-   Health records
-   Behavior observations
-   Other user-specific application data

Firebase security rules should be configured so that users can only
access their own data.

------------------------------------------------------------------------

## 🧠 Behavior Analysis Prototype

The behavior-analysis prototype uses a short sequence of frames rather
than analyzing only one image.

Example:

``` text
Video
 ↓
5–10 second analysis window
 ↓
Sample frames
 ↓
Dog detection
 ↓
Behavior interpretation
 ↓
Result
```

### Example Result

``` text
Behavior: Walking

Observation:
The pet appears to be actively moving through the scene.

Confidence:
82%
```

If there is insufficient visual evidence:

``` text
Behavior: Unclear

Observation:
There is not enough visible information to determine the behavior.
```

------------------------------------------------------------------------

## 🧪 Testing

The behavior-analysis system can be tested using pre-recorded videos.

Recommended test cases:

  Test                   Expected Result
  ---------------------- --------------------------
  Dog lying down         Resting
  Dog walking            Walking / Moving
  Dog running            Running
  Dog eating/drinking    Eating / Drinking
  No dog                 No dog detected
  Obstructed dog         Unclear / Low confidence
  Poor-quality footage   Unclear

Pre-recorded footage can be used for repeatable demonstrations.

------------------------------------------------------------------------

## ⚠️ Limitations

The current project is an MVP/prototype.

Some advanced capabilities may require further development, including:

-   Continuous real-time behavior monitoring
-   Custom-trained behavior recognition models
-   Large-scale behavioral datasets
-   Advanced predictive analytics
-   BLE/IoT integration
-   Multi-pet advanced management
-   More sophisticated historical analytics

The AI behavior module should not be treated as a veterinary diagnostic
system.

------------------------------------------------------------------------

## 🔮 Future Enhancements

Possible future improvements include:

-   Real-time continuous behavior monitoring
-   Custom pet behavior recognition model
-   Improved action recognition
-   IoT wearable integration
-   Activity sensors
-   Bluetooth Low Energy (BLE)
-   Advanced health trend analysis
-   Multi-pet support
-   Personalized AI assistant
-   Automated reports
-   Cloud-based video processing
-   More detailed behavioral analytics

------------------------------------------------------------------------

## 👨‍💻 Development Principles

The project follows these principles:

1.  Keep the application simple and usable.
2.  Prefer working features over unnecessary complexity.
3.  Keep the Android application native using Java and XML.
4.  Reuse existing project architecture and resources.
5.  Separate AI processing from the Android UI.
6.  Avoid unsupported medical claims.
7.  Protect user and pet data.
8.  Build features incrementally and test each module independently.

------------------------------------------------------------------------

## 📌 Project Status

The project is currently being developed as a functional final-year
project MVP.

### Implemented / In Progress

-   [x] Android application foundation
-   [x] Firebase Authentication
-   [x] Main dashboard
-   [x] Pet profile module
-   [x] Bottom navigation
-   [ ] Complete health tracking
-   [ ] Complete behavior analysis pipeline
-   [ ] AI behavior integration
-   [ ] Behavior history
-   [ ] Complete insights module
-   [ ] Final testing and integration

Hardware/IoT integration is currently outside the immediate submission
scope and can be added as a future enhancement.

------------------------------------------------------------------------

## 📄 Disclaimer

PetSense is an academic software project intended for pet-care tracking
and behavioral observation.

AI-generated observations are not veterinary diagnoses and should not
replace professional veterinary advice.
