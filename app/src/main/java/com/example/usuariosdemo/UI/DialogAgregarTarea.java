package com.example.usuariosdemo.UI;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.usuariosdemo.Model.Tarea;
import com.example.usuariosdemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class DialogAgregarTarea extends DialogFragment {
    private EditText etTitulo, etDescripcion;
    private TextView tvFechaSeleccionada;
    private ImageView ivImagenSeleccionada;
    private Uri imagenUri = null;
    private String fechaSeleccionada = "";

    private TareaViewModel tareaViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_agregar_tarea, container, false);

        etTitulo = view.findViewById(R.id.etTitulo);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        tvFechaSeleccionada = view.findViewById(R.id.tvFechaSeleccionada);
        ivImagenSeleccionada = view.findViewById(R.id.ivImagenSeleccionada);

        Button btnSeleccionarFecha = view.findViewById(R.id.btnSeleccionarFecha);
        Button btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen);
        Button btnGuardar = view.findViewById(R.id.btnGuardar);

        tareaViewModel = new ViewModelProvider(requireActivity()).get(TareaViewModel.class);
        btnSeleccionarFecha.setOnClickListener(v -> seleccionarFecha());
        btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());
        btnGuardar.setOnClickListener(v -> guardarTarea());

        return view;
    }

    private void seleccionarFecha() {
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    fechaSeleccionada = year + "-" + (month + 1) + "-" + dayOfMonth;
                    tvFechaSeleccionada.setText("Fecha: " + fechaSeleccionada);
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            imagenUri = data.getData();
            ivImagenSeleccionada.setImageURI(imagenUri);
            ivImagenSeleccionada.setVisibility(View.VISIBLE);
        }
    }

    private void guardarTarea() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (titulo.isEmpty() || descripcion.isEmpty() || fechaSeleccionada.isEmpty()) {
            Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagenPath = (imagenUri != null) ? imagenUri.toString() : "DEFAULT_IMAGE";

        // 🔹 Obtener el email del usuario autenticado
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual == null) {
            Toast.makeText(getContext(), "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }
        String usuarioEmail = usuarioActual.getEmail(); // ⚠️ Ahora se guarda el EMAIL en lugar del UID

        // 🔹 Crear la tarea con el EMAIL del usuario
        Tarea nuevaTarea = new Tarea(titulo, descripcion, fechaSeleccionada, imagenPath, false, usuarioEmail);
        tareaViewModel.insertar(nuevaTarea);

        dismiss();
    }


}
