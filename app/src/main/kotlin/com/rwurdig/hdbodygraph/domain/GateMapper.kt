package com.rwurdig.hdbodygraph.domain

object GateMapper {
    /** Returns gate number 1-64 given a planet's ecliptic longitude */
    fun gate(longitude: Double): Int {
        val degrees = (longitude + 360) % 360
        // This is a simplified mapping. You need to fill in all 64 segments.
        return when (degrees) {
            in   0.0..  5.625 -> 41 // Example segment
            in   5.625..11.25 -> 19 // Example segment
            // … fill the 64 segments …
            else -> 1 // Default fallback
        }
    }
}