
package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.Acceso
import com.example.model.AccesoErrores
import com.example.model.ErrorTransaccionVenta
import com.example.model.ErrorUsuario
import com.example.model.RegistroConteo
import com.example.model.RegistroErrores
import com.example.model.TransaccionVenta
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
    val usuario: Usuario = Usuario()
)

class SistemaViewModel : ViewModel() {

    private val _estado = MutableStateFlow(SistemaProyec())
    val estado: StateFlow<SistemaProyec> = _estado

    // --------------------------------------------------
    // ACCESO
    // --------------------------------------------------

    fun onIdAccesoChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                acceso = estado.acceso.copy(
                    idAcceso = valor.toIntOrNull() ?: 0
                ),
                accesoErrores = estado.accesoErrores.copy(
                    idAcceso = null
                )
            )
        }
    }

    fun onNombreAccesoChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                acceso = estado.acceso.copy(nombreAcceso = valor),
                accesoErrores = estado.accesoErrores.copy(
                    nombreAcceso = null
                )
            )
        }
    }

    fun onUbicacionDescripcionChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                acceso = estado.acceso.copy(
                    ubicacionDescripcion = valor
                ),
                accesoErrores = estado.accesoErrores.copy(
                    ubicacionDescripcion = null
                )
            )
        }
    }

    fun validarFormularioAcceso(): Boolean {
        val accesoActual = _estado.value.acceso

        val errores = AccesoErrores(
            idAcceso = if (accesoActual.idAcceso <= 0) 1 else null,
            nombreAcceso = if (
                accesoActual.nombreAcceso.isBlank()
            ) {
                "El nombre no puede ir en blanco"
            } else {
                null
            },
            ubicacionDescripcion = if (
                accesoActual.ubicacionDescripcion.isBlank()
            ) {
                "Debe agregar ubicación"
            } else {
                null
            }
        )

        _estado.update { estado ->
            estado.copy(accesoErrores = errores)
        }

        return listOfNotNull(
            errores.idAcceso,
            errores.nombreAcceso,
            errores.ubicacionDescripcion
        ).isEmpty()
    }

    // --------------------------------------------------
    // REGISTRO DE CONTEO
    // --------------------------------------------------

    fun onIdRegistroChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                registroConteo = estado.registroConteo.copy(
                    idRegistro = valor
                ),
                registroErrores = estado.registroErrores.copy(
                    idRegistro = null
                )
            )
        }
    }

    fun onIdAccesoRegistroChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                registroConteo = estado.registroConteo.copy(
                    idAcceso = valor
                ),
                registroErrores = estado.registroErrores.copy(
                    idAcceso = null
                )
            )
        }
    }

    fun onFechaHoraChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                registroConteo = estado.registroConteo.copy(
                    fechaHora = valor
                ),
                registroErrores = estado.registroErrores.copy(
                    fechaHora = null
                )
            )
        }
    }

    fun onTipoClienteChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                registroConteo = estado.registroConteo.copy(
                    tipoCliente = valor
                ),
                registroErrores = estado.registroErrores.copy(
                    tipoCliente = null
                )
            )
        }
    }

    fun onRangoEtarioChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                registroConteo = estado.registroConteo.copy(
                    rangoEtario = valor
                ),
                registroErrores = estado.registroErrores.copy(
                    rangoEtario = null
                )
            )
        }
    }

    fun onGeneroChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                registroConteo = estado.registroConteo.copy(
                    genero = valor
                ),
                registroErrores = estado.registroErrores.copy(
                    genero = null
                )
            )
        }
    }

    fun validarFormularioRegistro(): Boolean {
        val registro = _estado.value.registroConteo

        val idRegInt = registro.idRegistro.toIntOrNull() ?: 0
        val idAccInt = registro.idAcceso.toIntOrNull() ?: 0

        val errores = RegistroErrores(
            idRegistro = if (idRegInt <= 0) 1 else null,
            idAcceso = if (idAccInt <= 0) 1 else null,
            fechaHora = if (registro.fechaHora.isBlank()) {
                "La fecha y hora es obligatoria"
            } else {
                null
            },
            tipoCliente = if (registro.tipoCliente.isBlank()) {
                "Indica el tipo de cliente"
            } else {
                null
            },
            rangoEtario = if (registro.rangoEtario.isBlank()) {
                "El rango etario debe registrarse"
            } else {
                null
            },
            genero = if (registro.genero.isBlank()) {
                "Indica el género"
            } else {
                null
            }
        )

        _estado.update { estado ->
            estado.copy(registroErrores = errores)
        }

        return listOfNotNull(
            errores.idRegistro,
            errores.idAcceso,
            errores.fechaHora,
            errores.tipoCliente,
            errores.rangoEtario,
            errores.genero
        ).isEmpty()
    }

    // --------------------------------------------------
    // TRANSACCIÓN DE VENTA
    // --------------------------------------------------

    fun onIdTransaccionChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                transaccionVenta = estado.transaccionVenta.copy(
                    idTransaccion = valor.toIntOrNull() ?: 0
                ),
                errorTransaccion = estado.errorTransaccion.copy(
                    idTransaccion = null
                )
            )
        }
    }

    fun onIdAccesoIngresoChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                transaccionVenta = estado.transaccionVenta.copy(
                    idAccesoIngreso = valor.toIntOrNull() ?: 0
                ),
                errorTransaccion = estado.errorTransaccion.copy(
                    idAccesoIngreso = null
                )
            )
        }
    }

    fun onTransaccionIdUsuarioChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                transaccionVenta = estado.transaccionVenta.copy(
                    idUsuario = valor
                ),
                errorTransaccion = estado.errorTransaccion.copy(
                    idUsuario = null
                )
            )
        }
    }

    fun onFechaHoraTransaccionChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                transaccionVenta = estado.transaccionVenta.copy(
                    fechaHora = valor
                ),
                errorTransaccion = estado.errorTransaccion.copy(
                    fechaHora = null
                )
            )
        }
    }

    fun onMontoTotalChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                transaccionVenta = estado.transaccionVenta.copy(
                    montoTotal = valor.toDoubleOrNull() ?: 0.0
                ),
                errorTransaccion = estado.errorTransaccion.copy(
                    montoTotal = null
                )
            )
        }
    }

    fun validarFormularioTransaccion(): Boolean {
        val transaccion = _estado.value.transaccionVenta

        val errores = ErrorTransaccionVenta(
            idTransaccion = if (
                transaccion.idTransaccion <= 0
            ) 1 else null,
            idAccesoIngreso = if (
                transaccion.idAccesoIngreso <= 0
            ) 1 else null,
            idUsuario = if (transaccion.idUsuario.isBlank()) {
                "Selecciona un usuario válido"
            } else {
                null
            },
            fechaHora = if (transaccion.fechaHora.isBlank()) {
                "La fecha y hora son obligatorias"
            } else {
                null
            },
            montoTotal = if (transaccion.montoTotal <= 0.0) {
                1.0
            } else {
                null
            }
        )

        _estado.update { estado ->
            estado.copy(errorTransaccion = errores)
        }

        return listOfNotNull(
            errores.idTransaccion,
            errores.idAccesoIngreso,
            errores.idUsuario,
            errores.fechaHora,
            errores.montoTotal
        ).isEmpty()
    }

    // --------------------------------------------------
    // USUARIO
    // Los errores están dentro de Usuario.ErrorUsu
    // --------------------------------------------------

    fun onUsuarioIdUsuarioChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                usuario = estado.usuario.copy(
                    idUsuario = valor,
                    ErrorUsu = estado.usuario.ErrorUsu.copy(
                        idUsuario = null
                    )
                )
            )
        }
    }

    fun onNombreChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                usuario = estado.usuario.copy(
                    nombre = valor,
                    ErrorUsu = estado.usuario.ErrorUsu.copy(
                        nombre = null
                    )
                )
            )
        }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                usuario = estado.usuario.copy(
                    correo = valor,
                    ErrorUsu = estado.usuario.ErrorUsu.copy(
                        correo = null
                    )
                )
            )
        }
    }

    fun onClaveChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                usuario = estado.usuario.copy(
                    clave = valor,
                    ErrorUsu = estado.usuario.ErrorUsu.copy(
                        clave = null
                    )
                )
            )
        }
    }

    fun onRolChange(valor: String) {
        _estado.update { estado ->
            estado.copy(
                usuario = estado.usuario.copy(
                    rol = valor,
                    ErrorUsu = estado.usuario.ErrorUsu.copy(
                        rol = null
                    )
                )
            )
        }
    }

    fun validarFormularioUsuario(): Boolean {
        val usuarioActual = _estado.value.usuario
        val idUserInt = usuarioActual.idUsuario.toIntOrNull() ?: 0

        val errores = ErrorUsuario(
            idUsuario = if (idUserInt <= 0) {
                "ID de usuario inválido"
            } else {
                null
            },
            nombre = if (usuarioActual.nombre.isBlank()) {
                "El nombre es obligatorio"
            } else {
                null
            },
            rol = if (usuarioActual.rol.isBlank()) {
                "Selecciona un rol"
            } else {
                null
            },
            correo = if (
                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(usuarioActual.correo)
                    .matches()
            ) {
                "Correo electrónico inválido"
            } else {
                null
            },
            clave = if (usuarioActual.clave.length < 8) {
                "La contraseña debe tener al menos 8 caracteres"
            } else {
                null
            }
        )

        _estado.update { estado ->
            estado.copy(
                usuario = estado.usuario.copy(
                    ErrorUsu = errores
                )
            )
        }

        return listOfNotNull(
            errores.idUsuario,
            errores.nombre,
            errores.rol,
            errores.correo,
            errores.clave
        ).isEmpty()
    }
}
