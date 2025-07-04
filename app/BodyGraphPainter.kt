package com.example.humandesign.ui
package com.rwurdig.hdbodygraph.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import kotlin.math.min
import kotlin.math.sqrt

// Data model for a center in the BodyGraph
private data class CenterDef(
    val name: String,
    val xFraction: Float,
    val yFraction: Float,
    val shape: CenterShape,
    val sizeFraction: Float,
    val color: Color,
    val defined: Boolean = true
)

// Shapes of centers (triangles can point Up or Down, plus Square and Diamond)
private enum class CenterShape { TriangleUp, TriangleDown, Square, Diamond }

// Predefine all 9 centers with approximate positions, sizes, colors, and whether defined
private val bodyGraphCenters = listOf(
    CenterDef(name = "Head",    xFraction = 0.50f, yFraction = 0.05f, shape = CenterShape.TriangleUp,   sizeFraction = 0.10f, color = Color(0xFFFFD700), defined = true),   // yellow
    CenterDef(name = "Ajna",    xFraction = 0.50f, yFraction = 0.15f, shape = CenterShape.TriangleDown, sizeFraction = 0.10f, color = Color(0xFF00B281), defined = true),   // green
    CenterDef(name = "Throat",  xFraction = 0.50f, yFraction = 0.30f, shape = CenterShape.Square,       sizeFraction = 0.12f, color = Color(0xFF795548), defined = true),   // brown
    CenterDef(name = "G",       xFraction = 0.50f, yFraction = 0.45f, shape = CenterShape.Diamond,      sizeFraction = 0.14f, color = Color(0xFFFFD700), defined = true),   // yellow (Identity)
    CenterDef(name = "Heart",   xFraction = 0.62f, yFraction = 0.57f, shape = CenterShape.TriangleUp,   sizeFraction = 0.08f, color = Color(0xFFD81B60), defined = false),  // red/pink (small Will center, here undefined)
    CenterDef(name = "Solar",   xFraction = 0.70f, yFraction = 0.65f, shape = CenterShape.TriangleDown, sizeFraction = 0.12f, color = Color(0xFFFFA726), defined = true),   // orange (Solar Plexus)
    CenterDef(name = "Spleen",  xFraction = 0.30f, yFraction = 0.65f, shape = CenterShape.TriangleDown, sizeFraction = 0.12f, color = Color(0xFF8D6E63), defined = true),   // brown (Spleen)
    CenterDef(name = "Sacral",  xFraction = 0.50f, yFraction = 0.75f, shape = CenterShape.Square,       sizeFraction = 0.14f, color = Color(0xFFE53935), defined = true),   // red (Sacral)
    CenterDef(name = "Root",    xFraction = 0.50f, yFraction = 0.90f, shape = CenterShape.Square,       sizeFraction = 0.16f, color = Color(0xFF5D4037), defined = true)    // dark brown (Root)
)

// Define channel connections between centers (each pair represents a channel connecting two centers)
private val bodyGraphChannels: List<Pair<String, String>> = listOf(
    "Head" to "Ajna",
    "Ajna" to "Throat",
    "Throat" to "G",
    "Throat" to "Heart",
    "Throat" to "Spleen",
    "Throat" to "Solar",
    "G" to "Heart",
    "G" to "Spleen",
    "G" to "Sacral",
    "Heart" to "Solar",
    "Heart" to "Spleen",
    "Sacral" to "Solar",
    "Sacral" to "Spleen",
    "Root" to "Sacral",
    "Root" to "Spleen",
    "Root" to "Solar"
)

@Composable
fun BodyGraphPainter(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        // Background image layer (place a 3D abstract image in res/drawable, e.g., bg_bodygraph.png)
        Image(
            painter = painterResource(id = R.drawable.bg_bodygraph),
            contentDescription = "Bodygraph Background",
            modifier = Modifier.fillMaxSize(),
            alpha = 0.3f  // slightly transparent to not distract from diagram
        )
        // Canvas to draw the bodygraph on top of the background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val scale = min(canvasWidth, canvasHeight)  // scale shapes relative to smaller dimension for responsiveness

            // Pre-compute actual center positions in pixels
            val centerPositions: Map<String, Offset> = bodyGraphCenters.associate { center ->
                center.name to Offset(center.xFraction * canvasWidth, center.yFraction * canvasHeight)
            }

            // Draw channels (connecting lines) behind the centers
            bodyGraphChannels.forEach { (a, b) ->
                val start = centerPositions[a]!!
                val end = centerPositions[b]!!
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = start,
                    end = end,
                    strokeWidth = 4f  // line thickness in pixels
                )
            }

            // Draw each center shape on top of the channels
            bodyGraphCenters.forEach { center ->
                val centerOffset = centerPositions[center.name]!!
                val shapeSize = center.sizeFraction * scale
                val half = shapeSize / 2f

                // Prepare a path for triangles and diamond (squares can be drawn directly)
                val path = Path()
                when (center.shape) {
                    CenterShape.TriangleUp -> {
                        // Up-pointing triangle (apex at top middle)
                        val height = shapeSize * sqrt(3f) / 2f  // equilateral triangle height
                        path.moveTo(centerOffset.x, centerOffset.y - height/2)            // top point
                        path.lineTo(centerOffset.x - half, centerOffset.y + height/2)     // bottom-left
                        path.lineTo(centerOffset.x + half, centerOffset.y + height/2)     // bottom-right
                        path.close()
                    }
                    CenterShape.TriangleDown -> {
                        // Down-pointing triangle (apex at bottom middle)
                        val height = shapeSize * sqrt(3f) / 2f
                        path.moveTo(centerOffset.x, centerOffset.y + height/2)            // bottom point
                        path.lineTo(centerOffset.x - half, centerOffset.y - height/2)     // top-left
                        path.lineTo(centerOffset.x + half, centerOffset.y - height/2)     // top-right
                        path.close()
                    }
                    CenterShape.Diamond -> {
                        // Diamond (rotated square 45 degrees)
                        path.moveTo(centerOffset.x, centerOffset.y - half)               // top
                        path.lineTo(centerOffset.x + half, centerOffset.y)               // right
                        path.lineTo(centerOffset.x, centerOffset.y + half)               // bottom
                        path.lineTo(centerOffset.x - half, centerOffset.y)               // left
                        path.close()
                    }
                    CenterShape.Square -> {
                        // Square (no path needed for filled square, we'll drawRect directly)
                    }
                }

                // Draw the center shape: filled with color if defined, white if open, plus outline if open
                if (center.shape == CenterShape.Square) {
                    // Draw square via drawRect
                    val left = centerOffset.x - half
                    val top = centerOffset.y - half
                    if (center.defined) {
                        drawRect(color = center.color, topLeft = Offset(left, top), size = androidx.compose.ui.geometry.Size(shapeSize, shapeSize))
                    } else {
                        drawRect(color = Color.White, topLeft = Offset(left, top), size = androidx.compose.ui.geometry.Size(shapeSize, shapeSize))
                        drawRect(color = Color.Black, topLeft = Offset(left, top), size = androidx.compose.ui.geometry.Size(shapeSize, shapeSize), style = Stroke(width = 3f))
                    }
                } else {
                    // Draw triangle or diamond via path
                    if (center.defined) {
                        drawPath(path = path, color = center.color, style = Fill)
                    } else {
                        drawPath(path = path, color = Color.White, style = Fill)
                        drawPath(path = path, color = Color.Black, style = Stroke(width = 3f))
                    }
                }
            }
        }
    }
}
