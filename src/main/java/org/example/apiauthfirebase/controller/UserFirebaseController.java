package org.example.apiauthfirebase.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.apiauthfirebase.entities.UserFirebase;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getEmail());
        docRef.set(user);
        return "Usuario agregado con éxito";
    }

    @DeleteMapping("/delete/{email}")
    public String deleteUser(@PathVariable String email) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(email);
        docRef.delete();
        return "Usuario eliminado con éxito";
    }

    @GetMapping("/exists/{email}")
    public boolean checkIfUserExists(@PathVariable String email) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(email);
        DocumentSnapshot document = docRef.get().get();
        return document.exists();
    }
}

