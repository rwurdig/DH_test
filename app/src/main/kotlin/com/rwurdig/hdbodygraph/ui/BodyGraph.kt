package com.rwurdig.hdbodygraph.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BodyGraph(
    gates: Map<Int, Color>,          // centreGate -> color id
    channels: Set<Pair<Int,Int>>,  // pair of gate numbers filled
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.aspectRatio(0.5f)) {
        // 1. draw background image (SVG or PNG under res/drawable)
        // 2. iterate gates: drawRect / drawCircle at pre-computed offsets
        // 3. iterate channels: drawLine with stroke = centreColor
    }
}

@Preview
@Composable
fun PreviewBodyGraph() {
    val dummyGates = mapOf(1 to Color.Green, 2 to Color.Green)
    val dummyChannels = setOf(1 to 2)
    BodyGraph(dummyGates, dummyChannels, Modifier.fillMaxWidth())
}