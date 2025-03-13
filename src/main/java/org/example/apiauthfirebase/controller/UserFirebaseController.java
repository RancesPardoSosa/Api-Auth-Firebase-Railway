package org.example.apiauthfirebase.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;
import org.example.apiauthfirebase.entities.UserFirebase;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173") // acepta peticiones desde react
@RestController
@RequestMapping("/api/user")
public class UserFirebaseController {

    @GetMapping("/list")
    public List<UserFirebase> getUsers() throws FirebaseAuthException {
        ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
        List<UserFirebase> users = new ArrayList<>();
        UserFirebase userFirebase;
        for (UserRecord user : page.iterateAll()) {
            userFirebase = new UserFirebase(user.getUid(),user.getEmail());
            users.add(userFirebase);
        }
        return users;
    }

    @PostMapping("/add")
    public String addUser(@RequestBody UserFirebase user) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(user.getEmail()) // Solo email
                    .setEmailVerified(true);    // Verificar que es un email real
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            return "Usuario creado con UID: " + userRecord.getUid();
        } catch (FirebaseAuthException e) {
            return "Error al crear usuario: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete/{uid}")
    public String deleteUser(@PathVariable String uid) {
        try {
            FirebaseAuth.getInstance().deleteUser(uid);
            return "Usuario eliminado con éxito.";
        } catch (FirebaseAuthException e) {
            return "Error al eliminar usuario: " + e.getMessage();
        }
    }

    @GetMapping("/exists/{email}")
    public boolean checkIfUserExists(@PathVariable String email) {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            return userRecord != null; // Si encuentra el usuario, devuelve true
        } catch (FirebaseAuthException e) {
            return false; // Si hay un error (usuario no encontrado), devuelve false
        }
    }

}
