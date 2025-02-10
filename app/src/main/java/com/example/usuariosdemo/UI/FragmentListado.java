package com.example.usuariosdemo.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.usuariosdemo.Model.Tarea;
import com.example.usuariosdemo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

public class FragmentListado extends Fragment {
    private RecyclerView recyclerView;
    private TareaAdapter adapter;
    private TareaViewModel tareaViewModel;
    private FloatingActionButton fabAgregar;
    private boolean mostrarCompletadas;
    private SharedPreferences sharedPreferences;
    private String usuarioEmail; // 🔹 Ahora comparamos con el email del usuario autenticado

    public FragmentListado() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Activa el menú superior

        sharedPreferences = requireActivity().getSharedPreferences("config", requireActivity().MODE_PRIVATE);
        mostrarCompletadas = sharedPreferences.getBoolean("mostrarCompletadas", false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listado, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        fabAgregar = view.findViewById(R.id.fabAgregar);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        // 🔹 Obtener email del usuario autenticado en Firebase
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioEmail = (usuarioActual != null) ? usuarioActual.getEmail() : "";

        adapter = new TareaAdapter(new ArrayList<>(), requireContext(), usuarioEmail);
        recyclerView.setAdapter(adapter);

        tareaViewModel = new ViewModelProvider(requireActivity()).get(TareaViewModel.class);

        // Observar cambios en la base de datos
        tareaViewModel.obtenerTodas().observe(getViewLifecycleOwner(), this::actualizarLista);

        fabAgregar.setOnClickListener(v -> {
            DialogAgregarTarea dialog = new DialogAgregarTarea();
            dialog.show(getParentFragmentManager(), "DialogAgregarTarea");
        });

        // Swipe para completar/eliminar tarea
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Tarea tareaSeleccionada = adapter.getTareaEnPosicion(position);

                if (tareaSeleccionada == null) return;

                boolean esPropietario = tareaSeleccionada.getUsuarioEmail().equals(usuarioEmail);

                if (direction == ItemTouchHelper.RIGHT) { // Marcar como completada
                    if (esPropietario) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Completar Tarea")
                                .setMessage("¿Marcar esta tarea como completada?")
                                .setPositiveButton("Sí", (dialog, which) -> {
                                    tareaSeleccionada.setCompletada(true);
                                    tareaViewModel.actualizar(tareaSeleccionada);
                                })
                                .setNegativeButton("Cancelar", (dialog, which) -> adapter.notifyItemChanged(position))
                                .setCancelable(false)
                                .show();
                    } else {
                        Toast.makeText(requireContext(), "No puedes modificar esta tarea", Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(position);
                    }
                } else if (direction == ItemTouchHelper.LEFT) { // Eliminar tarea
                    if (esPropietario) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Eliminar Tarea")
                                .setMessage("¿Deseas eliminar esta tarea?")
                                .setPositiveButton("Eliminar", (dialog, which) -> {
                                    tareaViewModel.eliminar(tareaSeleccionada);
                                })
                                .setNegativeButton("Cancelar", (dialog, which) -> adapter.notifyItemChanged(position))
                                .setCancelable(false)
                                .show();
                    } else {
                        Toast.makeText(requireContext(), "No puedes eliminar esta tarea", Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(position);
                    }
                }
            }
        }).attachToRecyclerView(recyclerView);

        // Clic en tarea para abrir detalles
        adapter.setOnItemClickListener(this::abrirDetalleTarea);

        return view;
    }

    private void actualizarLista(List<Tarea> tareas) {
        if (tareas == null) return;

        List<Tarea> listaFiltrada = new ArrayList<>();
        for (Tarea tarea : tareas) {
            if (mostrarCompletadas == tarea.isCompletada()) {
                listaFiltrada.add(tarea);
            }
        }
        adapter.setTareas(listaFiltrada);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear(); // Evitar duplicados
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_toggle_completadas);
        item.setChecked(mostrarCompletadas);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_toggle_completadas) {
            mostrarCompletadas = !item.isChecked();
            item.setChecked(mostrarCompletadas);

            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("mostrarCompletadas", mostrarCompletadas);
                editor.apply();
            }

            actualizarLista(tareaViewModel.obtenerTodas().getValue());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirDetalleTarea(Tarea tarea) {
        FragmentDetalleTarea fragment = FragmentDetalleTarea.newInstance(tarea);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
