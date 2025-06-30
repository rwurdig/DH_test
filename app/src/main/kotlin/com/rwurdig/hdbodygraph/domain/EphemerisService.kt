package com.rwurdig.hdbodygraph.domain

import swisseph.SweDate
import swisseph.SwissEph
import java.time.ZonedDateTime

class EphemerisService(
    private val swe: SwissEph = SwissEph()
) {
    data class PlanetPos(val longitude: Double)

    fun positions(utc: ZonedDateTime): Map<Int, PlanetPos> {
        val jdUt = SweDate(utc.toLocalDate().year,
                           utc.monthValue,
                           utc.dayOfMonth,
                           utc.hour + utc.minute/60.0).julDay

        return listOf(
            SwissEph.SE_SUN, SwissEph.SE_EARTH, SwissEph.SE_MOON,
            SwissEph.SE_MERCURY, SwissEph.SE_VENUS, SwissEph.SE_MARS,
            SwissEph.SE_JUPITER, SwissEph.SE_SATURN, SwissEph.SE_URANUS,
            SwissEph.SE_NEPTUNE, SwissEph.SE_PLUTO
        ).associateWith { idx ->
            val xx = DoubleArray(6)
            // Specify the ephemeris flag, e.g., SEFLG_SWIEPH
            swe.swe_calc_ut(jdUt, idx, SwissEph.SEFLG_SWIEPH, xx, DoubleArray(6))
            PlanetPos(xx[0])   // longitude in degrees
        }
    }
}