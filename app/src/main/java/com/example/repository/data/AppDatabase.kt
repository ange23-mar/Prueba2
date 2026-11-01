package com.example.repository.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.repository.models.Acceso
import com.example.repository.models.RegistroConteo
import com.example.repository.models.RegistroFlujo

@Database(
    entities = [Acceso::class, RegistroConteo::class, RegistroFlujo::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accesoDao(): AccesoDao
    abstract fun registroConteoDao(): RegistroConteoDao
    abstract fun registroFlujoDao(): RegistroFlujoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rincon_sur_database"
                )
                    // Proyecto académico (MVP): al cambiar la versión se recrea la BD
                    .fallbackToDestructiveMigration()
                    .addCallback(seedCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Los 4 accesos existen siempre (INSERT OR IGNORE evita duplicarlos).
        // Nombres y ubicaciones son de ejemplo: ajústalos al plano entregado por la tienda.
        private val seedCallback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL(
                    "INSERT OR IGNORE INTO accesos (id, nombre, ubicacion) VALUES " +
                            "(1, 'Acceso 1', 'Mall - entrada principal'), " +
                            "(2, 'Acceso 2', 'Mall - entrada lateral'), " +
                            "(3, 'Acceso 3', 'Tienda vecina (mejoramiento del hogar)'), " +
                            "(4, 'Acceso 4', 'Estacionamiento')"
                )
            }
        }
    }
}
