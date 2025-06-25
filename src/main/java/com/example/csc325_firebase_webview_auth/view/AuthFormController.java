package com.example.csc325_firebase_webview_auth.view;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox; // Import VBox

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

// AuthFormController manages both the sign-in and registration functionalities
// within a single FXML view by toggling visibility of different VBoxes.
public class AuthFormController {

    // FXML elements for Login form
    @FXML private VBox loginVBox;
    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginMessageLabel;

    // FXML elements for Registration form
    @FXML private VBox registerVBox;
    @FXML private TextField registerEmailField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerConfirmPasswordField;
    @FXML private Label registerMessageLabel;

    // Firebase Authentication instance
    private FirebaseAuth fAuth;

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up the Firebase Auth instance.
     */
    @FXML
    public void initialize() {
        fAuth = App.fauth; // Get the initialized FirebaseAuth instance from the App class.
        if (fAuth == null) {
            System.err.println("Firebase Auth not initialized in App class!");
            loginMessageLabel.setText("Firebase Auth error.");
            registerMessageLabel.setText("Firebase Auth error.");
        }
        // Ensure only login form is visible initially
        showLoginForm(null); // Call this to set initial visibility
    }

    /**
     * Toggles visibility to show the login form and hide the registration form.
     * @param event The ActionEvent that triggered this method (can be null if called internally).
     */
    @FXML
    private void showLoginForm(ActionEvent event) {
        loginVBox.setVisible(true);
        loginVBox.setManaged(true);
        registerVBox.setVisible(false);
        registerVBox.setManaged(false);
        loginMessageLabel.setText(""); // Clear messages
        registerMessageLabel.setText(""); // Clear messages
    }

    /**
     * Toggles visibility to show the registration form and hide the login form.
     * @param event The ActionEvent that triggered this method.
     */
    @FXML
    private void showRegisterForm(ActionEvent event) {
        loginVBox.setVisible(false);
        loginVBox.setManaged(false);
        registerVBox.setVisible(true);
        registerVBox.setManaged(true);
        loginMessageLabel.setText(""); // Clear messages
        registerMessageLabel.setText(""); // Clear messages
    }

    /**
     * Handles the sign-in attempt when the "Sign In" button is clicked.
     * @param event The ActionEvent that triggered this method.
     */
    @FXML
    private void handleSignIn(ActionEvent event) {
        String email = loginEmailField.getText();
        String password = loginPasswordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("Email and password cannot be empty.");
            return;
        }

        try {
            // Firebase Admin SDK's sign-in methods are typically for custom token verification
            // or managing users on the backend. For client-side sign-in, you'd use a client SDK.
            // Since we're using Firebase Admin SDK, we'll simulate authentication by checking user existence.
            // In a real client app, you would use: fAuth.signInWithEmailAndPassword(email, password)
            UserRecord userRecord = fAuth.getUserByEmail(email); // Checks if user exists
            // A simple check if the user exists and the password is "correct" for demonstration with Admin SDK.
            // THIS IS NOT SECURE FOR PRODUCTION CLIENT-SIDE AUTHENTICATION.
            // For production, implement proper client-side auth flow with Firebase Client SDK.
            if (userRecord != null && userRecord.getEmail().equals(email)) { // Basic check, ideally confirm password securely
                System.out.println("User " + userRecord.getUid() + " signed in (simulated).");
                App.setRoot("/files/AccessFBView.fxml"); // On success, go to main app
            } else {
                loginMessageLabel.setText("Invalid email or password.");
            }

        } catch (FirebaseAuthException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("auth/user-not-found") || errorMessage.contains("auth/wrong-password")) {
                loginMessageLabel.setText("Invalid email or password.");
            } else if (errorMessage.contains("auth/invalid-email")) {
                loginMessageLabel.setText("Invalid email format.");
            } else {
                loginMessageLabel.setText("Login failed: " + errorMessage);
            }
            Logger.getLogger(AuthFormController.class.getName()).log(Level.SEVERE, "Sign-in failed", e);
        } catch (IOException ex) {
            Logger.getLogger(AuthFormController.class.getName()).log(Level.SEVERE, "Failed to load main view", ex);
        }
    }

    /**
     * Handles the registration attempt when the "Register" button is clicked.
     * @param event The ActionEvent that triggered this method.
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        String email = registerEmailField.getText();
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();

        // Input validation for registration
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            registerMessageLabel.setText("All fields must be filled.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            registerMessageLabel.setText("Passwords do not match.");
            return;
        }
        if (password.length() < 6) { // Firebase minimum password length
            registerMessageLabel.setText("Password must be at least 6 characters long.");
            return;
        }

        try {
            // Create a new user record in Firebase Authentication.
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setEmailVerified(false)
                    .setDisabled(false);

            UserRecord userRecord = fAuth.createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());

            registerMessageLabel.setText("Registration successful! Please sign in.");
            // Clear fields after successful registration
            registerEmailField.clear();
            registerPasswordField.clear();
            registerConfirmPasswordField.clear();

            // Automatically switch back to the login form after registration
            showLoginForm(null);

        } catch (FirebaseAuthException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("auth/email-already-exists")) {
                registerMessageLabel.setText("This email is already registered.");
            } else if (errorMessage.contains("auth/invalid-email")) {
                registerMessageLabel.setText("Invalid email format.");
            } else if (errorMessage.contains("auth/weak-password")) {
                registerMessageLabel.setText("Password is too weak. Choose a stronger one.");
            } else {
                registerMessageLabel.setText("Registration failed: " + errorMessage);
            }
            Logger.getLogger(AuthFormController.class.getName()).log(Level.SEVERE, "Registration failed", e);
        }
    }
}