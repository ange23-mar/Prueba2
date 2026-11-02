package com.example.repository.data

import com.example.repository.models.Usuario
import kotlinx.coroutines.flow.Flow

/** Fuente de datos de usuarios (login, registro de clientes y perfil). */
class UsuarioRepository(db: AppDatabase) {

    private val usuarioDao = db.usuarioDao()

    suspend fun login(email: String, password: String): Usuario? = usuarioDao.login(email, password)

    fun observar(id: Int): Flow<Usuario?> = usuarioDao.observarPorId(id)

    suspend fun obtener(id: Int): Usuario? = usuarioDao.getById(id)

    /** Lanza SQLiteConstraintException si el email ya existe. */
    suspend fun crear(usuario: Usuario): Long = usuarioDao.insert(usuario)

    suspend fun actualizar(usuario: Usuario) = usuarioDao.update(usuario)

    suspend fun actualizarFoto(id: Int, uri: String?) = usuarioDao.actualizarFoto(id, uri)
}
