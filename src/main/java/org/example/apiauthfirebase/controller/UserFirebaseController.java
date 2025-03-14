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

    private static final String COLLECTION_ALLOWED = "allowedEmails";
    private static final String COLLECTION_ADMIN = "admin";

    // Obtener la lista de usuarios de allowedEmails
    @GetMapping("/list")
    public List<UserFirebase> getUsers() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_ALLOWED).get();
        List<UserFirebase> users = new ArrayList<>();

        for (DocumentSnapshot doc : query.get().getDocuments()) {
            String email = doc.getId();
            boolean authorized = doc.getBoolean("authorized") != null ? doc.getBoolean("authorized") : false;
            users.add(new UserFirebase(email, authorized));
        }
        return users;
    }

    // Agregar usuario a allowedEmails
    @PostMapping("/add")
    public String addUser(@RequestBody UserFirebase user) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection(COLLECTION_ALLOWED).document(user.getEmail());
            DocumentSnapshot document = docRef.get().get();

            if (document.exists()) {
                return "Error: El usuario con email " + user.getEmail() + " ya está registrado.";
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("authorized", true);
            docRef.set(userData);
            return "Usuario agregado con email: " + user.getEmail();
        } catch (Exception e) {
            return "Error al agregar usuario: " + e.getMessage();
        }
    }

    // Eliminar usuario de allowedEmails
    @DeleteMapping("/delete/{email}")
    public String deleteUser(@PathVariable String email) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection(COLLECTION_ALLOWED).document(email);
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

    // Verificar si un usuario existe en allowedEmails
    @GetMapping("/exists/{email}")
    public boolean checkIfUserExists(@PathVariable String email) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_ALLOWED).document(email);
        DocumentSnapshot document = docRef.get().get();
        return document.exists();
    }

    // Verificar si un usuario tiene acceso de admin
    @PostMapping("/login")
    public String adminLogin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection(COLLECTION_ADMIN).document(email);
            DocumentSnapshot document = docRef.get().get();

            if (!document.exists() || !document.getBoolean("authorized")) {
                return "Acceso denegado";
            }
            return "Acceso concedido";
        } catch (Exception e) {
            return "Error en la autenticación: " + e.getMessage();
        }
    }
}


