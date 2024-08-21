package com.lahcosah.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    @SuppressWarnings("deprecation")
    @Bean
    public FirebaseApp initializeFirebaseApp() throws IOException {
        String credentials = System.getenv("GOOGLE_CREDENTIALS");
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ByteArrayInputStream(credentials.getBytes()));

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(googleCredentials)
                .setStorageBucket(System.getenv("FIREBASE_STORAGE_BUCKET"))
                .build();

        return FirebaseApp.initializeApp(options);
    }
}

