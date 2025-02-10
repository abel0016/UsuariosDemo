package com.example.usuariosdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.usuariosdemo.Model.Tarea;
import com.example.usuariosdemo.UI.DialogAgregarTarea;
import com.example.usuariosdemo.UI.FragmentListado;
import com.example.usuariosdemo.UI.FragmentBusqueda;
import com.example.usuariosdemo.UI.TareaAdapter;
import com.example.usuariosdemo.UI.TareaViewModel;
import com.example.usuariosdemo.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private TareaViewModel tareaViewModel;
    private String usuarioEmail;
    private RecyclerView recyclerView;
    private TareaAdapter tareaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔹 Configurar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser usuarioActual = mAuth.getCurrentUser();

        if (usuarioActual == null) {
            volverRegistro();
            return;
        } else {
            usuarioEmail = usuarioActual.getEmail();
        }

        // 🔹 Configurar Toolbar
        setSupportActionBar(binding.toolbar);

        // 🔹 Configurar BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.nav_listado) {
                fragment = new FragmentListado();
            } else if (item.getItemId() == R.id.nav_busqueda) {
                fragment = new FragmentBusqueda();
            }

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
            return true;
        });

        // 🔹 Cargar `FragmentListado` al iniciar sesión
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new FragmentListado())
                    .commit();
        }

        // 🔹 Botón para agregar tarea
        binding.fabAgregar.setOnClickListener(v -> {
            DialogAgregarTarea dialog = new DialogAgregarTarea();
            dialog.show(getSupportFragmentManager(), "DialogAgregarTarea");
        });
    }

    private void volverRegistro() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            volverRegistro();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
