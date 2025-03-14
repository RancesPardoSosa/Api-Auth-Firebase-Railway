package org.example.apiauthfirebase.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.apiauthfirebase.entities.UserFirebase;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5002"})
@RestController
@RequestMapping("/api/user")
public class UserFirebaseController {

    private static final String COLLECTION_NAME = "allowedEmails";

    @GetMapping("/list")
    public List<UserFirebase> getUsers() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = db.collection("allowedEmails").get();
        List<UserFirebase> users = new ArrayList<>();

        for (DocumentSnapshot doc : query.get().getDocuments()) {
            String email = doc.getId(); // Extraemos el email desde el ID del documento
            boolean authorized = doc.getBoolean("authorized") != null ? doc.getBoolean("authorized") : false;
            users.add(new UserFirebase(email, authorized));
        }
        return users;
    }

    @PostMapping("/add")
    public String addUser(@RequestBody UserFirebase user) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection("allowedEmails").document(user.getEmail());
            DocumentSnapshot document = docRef.get().get();

            if (document.exists()) {
                return "Error: El usuario con email " + user.getEmail() + " ya está registrado.";
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("authorized", true); // Siempre guardamos como true

            docRef.set(userData);
            return "Usuario agregado con email: " + user.getEmail();
        } catch (Exception e) {
            return "Error al agregar usuario: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete/{email}")
    public String deleteUser(@PathVariable String email) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection("allowedEmails").document(email);
            DocumentSnapshot document = docRef.get().get();

            if (!document.exists()) {
                return "Error: No se encontró el usuario con email " + email;
            }

            docRef.delete();
            return "Usuario eliminado exitosamente: " + email;
        } catch (Exception e) {
            return "Error al eliminar usuario: " + e.getMessage();
        }
    }

    @GetMapping("/exists/{email}")
    public boolean checkIfUserExists(@PathVariable String email) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(email);
        DocumentSnapshot document = docRef.get().get();
        return document.exists();
    }
}

