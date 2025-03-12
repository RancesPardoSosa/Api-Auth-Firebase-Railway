package org.example.apiauthfirebase.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Obtiene la variable de entorno
        String firebaseJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");

        if (firebaseJson == null || firebaseJson.isEmpty()) {
            throw new IllegalStateException("No se encontró la variable de entorno GOOGLE_APPLICATION_CREDENTIALS_JSON");
        }

        // Convierte la variable de entorno en un flujo de entrada
        InputStream serviceAccount = new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8));

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
