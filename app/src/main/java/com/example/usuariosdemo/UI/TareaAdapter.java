package com.example.usuariosdemo.UI;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usuariosdemo.Model.Tarea;
import com.example.usuariosdemo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private List<Tarea> tareas;
    private Context context;
    private String usuarioEmail;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Tarea tarea);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TareaAdapter(List<Tarea> tareas, Context context, String usuarioEmail) {
        this.tareas = tareas;
        this.context = context;
        this.usuarioEmail = usuarioEmail;
    }

    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
        notifyDataSetChanged();
    }

    public List<Tarea> getListaTareas() {
        return tareas;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tarea = tareas.get(position);
        holder.tvTitulo.setText(tarea.getTitulo());
        holder.tvFecha.setText(tarea.getFecha());

        holder.tvUsuario.setText("Creado por: " + tarea.getUsuarioEmail());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date fechaTarea = sdf.parse(tarea.getFecha());
            Date fechaActual = new Date();

            long diff = fechaTarea.getTime() - fechaActual.getTime();
            long diasRestantes = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if (diasRestantes < 0) {
                holder.tvTitulo.setTextColor(Color.RED);
            } else if (diasRestantes <= 2) {
                holder.tvTitulo.setTextColor(Color.parseColor("#FFA500"));
            } else {
                holder.tvTitulo.setTextColor(Color.GREEN);
            }

        } catch (ParseException e) {
            holder.tvTitulo.setTextColor(Color.BLACK);
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(tarea);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (tareas != null) ? tareas.size() : 0;
    }

    public class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha, tvUsuario;

        public TareaViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
        }
    }

    public Tarea getTareaEnPosicion(int posicion) {
        if (tareas != null && posicion >= 0 && posicion < tareas.size()) {
            return tareas.get(posicion);
        }
        return null;
    }
}
