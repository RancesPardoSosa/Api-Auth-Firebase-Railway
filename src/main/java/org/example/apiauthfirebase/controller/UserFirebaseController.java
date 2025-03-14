package org.example.apiauthfirebase.controller;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.apiauthfirebase.entities.UserFirebase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5002"}) // Acepta peticiones desde React
@RestController
@RequestMapping("/api/user")
public class UserFirebaseController {

    private static final String COLLECTION_NAME = "allowedEmails";

    // Obtener lista de usuarios desde Firestore
    @GetMapping("/list")
    public ResponseEntity<List<UserFirebase>> getUsers() {
        List<UserFirebase> users = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                String uid = document.getId();  // El ID del documento es el email
                boolean authorized = document.getBoolean("authorized");

                if (authorized) { // Solo agregar usuarios autorizados
                    users.add(new UserFirebase(uid, document.getId()));
                }
            }
            return ResponseEntity.ok(users);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // Agregar usuario a Firestore
    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody UserFirebase user) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getEmail());

        Map<String, Object> data = new HashMap<>();
        data.put("authorized", true);

        try {
            docRef.set(data).get();
            return ResponseEntity.ok("Usuario agregado correctamente.");
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.internalServerError().body("Error al agregar usuario: " + e.getMessage());
        }
    }

    // Eliminar usuario de Firestore
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(email);

        try {
            docRef.delete().get();
            return ResponseEntity.ok("Usuario eliminado correctamente.");
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.internalServerError().body("Error al eliminar usuario: " + e.getMessage());
        }
    }

    // Verificar si el usuario está autorizado en Firestore
    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> checkIfUserExists(@PathVariable String email) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(email);

        try {
            DocumentSnapshot document = docRef.get().get();
            if (document.exists() && document.getBoolean("authorized") != null) {
                return ResponseEntity.ok(document.getBoolean("authorized"));
            }
            return ResponseEntity.ok(false);
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.internalServerError().body(false);
        }
    }
}
