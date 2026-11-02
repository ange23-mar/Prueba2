package com.example.repository.data

import com.example.repository.models.RangoEtario
import com.example.repository.models.RegistroConteo
import com.example.repository.models.RegistroFlujo
import com.example.repository.models.TipoFlujo
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random

/**
 * Generador de datos FICTICIOS (la guía prohíbe usar datos reales del sistema de conteo).
 * Usa una semilla fija, así siempre produce el mismo resultado y es fácil de explicar.
 *
 * Supuestos de ejemplo: los accesos que conectan con la tienda vecina (3) y el mall (1, 2)
 * tienen más tránsito; el estacionamiento (4) tiene más compradores.
 */
object DatosIniciales {

    private val ingresosBase = mapOf(1 to 220, 2 to 160, 3 to 190, 4 to 90)      // por franja horaria
    private val porcentajeTransito = mapOf(1 to 0.62, 2 to 0.55, 3 to 0.72, 4 to 0.40)

    // Peso (%) de cada rango etario dentro de cada tipo de flujo
    private val pesosTransito = linkedMapOf(
        RangoEtario.MENOR_18 to 14, RangoEtario.ADULTO_18_29 to 30, RangoEtario.ADULTO_30_44 to 24,
        RangoEtario.ADULTO_45_59 to 16, RangoEtario.MAYOR_60 to 10, RangoEtario.SIN_DATO to 6
    )
    private val pesosCompra = linkedMapOf(
        RangoEtario.MENOR_18 to 4, RangoEtario.ADULTO_18_29 to 22, RangoEtario.ADULTO_30_44 to 34,
        RangoEtario.ADULTO_45_59 to 24, RangoEtario.MAYOR_60 to 12, RangoEtario.SIN_DATO to 4
    )

    private val horasDeConteo = listOf(13, 19) // dos lecturas por día en cada acceso

    fun generar(
        dias: Int = 30,
        ahora: Long = System.currentTimeMillis()
    ): Pair<List<RegistroConteo>, List<RegistroFlujo>> {
        val random = Random(42)
        val zona = ZoneId.systemDefault()
        val hoy = LocalDate.now(zona)
        val conteos = mutableListOf<RegistroConteo>()
        val flujos = mutableListOf<RegistroFlujo>()

        for (d in 0 until dias) {
            val fecha = hoy.minusDays(d.toLong())
            for (hora in horasDeConteo) {
                val ts = fecha.atTime(hora, 0).atZone(zona).toInstant().toEpochMilli()
                if (ts > ahora) continue // no generar lecturas en el futuro

                for (accesoId in 1..4) {
                    val total = (ingresosBase.getValue(accesoId) * (0.8 + random.nextDouble() * 0.4)).toInt()
                    val transito = (total * porcentajeTransito.getValue(accesoId)).toInt()
                    val compra = total - transito

                    conteos += RegistroConteo(accesoId = accesoId, fechaHora = ts, cantidad = total)
                    agregarFlujos(flujos, accesoId, ts, TipoFlujo.TRANSITO, transito, pesosTransito)
                    agregarFlujos(flujos, accesoId, ts, TipoFlujo.COMPRA, compra, pesosCompra)
                }
            }
        }
        return conteos to flujos
    }

    private fun agregarFlujos(
        destino: MutableList<RegistroFlujo>,
        accesoId: Int,
        ts: Long,
        tipo: TipoFlujo,
        total: Int,
        pesos: Map<RangoEtario, Int>
    ) {
        val sumaPesos = pesos.values.sum()
        var restante = total
        pesos.entries.forEachIndexed { i, (rango, peso) ->
            // El último rango recibe lo que sobra, para que la suma sea exactamente `total`
            val cantidad = if (i == pesos.size - 1) restante else total * peso / sumaPesos
            restante -= cantidad
            if (cantidad > 0) {
                destino += RegistroFlujo(
                    accesoId = accesoId,
                    fechaHora = ts,
                    tipoFlujo = tipo,
                    rangoEtario = rango,
                    cantidad = cantidad
                )
            }
        }
    }
}