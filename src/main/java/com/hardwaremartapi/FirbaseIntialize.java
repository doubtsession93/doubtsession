package com.hardwaremartapi;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FirbaseIntialize {
	@PostConstruct
    public void initialize() {
        try {
        	
            InputStream serviceAccount = this.getClass().getClassLoader().getResourceAsStream("./serviceAccountKey.json");
            @SuppressWarnings("deprecation")
			FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://hardwaremart-ba4a5.firebaseio.com")
                    .setStorageBucket("hardwaremart-ba4a5.appspot.com")
                    .build();
            if(FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
