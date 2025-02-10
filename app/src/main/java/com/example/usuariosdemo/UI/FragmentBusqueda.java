package com.example.usuariosdemo.UI;

import android.os.Bundle;
import android.view.*;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usuariosdemo.Model.Tarea;
import com.example.usuariosdemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FragmentBusqueda extends Fragment {

    private RecyclerView recyclerView;
    private TareaAdapter tareaAdapter;
    private TareaViewModel tareaViewModel;
    private SearchView searchView;
    private String usuarioId;
    private List<Tarea> listaTareas = new ArrayList<>();

    public FragmentBusqueda() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_busqueda, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBusqueda);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        searchView = view.findViewById(R.id.searchView);

        // 🔹 Obtener usuario autenticado en Firebase
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioId = (usuarioActual != null) ? usuarioActual.getUid() : "";

        tareaAdapter = new TareaAdapter(new ArrayList<>(), requireContext(), usuarioId);
        recyclerView.setAdapter(tareaAdapter);

        tareaViewModel = new ViewModelProvider(requireActivity()).get(TareaViewModel.class);

        // 🔹 Observar los cambios en la base de datos y guardar las tareas en una lista
        tareaViewModel.obtenerTodas().observe(getViewLifecycleOwner(), tareas -> {
            if (tareas != null) {
                listaTareas.clear();
                listaTareas.addAll(tareas);
                tareaAdapter.setTareas(listaTareas);
            }
        });

        // 🔹 Configurar el SearchView para filtrar las tareas
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarTareas(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarTareas(newText);
                return true;
            }
        });

        return view;
    }

    private void filtrarTareas(String texto) {
        List<Tarea> tareasFiltradas = new ArrayList<>();
        for (Tarea tarea : listaTareas) {
            if (tarea.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                    tarea.getDescripcion().toLowerCase().contains(texto.toLowerCase())) {
                tareasFiltradas.add(tarea);
            }
        }
        tareaAdapter.setTareas(tareasFiltradas);
    }
}
