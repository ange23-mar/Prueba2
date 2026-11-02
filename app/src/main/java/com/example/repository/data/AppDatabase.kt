package com.example.repository.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.repository.models.Acceso
import com.example.repository.models.MovimientoCliente
import com.example.repository.models.RegistroConteo
import com.example.repository.models.RegistroFlujo
import com.example.repository.models.Usuario

@Database(
    entities = [
        Acceso::class,
        RegistroConteo::class,
        RegistroFlujo::class,
        Usuario::class,
        MovimientoCliente::class
    ],
    version = 3, // v2: RegistroFlujo.fotoUri | v3: usuarios, roles, movimiento_cliente, usuarioId en conteo/flujo
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accesoDao(): AccesoDao
    abstract fun registroConteoDao(): RegistroConteoDao
    abstract fun registroFlujoDao(): RegistroFlujoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun movimientoClienteDao(): MovimientoClienteDao

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

        // Datos base. INSERT OR IGNORE: se ejecuta en cada apertura sin duplicar
        // (también se recargan después de una migración destructiva).
        private val seedCallback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Los 4 accesos existen siempre. Nombres y ubicaciones son de ejemplo.
                db.execSQL(
                    "INSERT OR IGNORE INTO accesos (id, nombreAcceso, ubicacionDescripcion) VALUES " +
                            "(1, 'Acceso 1', 'Mall - entrada principal'), " +
                            "(2, 'Acceso 2', 'Mall - entrada lateral'), " +
                            "(3, 'Acceso 3', 'Tienda vecina (mejoramiento del hogar)'), " +
                            "(4, 'Acceso 4', 'Estacionamiento')"
                )
                // Usuarios de prueba (el email es único, por eso OR IGNORE no los duplica)
                db.execSQL(
                    "INSERT OR IGNORE INTO usuarios " +
                            "(nombre, email, password, rol, tipoCliente, rangoEtario, fotoPerfilUri) VALUES " +
                            "('Administrador', 'admin@rinconsur.cl', 'admin123', 'ADMINISTRADOR', NULL, 'SIN_DATO', NULL), " +
                            "('Cliente Demo', 'cliente@rinconsur.cl', 'cliente123', 'CLIENTE', 'COMPRA', 'ADULTO_30_44', NULL)"
                )
            }
        }
    }
}
