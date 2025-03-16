package org.example.apiauthfirebase.controller;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {"https://panel-admin-kill-data.firebaseapp.com", "https://panel-admin-kill-data.web.app"})
@RestController
@RequestMapping("/api/auth")
public class AdminLoginController {

    private static final String COLLECTION_NAME = "admin";

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            response.put("success", false);
            response.put("message", "El email es requerido.");
            return response;
        }

        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(email);

        try {
            DocumentSnapshot document = docRef.get().get();
            if (document.exists() && Boolean.TRUE.equals(document.getBoolean("Authorized"))) {
                response.put("success", true);
                response.put("message", "Acceso permitido.");
            } else {
                response.put("success", false);
                response.put("message", "Acceso denegado.");
            }
        } catch (InterruptedException | ExecutionException e) {
            response.put("success", false);
            response.put("message", "Error en la autenticación: " + e.getMessage());
        }

        return response;
    }
}

