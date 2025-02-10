package com.example.usuariosdemo.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "tareas")
public class Tarea implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String titulo;
    private String descripcion;
    private String fecha;

    @ColumnInfo(name = "imagen_uri")
    private String imagenUri;

    private boolean completada;

    @ColumnInfo(name = "usuario_email") // 🔹 Nuevo campo para almacenar el email del creador
    private String usuarioEmail;

    public Tarea(String titulo, String descripcion, String fecha, String imagenUri, boolean completada, String usuarioEmail) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.imagenUri = imagenUri;
        this.completada = completada;
        this.usuarioEmail = usuarioEmail;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getFecha() { return fecha; }
    public String getImagenUri() { return imagenUri; }
    public void setImagenUri(String imagenUri) { this.imagenUri = imagenUri; }
    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }
}
