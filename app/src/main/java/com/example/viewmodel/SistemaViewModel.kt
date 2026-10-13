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
class SistemaViewModel: ViewModel() {
    private val estado = MutableStateFlow(value = Acceso())
}




