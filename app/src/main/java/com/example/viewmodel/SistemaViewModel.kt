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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//contendor agrupa subclases
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
abstract class SistemaViewModel: ViewModel() {
    private val _estado = MutableStateFlow(SistemaProyec())
    val estado: StateFlow<SistemaProyec> = _estado.asStateFlow()
// Acceso
fun onIdAccesoChange(valor: String) {
    _estado.update {
        it.copy(
            acceso = it.acceso.copy(idAcceso = valor.toIntOrNull() ?: 0),
            accesoErrores = it.accesoErrores.copy(idAcceso = null)
        )
    }
}
    // Código corregido para guardar el nombre de acceso:
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
//validacion
    fun validarFormularioAcceso(): Boolean {

        val accesoActual = _estado.value.acceso

        val erroresCalculados = AccesoErrores(
            idAcceso = (if (accesoActual.idAcceso <= 0) "El ID debe ser mayor a 0" else null) as Int?,
            nombreAcceso = if (accesoActual.nombreAcceso.isBlank()) "El Nombre no puede ir en blanco" else null,
            ubicacionDescripcion = if (accesoActual.ubicacionDescripcion.isBlank()) "debe agregar Ubicacion" else null
        )

        //  genera mensaje dentro de AccesoErrores
        val hayErrores = listOfNotNull(
            erroresCalculados.idAcceso,
            erroresCalculados.nombreAcceso,
            erroresCalculados.ubicacionDescripcion
        ).isNotEmpty()

        // 4. Guarda
        _estado.update {
            it.copy(accesoErrores = erroresCalculados)
        }


        return !hayErrores
    }

//registroConteo
fun onIdRegistroChange(valor: String) {
    _estado.update {
        it.copy(
            registroConteo = it.registroConteo.copy(idRegistro = valor.toIntOrNull() ?: 0),
            registroErrores = it.registroErrores.copy(idRegistro = null)
        )
    }
}


    fun onIdAccesoRegistroChange(valor: String) {
        _estado.update {
            it.copy(
                registroConteo = it.registroConteo.copy(idAcceso = valor.toIntOrNull() ?: 0),
                registroErrores = it.registroErrores.copy(idAcceso = null)
            )
        }
    }

    // Para fechaHora (String)
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

    // Para rangoEtario (String)
    fun onRangoEtarioChange(valor: String) {
        _estado.update {
            it.copy(
                registroConteo = it.registroConteo.copy(rangoEtario = valor),
                registroErrores = it.registroErrores.copy(rangoEtario = null)
            )
        }
    }

    // Para genero (String)
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


        val errores = RegistroErrores(
            idRegistro = (if (registroActual.idRegistro <= 0) "ID de registro inválido" else null) as Int?,
            idAcceso = (if (registroActual.idAcceso <= 0) "Selecciona un acceso válido" else null) as Int?,
            fechaHora = if (registroActual.fechaHora.isBlank()) "La fecha y hora es obligatoria" else null,
            tipoCliente = if (registroActual.tipoCliente.isBlank()) "Selecciona el tipo de cliente" else null,
            rangoEtario = if (registroActual.rangoEtario.isBlank()) "Selecciona el rango etario" else null,
            genero = if (registroActual.genero.isBlank()) "Selecciona el género" else null
        )

        //  guardó un mensaje
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

    //transaccionVenta
    fun onIdTransaccionChange(valor: String) {
        _estado.update {
            it.copy(
                transaccionVenta = it.transaccionVenta.copy(idTransaccion = valor.toIntOrNull() ?: 0),
                errorTransaccion = it.errorTransaccion.copy(idTransaccion = null)
            )
        }
    }

    // Para idAccesoIngreso (Int)
    fun onIdAccesoIngresoChange(valor: String) {
        _estado.update {
            it.copy(
                transaccionVenta = it.transaccionVenta.copy(idAccesoIngreso = valor.toIntOrNull() ?: 0),
                errorTransaccion = it.errorTransaccion.copy(idAccesoIngreso = null)
            )
        }
    }

    // Para idUsuario
    fun onTransaccionIdUsuarioChange(valor: String) {
        _estado.update {
            it.copy(
                transaccionVenta = it.transaccionVenta.copy(idUsuario = valor.toIntOrNull() ?: 0),
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

    // Para montoTotal (Double)
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

        // La instancia de la clase sigue siendo ErrorTransaccionVenta(...)
        val errores = ErrorTransaccionVenta(
            idTransaccion = (if (transaccionActual.idTransaccion <= 0) "ID de transacción inválido" else null) as Int?,
            idAccesoIngreso = (if (transaccionActual.idAccesoIngreso <= 0) "Selecciona un acceso válido" else null) as Int?,
            idUsuario = (if (transaccionActual.idUsuario <= 0) "Selecciona un usuario válido" else null) as Int?,
            fechaHora = if (transaccionActual.fechaHora.isBlank()) "La fecha y hora son obligatorias" else null,
            montoTotal = (if (transaccionActual.montoTotal <= 0.0) "El monto debe ser mayor a 0" else null) as Double?
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
              usuario = it.usuario.copy(idUsuario = valor.toIntOrNull() ?: 0),
              errorUsuario = it.errorUsuario.copy(idUsuario = null)
          )
      }
  }



    // Para nombre
    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                usuario = it.usuario.copy(nombre = valor),
                errorUsuario = it.errorUsuario.copy(nombre = null)
            )
        }
    }

    // Para correo (String)
    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                usuario = it.usuario.copy(correo = valor),
                errorUsuario = it.errorUsuario.copy(correo = null)
            )
        }
    }

    // Para clave (String)
    fun onClaveChange(valor: String) {
        _estado.update {
            it.copy(
                usuario = it.usuario.copy(clave = valor),
                errorUsuario = it.errorUsuario.copy(clave = null)
            )
        }
    }

    fun validarFormularioUsuario(): Boolean {
        // 1. Leemos los datos actuales de usuario
        val usuarioActual = _estado.value.usuario

        // 2. Evaluamos los campos y creamos el objeto con los errores
        val errores = ErrorUsuario(
            idUsuario = (if (usuarioActual.idUsuario <= 0) "ID de usuario inválido" else null) as Int?,
            nombre = if (usuarioActual.nombre.isBlank()) "El nombre es obligatorio" else null,
            correo = when {
                usuarioActual.correo.isBlank() -> "El correo es obligatorio"
                !android.util.Patterns.EMAIL_ADDRESS.matcher(usuarioActual.correo).matches() -> "Formato de correo inválido"
                else -> null
            }
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





