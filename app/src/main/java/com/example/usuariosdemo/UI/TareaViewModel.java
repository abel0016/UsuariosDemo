package com.example.usuariosdemo.UI;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.usuariosdemo.Model.Tarea;
import com.example.usuariosdemo.Model.TareasRepositorio;

import java.util.List;

public class TareaViewModel extends AndroidViewModel {
    private TareasRepositorio repositorio;
    private LiveData<List<Tarea>> todasLasTareas;

    public TareaViewModel(@NonNull Application application) {
        super(application);
        repositorio = new TareasRepositorio(application);
        todasLasTareas = repositorio.obtenerTodas();
    }

    public LiveData<List<Tarea>> obtenerTodas() {
        return todasLasTareas;
    }

    public void insertar(Tarea tarea) {
        repositorio.insertar(tarea);
    }

    public void actualizar(Tarea tarea) {
        repositorio.actualizar(tarea);
    }

    public void eliminar(Tarea tarea) {
        repositorio.eliminar(tarea);
    }
    public LiveData<Tarea> obtenerTareaPorId(int id) {
        return repositorio.obtenerTareaPorId(id);
    }
    public void actualizarImagen(int tareaId, String nuevaUri) {
        Log.d("DEBUG", "Llamando a repositorio.actualizarImagen con tareaId: " + tareaId);
        repositorio.actualizarImagen(tareaId, nuevaUri);
    }



}