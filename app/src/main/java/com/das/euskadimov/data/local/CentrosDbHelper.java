package com.das.euskadimov.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.das.euskadimov.model.Centro;

import java.util.ArrayList;
import java.util.List;

public class CentrosDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "euskadimov_centros.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_CENTROS = "centros";

    public static final String COL_ID = "id";
    public static final String COL_UNIVERSIDAD = "universidad";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_CIUDAD = "ciudad";
    public static final String COL_DIRECCION = "direccion";
    public static final String COL_LATITUD = "latitud";
    public static final String COL_LONGITUD = "longitud";

    public CentrosDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String crearTablaCentros =
                "CREATE TABLE " + TABLE_CENTROS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        COL_UNIVERSIDAD + " TEXT NOT NULL, " +
                        COL_NOMBRE + " TEXT NOT NULL, " +
                        COL_CIUDAD + " TEXT NOT NULL, " +
                        COL_DIRECCION + " TEXT, " +
                        COL_LATITUD + " REAL NOT NULL, " +
                        COL_LONGITUD + " REAL NOT NULL" +
                        ")";

        db.execSQL(crearTablaCentros);

        insertarCentrosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CENTROS);
        onCreate(db);
    }

    private void insertarCentrosIniciales(SQLiteDatabase db) {
        /*
         * Coordenadas aproximadas para poder trabajar con OSM.
         * Más adelante se pueden afinar consultando el mapa.
         */

        // EHU
        insertarCentro(db, "EHU", "Escuela de Ingeniería de Bilbao - Edificio II",
                "Bilbao", "Plaza Ingeniero Torres Quevedo, Bilbao",
                43.2630, -2.9489);

        insertarCentro(db, "EHU", "Escuela de Ingeniería de Bilbao - Náutica",
                "Portugalete", "Portugalete",
                43.3205, -3.0203);

        insertarCentro(db, "EHU", "Facultad de Bellas Artes",
                "Leioa", "Barrio Sarriena, Leioa",
                43.3310, -2.9716);

        insertarCentro(db, "EHU", "Facultad de Ciencia y Tecnología",
                "Leioa", "Barrio Sarriena, Leioa",
                43.3317, -2.9704);

        insertarCentro(db, "EHU", "Facultad de Ciencias Sociales y Comunicación",
                "Leioa", "Barrio Sarriena, Leioa",
                43.3314, -2.9687);

        insertarCentro(db, "EHU", "Facultad de Derecho - Sección Bizkaia",
                "Leioa", "Barrio Sarriena, Leioa",
                43.3312, -2.9696);

        insertarCentro(db, "EHU", "Facultad de Economía y Empresa - Sarriko",
                "Bilbao", "Avenida Lehendakari Agirre, Bilbao",
                43.2713, -2.9575);

        insertarCentro(db, "EHU", "Facultad de Economía y Empresa - Elkano",
                "Bilbao", "Calle Elcano, Bilbao",
                43.2636, -2.9355);

        insertarCentro(db, "EHU", "Facultad de Educación de Bilbao",
                "Leioa", "Barrio Sarriena, Leioa",
                43.3306, -2.9708);

        insertarCentro(db, "EHU", "Facultad de Medicina y Enfermería",
                "Leioa", "Barrio Sarriena, Leioa",
                43.3319, -2.9725);

        insertarCentro(db, "EHU", "Unidad Docente de Medicina y Enfermería - Galdakao",
                "Galdakao", "Hospital de Galdakao",
                43.2317, -2.8423);

        insertarCentro(db, "EHU", "Unidad Docente de Medicina y Enfermería - Basurto",
                "Bilbao", "Hospital Universitario Basurto",
                43.2616, -2.9521);

        insertarCentro(db, "EHU", "Unidad Docente de Medicina y Enfermería - Cruces",
                "Barakaldo", "Hospital Universitario Cruces",
                43.2844, -2.9894);

        insertarCentro(db, "EHU", "Aulas de la Experiencia",
                "Bilbao", "Bilbao",
                43.2577, -2.9232);

        // Deusto
        insertarCentro(db, "Deusto", "Deusto Business School",
                "Bilbao", "Universidad de Deusto, Bilbao",
                43.2712, -2.9386);

        insertarCentro(db, "Deusto", "Facultad de Derecho",
                "Bilbao", "Universidad de Deusto, Bilbao",
                43.2712, -2.9386);

        insertarCentro(db, "Deusto", "Facultad de Ciencias Sociales y Humanas",
                "Bilbao", "Universidad de Deusto, Bilbao",
                43.2712, -2.9386);

        insertarCentro(db, "Deusto", "Facultad de Ingeniería",
                "Bilbao", "Universidad de Deusto, Bilbao",
                43.2712, -2.9386);

        insertarCentro(db, "Deusto", "Facultad de Educación y Deporte",
                "Bilbao", "Universidad de Deusto, Bilbao",
                43.2712, -2.9386);

        insertarCentro(db, "Deusto", "Facultad de Ciencias de la Salud",
                "Bilbao", "Universidad de Deusto, Bilbao",
                43.2712, -2.9386);

        insertarCentro(db, "Deusto", "Facultad de Ciencias Sociales y Comunicación",
                "Bilbao", "Universidad de Deusto, Bilbao",
                43.2712, -2.9386);

        // Mondragon Unibertsitatea
        insertarCentro(db, "Mondragon", "Bilbao Berrikuntza Faktoria - Facultad de Empresariales",
                "Bilbao", "Uribitarte 6, Bilbao",
                43.2674, -2.9276);

        insertarCentro(db, "Mondragon", "Bilbao Berrikuntza Faktoria - LEINN",
                "Bilbao", "Uribitarte 6, Bilbao",
                43.2674, -2.9276);

        insertarCentro(db, "Mondragon", "As Fabrik - Escuela Politécnica Superior",
                "Bilbao", "Zorrotzaurre, Bilbao",
                43.2808, -2.9659);

        insertarCentro(db, "Mondragon", "As Fabrik - Facultad de Humanidades y Ciencias de la Educación",
                "Bilbao", "Zorrotzaurre, Bilbao",
                43.2808, -2.9659);
    }

    private void insertarCentro(SQLiteDatabase db, String universidad, String nombre,
                                String ciudad, String direccion,
                                double latitud, double longitud) {
        ContentValues valores = new ContentValues();
        valores.put(COL_UNIVERSIDAD, universidad);
        valores.put(COL_NOMBRE, nombre);
        valores.put(COL_CIUDAD, ciudad);
        valores.put(COL_DIRECCION, direccion);
        valores.put(COL_LATITUD, latitud);
        valores.put(COL_LONGITUD, longitud);

        db.insert(TABLE_CENTROS, null, valores);
    }

    public List<Centro> obtenerCentrosPorUniversidad(String universidad) {
        List<Centro> centros = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String[] columnas = new String[]{
                COL_ID,
                COL_UNIVERSIDAD,
                COL_NOMBRE,
                COL_CIUDAD,
                COL_DIRECCION,
                COL_LATITUD,
                COL_LONGITUD
        };

        String seleccion = COL_UNIVERSIDAD + " = ?";
        String[] argumentos = new String[]{universidad};

        Cursor cursor = db.query(
                TABLE_CENTROS,
                columnas,
                seleccion,
                argumentos,
                null,
                null,
                COL_NOMBRE + " ASC"
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String uni = cursor.getString(1);
            String nombre = cursor.getString(2);
            String ciudad = cursor.getString(3);
            String direccion = cursor.getString(4);
            double latitud = cursor.getDouble(5);
            double longitud = cursor.getDouble(6);

            centros.add(new Centro(id, uni, nombre, ciudad, direccion, latitud, longitud));
        }

        cursor.close();
        db.close();

        return centros;
    }

    public Centro obtenerCentroPorId(int idCentro) {
        Centro centro = null;

        SQLiteDatabase db = getReadableDatabase();

        String[] columnas = new String[]{
                COL_ID,
                COL_UNIVERSIDAD,
                COL_NOMBRE,
                COL_CIUDAD,
                COL_DIRECCION,
                COL_LATITUD,
                COL_LONGITUD
        };

        String seleccion = COL_ID + " = ?";
        String[] argumentos = new String[]{String.valueOf(idCentro)};

        Cursor cursor = db.query(
                TABLE_CENTROS,
                columnas,
                seleccion,
                argumentos,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String uni = cursor.getString(1);
            String nombre = cursor.getString(2);
            String ciudad = cursor.getString(3);
            String direccion = cursor.getString(4);
            double latitud = cursor.getDouble(5);
            double longitud = cursor.getDouble(6);

            centro = new Centro(id, uni, nombre, ciudad, direccion, latitud, longitud);
        }

        cursor.close();
        db.close();

        return centro;
    }
}