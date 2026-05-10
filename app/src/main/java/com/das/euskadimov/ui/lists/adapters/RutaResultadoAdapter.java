package com.das.euskadimov.ui.lists.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.das.euskadimov.R;
import com.das.euskadimov.RutaResultado;
import com.das.euskadimov.TramoRuta;

import java.util.List;

public class RutaResultadoAdapter extends RecyclerView.Adapter<RutaResultadoAdapter.RutaViewHolder> {

    private List<RutaResultado> rutas;
    private OnRutaClickListener listener;

    public interface OnRutaClickListener {
        void onRutaClick(RutaResultado ruta);
    }

    public RutaResultadoAdapter(List<RutaResultado> rutas, OnRutaClickListener listener) {
        this.rutas = rutas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RutaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ruta_resultado, parent, false);
        return new RutaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutaViewHolder holder, int position) {
        RutaResultado ruta = rutas.get(position);

        holder.tvNumeroRuta.setText((position + 1) + ".");
        holder.tvResumenRuta.setText(ruta.getHoraInicio() + "   " + ruta.getResumen() + "   " + ruta.getHoraFin());
        holder.tvCosteRuta.setText("Coste generalizado: " + ruta.getCosteGeneralizado());

        if (ruta.isDesplegada()) {
            holder.layoutDetalleRuta.setVisibility(View.VISIBLE);
            holder.tvFlechaRuta.setText("▲");
        } else {
            holder.layoutDetalleRuta.setVisibility(View.GONE);
            holder.tvFlechaRuta.setText("▼");
        }

        holder.containerTramos.removeAllViews();

        for (TramoRuta tramo : ruta.getTramos()) {
            View tramoView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_tramo_ruta, holder.containerTramos, false);

            TextView tvHorario = tramoView.findViewById(R.id.tvTramoHorario);
            TextView tvDistancia = tramoView.findViewById(R.id.tvTramoDistancia);
            TextView tvTipo = tramoView.findViewById(R.id.tvTramoTipo);
            TextView tvDescripcion = tramoView.findViewById(R.id.tvTramoDescripcion);

            tvHorario.setText(tramo.getHoraInicio() + " - " + tramo.getHoraFin());
            tvDistancia.setText(tramo.getDistancia() + ", " + tramo.getDuracion() + ", " + tramo.getCoste());
            tvTipo.setText(tramo.getTipo());
            tvDescripcion.setText(tramo.getDescripcion());

            holder.containerTramos.addView(tramoView);
        }

        holder.headerRuta.setOnClickListener(v -> {
            ruta.setDesplegada(!ruta.isDesplegada());
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.tvFlechaRuta.setOnClickListener(v -> {
            ruta.setDesplegada(!ruta.isDesplegada());
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.btnVerRutaMapa.setOnClickListener(v -> listener.onRutaClick(ruta));
    }

    @Override
    public int getItemCount() {
        return rutas.size();
    }

    public static class RutaViewHolder extends RecyclerView.ViewHolder {

        LinearLayout headerRuta;
        LinearLayout layoutDetalleRuta;
        LinearLayout containerTramos;
        TextView tvNumeroRuta;
        TextView tvResumenRuta;
        TextView tvFlechaRuta;
        TextView tvCosteRuta;
        Button btnVerRutaMapa;

        public RutaViewHolder(@NonNull View itemView) {
            super(itemView);

            headerRuta = itemView.findViewById(R.id.headerRuta);
            layoutDetalleRuta = itemView.findViewById(R.id.layoutDetalleRuta);
            containerTramos = itemView.findViewById(R.id.containerTramos);
            tvNumeroRuta = itemView.findViewById(R.id.tvNumeroRuta);
            tvResumenRuta = itemView.findViewById(R.id.tvResumenRuta);
            tvFlechaRuta = itemView.findViewById(R.id.tvFlechaRuta);
            tvCosteRuta = itemView.findViewById(R.id.tvCosteRuta);
            btnVerRutaMapa = itemView.findViewById(R.id.btnVerRutaMapa);
        }
    }
}