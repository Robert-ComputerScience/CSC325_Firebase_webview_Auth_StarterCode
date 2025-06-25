package com.example.csc325_firebase_webview_auth.model;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class initializes the Firebase Firestore connection.
 * It loads the Firebase service account credentials from a JSON file included
 * in the project resources.
 */
public class FirestoreContext {

    /**
     * Initializes the Firebase application and returns a Firestore instance.
     *
     * @return A Firestore database client.
     */
    public Firestore firebase() {
        try {
            // This path must exactly match the location of your Firebase service account JSON file
            // in the src/main/resources directory. The leading '/' indicates the root of the classpath.
            // In a Maven project, src/main/resources is automatically added to the classpath.
            InputStream serviceAccount = getClass().getResourceAsStream("/files/key.json");

            // --- FIX AND EXPLANATION ---
            // The error `java.lang.NullPointerException` occurs here because `serviceAccount` is `null`.
            // This means the `key.json` file was not found at the specified resource path.
            //
            // **Troubleshooting steps for the user:**
            // 1.  **Verify the File's Location:** Ensure that `key.json` is located in the
            //     `src/main/resources/files/` directory of your project.
            // 2.  **Verify the Filename:** Double-check the filename for typos or case-sensitivity issues.
            //     It must be exactly `key.json`.
            // 3.  **Clean and Rebuild:** Sometimes, the IDE or Maven doesn't include resources correctly.
            //     Run `mvn clean install` from the terminal or use IntelliJ's "Build" -> "Rebuild Project"
            //     option to ensure the resources are copied to the build output.
            // 4.  **Check Build Path:** Confirm that the `resources` folder is marked as a "Resources Root"
            //     in IntelliJ's project structure settings (File -> Project Structure -> Modules -> [your module] -> Sources).
            if (serviceAccount == null) {
                // Add a more helpful error message for the user.
                System.err.println("FATAL ERROR: Could not find 'key.json' in resources.");
                System.err.println("Please ensure the file is located at 'src/main/resources/files/key.json'.");
                throw new IOException("Firebase service account file not found.");
            }

            // --- End of Fix ---

            // Build Firebase options using the credentials from the service account file.
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Check if FirebaseApp is already initialized to avoid re-initialization errors.
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase has been successfully initialized.");
            } else {
                System.out.println("Firebase is already initialized.");
            }

        } catch (IOException ex) {
            // Print the stack trace for any IO exceptions that occur.
            System.err.println("An IOException occurred during Firebase initialization:");
            ex.printStackTrace();
        }

        // Return the Firestore instance.
        return FirestoreClient.getFirestore();
    }
}