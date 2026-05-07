package com.das.euskadimov.ui.lists.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.das.euskadimov.Centro;
import com.das.euskadimov.R;

import java.util.List;

public class CentroAdapter extends RecyclerView.Adapter<CentroAdapter.CentroViewHolder> {

    private List<Centro> listaCentros;
    private OnCentroClickListener listener;

    // Interfaz para manejar el clic en cada cuadrito
    public interface OnCentroClickListener {
        void onCentroClick(Centro centro);
    }

    public CentroAdapter(List<Centro> listaCentros, OnCentroClickListener listener) {
        this.listaCentros = listaCentros;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CentroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_centro, parent, false);
        return new CentroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CentroViewHolder holder, int position) {
        Centro centro = listaCentros.get(position);
        holder.tvNombre.setText(centro.getNombre());
        String detalle = centro.getCiudad();

        if (centro.getDireccion() != null && !centro.getDireccion().isEmpty()) {
            detalle = centro.getCiudad() + " - " + centro.getDireccion();
        }

        holder.tvCiudad.setText(detalle);

        holder.itemView.setOnClickListener(v -> listener.onCentroClick(centro));
    }

    @Override
    public int getItemCount() {
        return listaCentros.size();
    }

    public static class CentroViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCiudad;

        public CentroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCentro);
            tvCiudad = itemView.findViewById(R.id.tvCiudadCentro);
        }
    }
}
