package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.Acceso
import com.example.model.RegistroConteo
import com.example.model.RegistroErrores
import com.example.model.TransaccionVenta
import com.example.model.AccesoErrores
import com.example.model.ErrorTransaccionVenta
import com.example.model.ErrorUsuario
import com.example.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class SistemaProyec(
    val acceso: Acceso = Acceso(),
    val accesoErrores: AccesoErrores = AccesoErrores(),
    val registroConteo: RegistroConteo = RegistroConteo(),
    val registroErrores: RegistroErrores = RegistroErrores(),
    val transaccionVenta: TransaccionVenta = TransaccionVenta(),
    val errorTransaccion: ErrorTransaccionVenta = ErrorTransaccionVenta(),
    val usuario: Usuario = Usuario(),
    val errorUsuario: ErrorUsuario = ErrorUsuario()
)

abstract class SistemaViewModel : ViewModel() {
    private val _estado = MutableStateFlow(value = SistemaProyec())
    val estado: StateFlow<SistemaProyec> = _estado

 //acceso
    fun onIdAccesoChange(valor: String) {
        _estado.update {
            it.copy(
                acceso = it.acceso.copy(idAcceso = valor.toIntOrNull() ?: 0),
                accesoErrores = it.accesoErrores.copy(idAcceso = null)
            )
        }
    }

    fun onNombreAccesoChange(valor: String) {
        _estado.update {
            it.copy(
                acceso = it.acceso.copy(nombreAcceso = valor),
                accesoErrores = it.accesoErrores.copy(nombreAcceso = null)
            )
        }
    }

    fun onUbicacionDescripcionChange(valor: String) {
        _estado.update {
            it.copy(
                acceso = it.acceso.copy(ubicacionDescripcion = valor),
                accesoErrores = it.accesoErrores.copy(ubicacionDescripcion = null)
            )
        }
    }

    fun validarFormularioAcceso(): Boolean {
        val accesoActual = _estado.value.acceso

        val erroresCalculados = AccesoErrores(
            idAcceso = if (accesoActual.idAcceso <= 0) 1 else null,
            nombreAcceso = if (accesoActual.nombreAcceso.isBlank()) "El Nombre no puede ir en blanco" else null,
            ubicacionDescripcion = if (accesoActual.ubicacionDescripcion.isBlank()) "Debe agregar Ubicación" else null
        )

        val hayErrores = listOfNotNull(
            erroresCalculados.idAcceso,
            erroresCalculados.nombreAcceso,
            erroresCalculados.ubicacionDescripcion
        ).isNotEmpty()

        _estado.update {
            it.copy(accesoErrores = erroresCalculados)
        }

        return !hayErrores
    }

//registro
    fun onIdRegistroChange(valor: String) {
        _estado.update {
            it.copy(
                registroConteo = it.registroConteo.copy(idRegistro = valor),
                registroErrores = it.registroErrores.copy(idRegistro = null)
            )
        }
    }

    fun onIdAccesoRegistroChange(valor: String) {
        _estado.update {
            it.copy(
                registroConteo = it.registroConteo.copy(idAcceso = valor),
                registroErrores = it.registroErrores.copy(idAcceso = null)
            )
        }
    }

    fun onFechaHoraChange(valor: String) {
        _estado.update {
            it.copy(
                registroConteo = it.registroConteo.copy(fechaHora = valor),
                registroErrores = it.registroErrores.copy(fechaHora = null)
            )
        }
    }

    fun onTipoClienteChange(valor: String) {
        _estado.update {
            it.copy(
                registroConteo = it.registroConteo.copy(tipoCliente = valor),
                registroErrores = it.registroErrores.copy(tipoCliente = null)
            )
        }
    }

    fun onRangoEtarioChange(valor: String) {
        _estado.update {
            it.copy(
                registroConteo = it.registroConteo.copy(rangoEtario = valor),
                registroErrores = it.registroErrores.copy(rangoEtario = null)
            )
        }
    }

    fun onGeneroChange(valor: String) {
        _estado.update {
            it.copy(
                registroConteo = it.registroConteo.copy(genero = valor),
                registroErrores = it.registroErrores.copy(genero = null)
            )
        }
    }

