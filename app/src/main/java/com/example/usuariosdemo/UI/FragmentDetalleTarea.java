package com.example.usuariosdemo.UI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.usuariosdemo.Model.Tarea;
import com.example.usuariosdemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FragmentDetalleTarea extends Fragment {
    private ImageView ivImagen;
    private TextView tvTitulo, tvDescripcion, tvFecha;
    private Button btnCambiarImagen, btnVolver;
    private Tarea tarea;
    private TareaViewModel tareaViewModel;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String usuarioActualId;

    public FragmentDetalleTarea() {}

    public static FragmentDetalleTarea newInstance(Tarea tarea) {
        FragmentDetalleTarea fragment = new FragmentDetalleTarea();
        Bundle args = new Bundle();
        args.putSerializable("tarea", tarea);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_tarea, container, false);

        ivImagen = view.findViewById(R.id.ivImagen);
        tvTitulo = view.findViewById(R.id.tvTitulo);
        tvDescripcion = view.findViewById(R.id.tvDescripcion);
        tvFecha = view.findViewById(R.id.tvFecha);
        btnCambiarImagen = view.findViewById(R.id.btnCambiarImagen);
        btnVolver = view.findViewById(R.id.btnVolver);

        tareaViewModel = new ViewModelProvider(requireActivity()).get(TareaViewModel.class);
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioActualId = (usuarioActual != null) ? usuarioActual.getUid() : "";

        if (getArguments() != null) {
            tarea = (Tarea) getArguments().getSerializable("tarea");

            if (tarea != null) {
                tvTitulo.setText(tarea.getTitulo());
                tvDescripcion.setText(tarea.getDescripcion());
                tvFecha.setText(tarea.getFecha());

                if (!tarea.getUsuarioEmail().equals(usuarioActualId)) {
                    btnCambiarImagen.setVisibility(View.GONE);
                }

                tareaViewModel.obtenerTareaPorId(tarea.getId()).observe(getViewLifecycleOwner(), tareaActualizada -> {
                    if (tareaActualizada != null && tareaActualizada.getImagenUri() != null) {
                        cargarImagen(tareaActualizada.getImagenUri());
                    } else {
                        ivImagen.setImageResource(R.drawable.default_image);
                    }
                });
            }
        }

        btnCambiarImagen.setOnClickListener(v -> seleccionarImagen());

        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imagenUri = data.getData();
            if (imagenUri != null) {
                try {
                    Uri nuevaUri = copiarImagenAlmacenamientoInterno(imagenUri);
                    ivImagen.setImageURI(nuevaUri);

                    String uriLimpia = nuevaUri.toString().replace("file://", "");
                    tareaViewModel.actualizarImagen(tarea.getId(), uriLimpia);

                    Toast.makeText(getContext(), "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private Uri copiarImagenAlmacenamientoInterno(Uri imagenUri) throws IOException {
        File directorio = new File(requireContext().getFilesDir(), "imagenes_tareas");
        if (!directorio.exists() && !directorio.mkdirs()) {
            throw new IOException("No se pudo crear el directorio para guardar imágenes");
        }

        File archivoDestino = new File(directorio, "tarea_" + System.currentTimeMillis() + ".jpg");
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(imagenUri);
             OutputStream outputStream = new FileOutputStream(archivoDestino)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return Uri.fromFile(archivoDestino);
    }

    private void cargarImagen(String uri) {
        File archivoImagen = new File(uri.replace("file://", ""));
        if (archivoImagen.exists()) {
            ivImagen.setImageURI(Uri.fromFile(archivoImagen));
        } else {
            ivImagen.setImageResource(R.drawable.default_image);
        }
    }
}
