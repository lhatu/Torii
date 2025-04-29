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

✅ Step 4: Sync & Test
1. Click Sync Now (Android Studio will sync the project)

2. Run the app and check the log to see if Firebase is working

### Enable Firebase Authentication

🔐 Step 1: Enable Authentication in Firebase Console
1. Go to Firebase Console → select your project.

2. Go to Build > Authentication > Sign-in method tab.

3. Enable Email/Password → click Save.

  ✅ Step 2: Configure Google Sign-In client in code
In the` GoogleAuthRepository.kt` file, you need to initialize GoogleSignInClient as follows:

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("YOUR_WEB_CLIENT_ID") // Replace with Web Client ID from Firebase Console
                .requestEmail()
                .build()
        )

Run & Test. If there are any errors, check Logcat and try again.
