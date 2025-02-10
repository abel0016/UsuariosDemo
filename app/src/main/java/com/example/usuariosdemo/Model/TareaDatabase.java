package com.example.usuariosdemo.Model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Tarea.class}, version = 3, exportSchema = false)
public abstract class TareaDatabase extends RoomDatabase {
    private static volatile TareaDatabase INSTANCE;

    public abstract TareaDao tareaDao();

    public static TareaDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TareaDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TareaDatabase.class, "tarea_database")
                            .addMigrations(MIGRACION_2_3) // AGREGAMOS LA MIGRACIÓN
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // MIGRACIÓN DE VERSION 1 A VERSION 2 (AÑADIR imagen_uri)
    static final Migration MIGRACION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE tareas ADD COLUMN imagen_uri TEXT");
        }
    };

    static final Migration MIGRACION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE tareas ADD COLUMN usuario_id TEXT NOT NULL DEFAULT ''");
        }
    };

}
