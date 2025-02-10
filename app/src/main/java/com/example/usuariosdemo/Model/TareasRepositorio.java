package com.example.usuariosdemo.Model;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TareasRepositorio {
    private TareaDao tareaDao;
    private LiveData<List<Tarea>> todasLasTareas;
    private ExecutorService executorService;
    public TareasRepositorio(Application application) {
        TareaDatabase db = TareaDatabase.getInstance(application);
        tareaDao = db.tareaDao();
        todasLasTareas = tareaDao.obtenerTodas();
        executorService = Executors.newSingleThreadExecutor(); // INICIALIZACIÓN CORRECTA
    }

    public LiveData<List<Tarea>> obtenerTodas() {
        return todasLasTareas;
    }

    public void insertar(Tarea tarea) {
        new Thread(() -> tareaDao.insertar(tarea)).start();
    }

    public void actualizar(Tarea tarea) {
        executorService.execute(() -> tareaDao.actualizar(tarea));
    }

    public void eliminar(Tarea tarea) {
        new Thread(() -> tareaDao.eliminar(tarea)).start();
    }
    public LiveData<Tarea> obtenerTareaPorId(int id) {
        return tareaDao.obtenerTareaPorId(id);
    }
    public void actualizarImagen(int tareaId, String nuevaUri) {
        Log.d("DEBUG", "Ejecutando Room para actualizar imagen de tarea con ID: " + tareaId);
        executorService.execute(() -> {
            tareaDao.actualizarImagen(tareaId, nuevaUri);
            Log.d("DEBUG", "Imagen de tarea con ID " + tareaId + " actualizada en Room.");
        });
    }



}
