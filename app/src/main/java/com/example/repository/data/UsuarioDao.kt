package com.example.repository.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.repository.models.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    // LOGIN
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): Usuario?

    // READ reactivo: el perfil y el menú se actualizan solos (ej. al cambiar la foto)
    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun observarPorId(id: Int): Flow<Usuario?>

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getById(id: Int): Usuario?

    // CREATE (ABORT: el email es único)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(usuario: Usuario): Long

    // UPDATE
    @Update
    suspend fun update(usuario: Usuario)

    @Query("UPDATE usuarios SET fotoPerfilUri = :uri WHERE id = :id")
    suspend fun actualizarFoto(id: Int, uri: String?)
}