    fun validarFormularioRegistro(): Boolean {
        val registroActual = _estado.value.registroConteo

        val idRegInt = registroActual.idRegistro.toIntOrNull() ?: 0
        val idAccInt = registroActual.idAcceso.toIntOrNull() ?: 0

        val errores = RegistroErrores(
            idRegistro = if (idRegInt <= 0) 1 else null,
            idAcceso = if (idAccInt <= 0) 1 else null,
            fechaHora = if (registroActual.fechaHora.isBlank()) "La fecha y hora es obligatoria" else null,
            tipoCliente = if (registroActual.tipoCliente.isBlank()) "Indica el tipo de cliente" else null,
            rangoEtario = if (registroActual.rangoEtario.isBlank()) "El rango etario debe registrarse" else null,
            genero = if (registroActual.genero.isBlank()) "Indica tu género" else null
        )

        val hayErrores = listOfNotNull(
            errores.idRegistro,
            errores.idAcceso,
            errores.fechaHora,
            errores.tipoCliente,
            errores.rangoEtario,
            errores.genero
        ).isNotEmpty()

        _estado.update {
            it.copy(registroErrores = errores)
        }

        return !hayErrores
    }

  //transaccion
    fun onIdTransaccionChange(valor: String) {
        _estado.update {
            it.copy(
                transaccionVenta = it.transaccionVenta.copy(idTransaccion = valor.toIntOrNull() ?: 0),
                errorTransaccion = it.errorTransaccion.copy(idTransaccion = null)
            )
        }
    }

    fun onIdAccesoIngresoChange(valor: String) {
        _estado.update {
            it.copy(
                transaccionVenta = it.transaccionVenta.copy(idAccesoIngreso = valor.toIntOrNull() ?: 0),
                errorTransaccion = it.errorTransaccion.copy(idAccesoIngreso = null)
            )
        }
    }

    fun onTransaccionIdUsuarioChange(valor: String) {
        _estado.update {
            it.copy(
                transaccionVenta = it.transaccionVenta.copy(idUsuario = valor),
                errorTransaccion = it.errorTransaccion.copy(idUsuario = null)
            )
        }
    }

    fun onFechaHoraTransaccionChange(valor: String) {
        _estado.update {
            it.copy(
                transaccionVenta = it.transaccionVenta.copy(fechaHora = valor),
                errorTransaccion = it.errorTransaccion.copy(fechaHora = null)
            )
        }
    }

    fun onMontoTotalChange(valor: String) {
        _estado.update {
            it.copy(
                transaccionVenta = it.transaccionVenta.copy(montoTotal = valor.toDoubleOrNull() ?: 0.0),
                errorTransaccion = it.errorTransaccion.copy(montoTotal = null)
            )
        }
    }

    fun validarFormularioTransaccion(): Boolean {
        val transaccionActual = _estado.value.transaccionVenta

        val errores = ErrorTransaccionVenta(
            idTransaccion = if (transaccionActual.idTransaccion <= 0) 1 else null,
            idAccesoIngreso = if (transaccionActual.idAccesoIngreso <= 0) 1 else null,
            idUsuario = if (transaccionActual.idUsuario.isBlank()) "Selecciona un usuario válido" else null,
            fechaHora = if (transaccionActual.fechaHora.isBlank()) "La fecha y hora son obligatorias" else null,
            montoTotal = if (transaccionActual.montoTotal <= 0.0) 1.0 else null
        )

        val hayErrores = listOfNotNull(
            errores.idTransaccion,
            errores.idAccesoIngreso,
            errores.idUsuario,
            errores.fechaHora,
            errores.montoTotal
        ).isNotEmpty()

        _estado.update {
            it.copy(errorTransaccion = errores)
        }

        return !hayErrores
    }

//usuario
    fun onUsuarioIdUsuarioChange(valor: String) {
        _estado.update {
            it.copy(
                usuario = it.usuario.copy(idUsuario = valor),
                errorUsuario = it.errorUsuario.copy(idUsuario = null)
            )
        }
    }

    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                usuario = it.usuario.copy(nombre = valor),
                errorUsuario = it.errorUsuario.copy(nombre = null)
            )
        }
    }

    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                usuario = it.usuario.copy(correo = valor),
                errorUsuario = it.errorUsuario.copy(correo = null)
            )
        }
    }

    fun onClaveChange(valor: String) {
        _estado.update {
            it.copy(
                usuario = it.usuario.copy(clave = valor),
                errorUsuario = it.errorUsuario.copy(clave = null)
            )
        }
    }

    fun validarFormularioUsuario(): Boolean {
        val usuarioActual = _estado.value.usuario
        val idUserInt = usuarioActual.idUsuario.toIntOrNull() ?: 0

        val errores = ErrorUsuario(
            idUsuario = if (idUserInt <= 0) "ID de usuario inválido" else null,
            nombre = if (usuarioActual.nombre.isBlank()) "El nombre es obligatorio" else null,
            correo = if (!usuarioActual.correo.contains("@")) "Correo inválido" else null
        )

        val hayErrores = listOfNotNull(
            errores.idUsuario,
            errores.nombre,
            errores.correo
        ).isNotEmpty()

        _estado.update {
            it.copy(errorUsuario = errores)
        }

        return !hayErrores
    }
}