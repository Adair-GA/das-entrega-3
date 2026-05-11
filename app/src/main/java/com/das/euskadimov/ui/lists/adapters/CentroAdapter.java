package com.das.euskadimov.ui.lists.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.das.euskadimov.model.Centro;
import com.das.euskadimov.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CentroAdapter extends RecyclerView.Adapter<CentroAdapter.CentroViewHolder> {

    private List<Centro> listaCentros;
    private OnCentroClickListener listener;
    private FirebaseFirestore db;
    private String uid;
    private Set<Integer> idsFavoritos = new HashSet<>();

    /**
     * En modo favoritos (true), al quitar la estrella el ítem desaparece de la lista.
     * En modo normal (false, lista de centros por universidad), solo cambia el icono.
     */
    private boolean modoFavoritos;

    /** Callback opcional para notificar a la Activity cuando la lista queda vacía. */
    public interface OnListaVaciaListener {
        void onListaVacia();
    }
    private OnListaVaciaListener onListaVaciaListener;

    public interface OnCentroClickListener {
        void onCentroClick(Centro centro);
    }

    /** Constructor para la lista de centros por universidad (modo normal). */
    public CentroAdapter(List<Centro> listaCentros, String uid, OnCentroClickListener listener) {
        this.listaCentros = listaCentros;
        this.listener = listener;
        this.uid = uid;
        this.modoFavoritos = false;
        this.db = FirebaseFirestore.getInstance();

        if (uid != null) {
            cargarFavoritosIniciales();
        }
    }

    /** Constructor para la lista de favoritos (modo favoritos). */
    public CentroAdapter(List<Centro> listaCentros, String uid,
                         boolean modoFavoritos,
                         OnCentroClickListener listener,
                         OnListaVaciaListener onListaVaciaListener) {
        this.listaCentros = listaCentros;
        this.listener = listener;
        this.uid = uid;
        this.modoFavoritos = modoFavoritos;
        this.onListaVaciaListener = onListaVaciaListener;
        this.db = FirebaseFirestore.getInstance();

        if (modoFavoritos) {
            // En modo favoritos todos los ítems ya son favoritos, los marcamos todos
            for (Centro c : listaCentros) {
                idsFavoritos.add(c.getId());
            }
        } else if (uid != null) {
            cargarFavoritosIniciales();
        }
    }

    private void cargarFavoritosIniciales() {
        db.collection("usuarios").document(uid)
                .collection("favoritos")
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (var doc : snapshots.getDocuments()) {
                        try {
                            idsFavoritos.add(Integer.parseInt(doc.getId()));
                        } catch (NumberFormatException ignored) {}
                    }
                    notifyDataSetChanged();
                });
    }

    @NonNull
    @Override
    public CentroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_centro, parent, false);
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

        boolean esFavorito = idsFavoritos.contains(centro.getId());
        setEstrellaEstado(holder.btnFavorito, esFavorito, false);

        holder.btnFavorito.setOnClickListener(v -> {
            if (uid == null) {
                Toast.makeText(v.getContext(),
                        "Inicia sesión para guardar favoritos", Toast.LENGTH_SHORT).show();
                return;
            }
            toggleFavorito(centro, holder.btnFavorito, holder.getAdapterPosition());
        });

        holder.itemView.setOnClickListener(v -> listener.onCentroClick(centro));
    }

    private void setEstrellaEstado(ImageButton btn, boolean favorito, boolean animar) {
        if (favorito) {
            btn.setImageResource(R.drawable.ic_star_filled);
            btn.setColorFilter(0xFFFFB300);
        } else {
            btn.setImageResource(R.drawable.ic_star_outline);
            btn.setColorFilter(0xFF9E9E9E);
        }

        if (animar) {
            btn.setScaleX(0.5f);
            btn.setScaleY(0.5f);
            btn.animate().scaleX(1f).scaleY(1f).setDuration(220).start();
        }
    }

    private void toggleFavorito(Centro centro, ImageButton btnFavorito, int position) {
        boolean eraFavorito = idsFavoritos.contains(centro.getId());
        String docId = String.valueOf(centro.getId());

        // Actualizar UI al instante (optimistic update)
        boolean nuevoEstado = !eraFavorito;

        if (modoFavoritos && eraFavorito) {
            // En modo favoritos, quitar estrella = eliminar ítem de la lista al momento
            idsFavoritos.remove(centro.getId());
            listaCentros.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listaCentros.size());

            if (listaCentros.isEmpty() && onListaVaciaListener != null) {
                onListaVaciaListener.onListaVacia();
            }

            // Sincronizar con Firestore y revertir si falla
            db.collection("usuarios").document(uid)
                    .collection("favoritos").document(docId)
                    .delete()
                    .addOnSuccessListener(unused ->
                            Toast.makeText(btnFavorito.getContext(),
                                    "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e -> {
                        // Revertir: volver a insertar el ítem
                        idsFavoritos.add(centro.getId());
                        listaCentros.add(position, centro);
                        notifyItemInserted(position);
                        Toast.makeText(btnFavorito.getContext(),
                                "Error al quitar favorito", Toast.LENGTH_SHORT).show();
                    });

        } else {
            // Modo normal: solo cambiar icono
            if (nuevoEstado) {
                idsFavoritos.add(centro.getId());
            } else {
                idsFavoritos.remove(centro.getId());
            }
            setEstrellaEstado(btnFavorito, nuevoEstado, true);

            if (eraFavorito) {
                db.collection("usuarios").document(uid)
                        .collection("favoritos").document(docId)
                        .delete()
                        .addOnSuccessListener(unused ->
                                Toast.makeText(btnFavorito.getContext(),
                                        "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e -> {
                            idsFavoritos.add(centro.getId());
                            setEstrellaEstado(btnFavorito, true, false);
                            Toast.makeText(btnFavorito.getContext(),
                                    "Error al quitar favorito", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Map<String, Object> datos = new HashMap<>();
                datos.put("id", centro.getId());
                datos.put("universidad", centro.getUniversidad());
                datos.put("nombre", centro.getNombre());
                datos.put("ciudad", centro.getCiudad());
                datos.put("direccion", centro.getDireccion());
                datos.put("latitud", centro.getLatitud());
                datos.put("longitud", centro.getLongitud());

                db.collection("usuarios").document(uid)
                        .collection("favoritos").document(docId)
                        .set(datos, SetOptions.merge())
                        .addOnSuccessListener(unused ->
                                Toast.makeText(btnFavorito.getContext(),
                                        "Añadido a favoritos ⭐", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e -> {
                            idsFavoritos.remove(centro.getId());
                            setEstrellaEstado(btnFavorito, false, false);
                            Toast.makeText(btnFavorito.getContext(),
                                    "Error al guardar favorito", Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaCentros.size();
    }

    public static class CentroViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCiudad;
        ImageButton btnFavorito;

        public CentroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCentro);
            tvCiudad = itemView.findViewById(R.id.tvCiudadCentro);
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
        }
    }
}