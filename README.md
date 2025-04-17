# Torii
## Setup
### Connect to Firebase
🔧 Step 1: Create a Firebase project

1. Go to: https://console.firebase.google.com

2. Click Add Project

3. Name the project → Next

4. (Optional) Enable/disable Google Analytics → Create Project

📱 Step 2: Connect the Android app to Firebase

1. After creating the project, click Add App → select Android

2. Fill in the information:

- Android package name: (`com.example.myapp`)

- (Optional) App nickname, SHA-1

3. Click Register app

📄 Step 3: Download `google-services.json`

1. Download the `google-services.json` file

2. Copy it to the app/ folder of the Android project

⚙️ Step 4: Add Firebase SDK to the Android project

a) `project-level build.gradle` file:

    dependencies {
        classpath 'com.google.gms:google-services:4.4.1' // or latest version
    }
    
 b) `app-level build.gradle` file
 
    plugins {
      id 'com.android.application'
      id 'com.google.gms.google-services' // Add this line
    }

    dependencies {
      implementation platform('com.google.firebase:firebase-bom:32.7.2') // BOM for version sync management
      implementation 'com.google.firebase:firebase-analytics' // example add Firebase Analytics
    }
✅ Step 5: Sync & Test
1. Click Sync Now (Android Studio will sync the project)

2. Run the app and check the log to see if Firebase is working

### Enable Firebase Authentication

🔐 Step 1: Enable Authentication in Firebase Console
1. Go to Firebase Console → select your project.

2. Go to Build > Authentication > Sign-in method tab.

3. Enable Email/Password → click Save.

📦 Step 2: Add Firebase Auth to Android project
In `app/build.gradle`, add dependency (if you don't have it):

     dependencies {
         implementation 'com.google.firebase:firebase-auth'
     }

  ✅ Step 3: Configure Google Sign-In client in code
In the` GoogleAuthRepository.kt` file, you need to initialize GoogleSignInClient as follows:

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("YOUR_WEB_CLIENT_ID") // Replace with Web Client ID from Firebase Console
                .requestEmail()
                .build()
        )

Run & Test. If there are any errors, check Logcat and try again.
