package com.resolutionprogamer.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

private val Bg = Color(0xFF09070F)
private val Card = Color(0xFF171321)
private val Purple = Color(0xFF9C4DFF)
private val Purple2 = Color(0xFF6D2DFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ResolutionProApp() }
    }
}

@Composable
fun ResolutionProApp() {
    val context = LocalContext.current
    var stretch by remember { mutableFloatStateOf(1.56f) }
    var profile by remember { mutableStateOf("Rendimiento") }
    var overlay by remember { mutableStateOf(false) }

    val refreshRates = remember {
        val display = context.getSystemService(Display::class.java)
        display?.supportedModes?.map { it.refreshRate.roundToInt() }
            ?.distinct()?.sorted() ?: emptyList()
    }

    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Card, primary = Purple)) {
        Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text("RESOLUTION PRO GAMER", style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black, color = Color.White)
                    Text("Controles gaming + panel flotante", color = Color.LightGray)
                }
                item {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Card), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Text("ESTIRAMIENTO VISUAL", color = Purple, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text("${"%.2f".format(stretch)}X", style = MaterialTheme.typography.displaySmall,
                                color = Color.White, fontWeight = FontWeight.Black)
                            Text("1.56X — 1.99X", color = Color.Gray)
                            Slider(value = stretch, onValueChange = { stretch = it }, valueRange = 1.56f..1.99f, steps = 42)
                            Text("La resolución real del teléfono no cambia. Este control guarda el perfil visual; aplicar un estiramiento sobre otra app requiere soporte del sistema/OEM.",
                                color = Color.LightGray)
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Card), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Text("PERFIL DE RENDIMIENTO", color = Purple, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Normal", "Rendimiento", "Turbo").forEach {
                                    FilterChip(selected = profile == it, onClick = { profile = it }, label = { Text(it) })
                                }
                            }
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Card), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Text("FRECUENCIA DE PANTALLA", color = Purple, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text(if (refreshRates.isEmpty()) "No se pudo leer el modo de pantalla"
                                else "Detectado: ${refreshRates.joinToString(" / ")} Hz", color = Color.White)
                            Text("Solo se muestran modos que Android reporta como compatibles.", color = Color.Gray)
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Card), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Text("ICONO FLOTANTE", color = Purple, fontWeight = FontWeight.Bold)
                            Text("Botón plegable para abrir controles encima del juego.", color = Color.LightGray)
                            Spacer(Modifier.height(10.dp))
                            Button(onClick = {
                                if (!Settings.canDrawOverlays(context)) {
                                    context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                                        data = android.net.Uri.parse("package:${context.packageName}")
                                    })
                                } else {
                                    val intent = Intent(context, FloatingControlService::class.java)
                                    if (!overlay) ContextCompat.startForegroundService(context, intent)
                                    else context.stopService(intent)
                                    overlay = !overlay
                                }
                            }, colors = ButtonDefaults.buttonColors(containerColor = Purple2)) {
                                Text(if (overlay) "OCULTAR PANEL" else "ACTIVAR PANEL")
                            }
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Card), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Text("PERFILES", color = Purple, fontWeight = FontWeight.Bold)
                            Text("Blood Strike • Perfil personalizado • Restaurar ajustes", color = Color.White)
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(onClick = {}) { Text("GUARDAR PERFIL ACTUAL") }
                            OutlinedButton(onClick = {}) { Text("RESTAURAR") }
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Card), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Text("ADB / SHIZUKU", color = Purple, fontWeight = FontWeight.Bold)
                            Text("Estado: no configurado. Esta versión no inventa permisos ni aplica cambios que Android no permita.",
                                color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}
