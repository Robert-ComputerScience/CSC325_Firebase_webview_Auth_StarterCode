package com.example.csc325_firebase_webview_auth.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.concurrent.Task;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

// The SplashScreenController manages the splash screen's behavior.
// It initializes UI elements and starts a background task to simulate loading
// or perform actual application setup before transitioning to the main view.
public class SplashScreenController implements Initializable {

    // FXML annotation to link the 'statusLabel' defined in splash_screen_v3.fxml
    // to this Java variable. This allows the controller to update the label's text.
    @FXML
    private Label statusLabel;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Create a new Task to perform loading operations in the background.
        // This prevents the UI from freezing while complex or time-consuming
        // operations (like Firebase initialization or data loading) are happening.
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Simulate loading time by pausing the thread.
                // In a real application, this would be replaced with actual initialization logic
                // such as:
                //   - Initializing Firebase (Firestore, Auth, Storage) if not already done in App.java's start method.
                //     (For this project, Firebase initialization is already in App.java's start method,
                //      so this sleep is purely for demonstration of a splash screen's purpose).
                //   - Loading initial data from Firestore.
                //   - Setting up user sessions.

                // Update UI from the background task using Platform.runLater().
                // UI updates MUST be done on the JavaFX Application Thread.
                Platform.runLater(() -> statusLabel.setText("Connecting to services..."));
                Thread.sleep(1500); // Wait for 1.5 seconds

                Platform.runLater(() -> statusLabel.setText("Loading user data..."));
                Thread.sleep(1500); // Wait for another 1.5 seconds

                Platform.runLater(() -> statusLabel.setText("Application ready."));
                Thread.sleep(1000); // Final short wait

                return null; // The task doesn't return a specific value.
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                // This method is called when the background task successfully completes.
                // It ensures the scene switch happens on the JavaFX Application Thread.
                try {
                    // Switch to the main application view (AccessFBView.fxml).
                    // This is done by calling the static setRoot method of the App class.
                    App.setRoot("/files/AccessFBView.fxml");
                } catch (IOException ex) {
                    // Log any exceptions that occur during the FXML loading.
                    Logger.getLogger(SplashScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            protected void failed() {
                super.failed();
                // This method is called if the background task encounters an exception.
                // You could display an error message on the splash screen or log the error.
                Logger.getLogger(SplashScreenController.class.getName()).log(Level.SEVERE, "Splash screen task failed", getException());
                Platform.runLater(() -> statusLabel.setText("Error loading application."));
                // Optionally, transition to an error screen or close the app.
            }
        };

        // Start the background task in a new thread.
        // It's important to run tasks in a separate thread to keep the UI responsive.
        new Thread(loadTask).start();
    }
}