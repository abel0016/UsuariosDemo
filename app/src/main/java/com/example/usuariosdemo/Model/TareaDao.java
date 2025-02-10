package com.example.usuariosdemo.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TareaDao {
    @Insert
    void insertar(Tarea tarea);

    @Update
    void actualizar(Tarea tarea);

    @Delete
    void eliminar(Tarea tarea);

    @Query("SELECT * FROM tareas ORDER BY fecha ASC")
    LiveData<List<Tarea>> obtenerTodas();

    @Query("SELECT * FROM tareas WHERE id = :id LIMIT 1")
    LiveData<Tarea> obtenerTareaPorId(int id);

    @Query("UPDATE tareas SET imagen_uri = :nuevaUri WHERE id = :tareaId")
    void actualizarImagen(int tareaId, String nuevaUri);
}
