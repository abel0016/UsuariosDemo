package com.example.usuariosdemo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.usuariosdemo.databinding.ActivityRegistroBinding;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;

import java.util.ArrayList;
import java.util.List;

public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Verificar autenticación pendiente
        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            pendingResultTask.addOnSuccessListener(authResult -> {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                iniciarMainActivity();
            }).addOnFailureListener(e -> {
                mostrarError("Error en autenticación pendiente: " + e.getMessage());
            });
        }

        // Configurar Google Sign-In
        configurarGoogleSignIn();
        registrarLauncherGoogleSignIn();

        // Configurar listeners de botones
        binding.btnLogin.setOnClickListener(view -> logarUsuario());
        binding.btnRegistro.setOnClickListener(view -> crearUsuario());
        binding.btnGoogleSignIn.setOnClickListener(view -> iniciarSignInConGoogle());
        binding.btnGithubSignIn.setOnClickListener(view -> iniciarSignInConGithub());
    }

    private void configurarGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void registrarLauncherGoogleSignIn() {
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                autenticarConFirebaseGoogle(account.getIdToken());
                            }
                        } catch (ApiException e) {
                            mostrarError("Error al iniciar con Google: " + e.getMessage());
                        }
                    } else {
                        mostrarError("Inicio de sesión con Google cancelado.");
                    }
                }
        );
    }

    private void iniciarSignInConGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void autenticarConFirebaseGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser usuario = mAuth.getCurrentUser();
                        if (usuario != null) {
                            Toast.makeText(RegistroActivity.this, "Bienvenido con Google: " + usuario.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                        iniciarMainActivity();
                    } else {
                        mostrarError("Error al autenticar con Firebase: " + task.getException().getMessage());
                    }
                });
    }

    private void iniciarSignInConGithub() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");

        // Configurar permisos (scopes)
        List<String> scopes = new ArrayList<>();
        scopes.add("user:email");
        provider.setScopes(scopes);

        // Intentar iniciar sesión con GitHub
        mAuth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        Toast.makeText(RegistroActivity.this, "Inicio de sesión con GitHub exitoso: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    }
                    iniciarMainActivity();
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        FirebaseAuthUserCollisionException ex = (FirebaseAuthUserCollisionException) e;
                        String email = ex.getEmail();

                        if (email != null) {
                            // Obtener los métodos de inicio de sesión disponibles para este email
                            mAuth.fetchSignInMethodsForEmail(email)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            List<String> providers = task.getResult().getSignInMethods();
                                            if (providers != null && !providers.isEmpty()) {
                                                String providerOriginal = providers.get(0); // Proveedor con el que se registró la cuenta

                                                mostrarError("Este email ya está registrado con: " + providerOriginal);

                                                // Si es Google, iniciar sesión con Google
                                                if (providerOriginal.equals(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)) {
                                                    iniciarSignInConGoogle(); // Método para iniciar sesión con Google
                                                } else if (providerOriginal.equals(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                                                    mostrarError("Inicia sesión con email y contraseña.");
                                                }
                                            }
                                        }
                                    });
                        }
                    } else {
                        mostrarError("Error en autenticación con GitHub: " + e.getMessage());
                    }
                });
    }





    private void crearUsuario() {
        String email = binding.textoMail.getText().toString().trim();
        String password = binding.textoPassword.getText().toString().trim();
        String password2 = binding.textoPassword2.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            mostrarError("Por favor, completa todos los campos");
            return;
        } else if (!password.equals(password2)) {
            mostrarError("Las contraseñas no coinciden");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser usuario = mAuth.getCurrentUser();
                        if (usuario != null) {
                            Toast.makeText(RegistroActivity.this, "Bienvenido, nuevo usuario: " + usuario.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                        iniciarMainActivity();
                    } else {
                        mostrarError("Error al crear usuario: " + task.getException().getMessage());
                    }
                });
    }

    private void logarUsuario() {
        String email = binding.textoMail.getText().toString().trim();
        String password = binding.textoPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, completa todos los campos");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser usuario = mAuth.getCurrentUser();
                        if (usuario != null) {
                            Toast.makeText(RegistroActivity.this, "Bienvenido de nuevo: " + usuario.getEmail(), Toast.LENGTH_SHORT).show();
                        }
                        iniciarMainActivity();
                    } else {
                        mostrarError("Error al iniciar sesión: " + task.getException().getMessage());
                    }
                });
    }

    private void iniciarMainActivity() {
        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(RegistroActivity.this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioActual = mAuth.getCurrentUser();
        if (usuarioActual != null) {
            iniciarMainActivity();
        }
    }
}
