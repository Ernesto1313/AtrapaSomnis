package com.ernesto.atrapasomnins.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.ernesto.atrapasomnins.ui.theme.AtrapaSomnisTheme
import com.ernesto.atrapasomnins.ui.theme.AzulNoche
import com.ernesto.atrapasomnins.ui.theme.AzulNocheMedio
import com.ernesto.atrapasomnins.ui.theme.LilaClaro
import com.ernesto.atrapasomnins.ui.theme.Morado
import com.ernesto.atrapasomnins.ui.theme.TextoApagado
import com.ernesto.atrapasomnins.ui.ajustes.AjustesScreen
import com.ernesto.atrapasomnins.ui.detalle.DetalleSuenoScreen
import com.ernesto.atrapasomnins.ui.estadisticas.EstadisticasScreen
import com.ernesto.atrapasomnins.ui.inicio.InicioScreen
import com.ernesto.atrapasomnins.ui.registro.RegistroScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Instalamos el splash screen antes de que se dibuje nada
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtrapaSomnisTheme {
                val navController = rememberNavController()
                val entradaActual by navController.currentBackStackEntryAsState()
                val rutaActual = entradaActual?.destination?.route

                // Pantallas donde se muestra la barra de navegación inferior
                val pantallasConNavBar = listOf(
                    Pantalla.Inicio.ruta,
                    Pantalla.Estadisticas.ruta,
                    Pantalla.Ajustes.ruta
                )

                Scaffold(
                    containerColor = AzulNoche,
                    bottomBar = {
                        // Solo mostramos la nav bar en las pantallas principales
                        if (rutaActual in pantallasConNavBar) {
                            NavigationBar(
                                containerColor = AzulNocheMedio,
                                contentColor = LilaClaro
                            ) {
                                NavigationBarItem(
                                    selected = rutaActual == Pantalla.Inicio.ruta,
                                    onClick = {
                                        navController.navigate(Pantalla.Inicio.ruta) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = "Sueños"
                                        )
                                    },
                                    label = { Text("Sueños") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = LilaClaro,
                                        selectedTextColor = LilaClaro,
                                        unselectedIconColor = TextoApagado,
                                        unselectedTextColor = TextoApagado,
                                        indicatorColor = Morado.copy(alpha = 0.3f)
                                    )
                                )
                                NavigationBarItem(
                                    selected = rutaActual == Pantalla.Estadisticas.ruta,
                                    onClick = {
                                        navController.navigate(Pantalla.Estadisticas.ruta) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Default.BarChart,
                                            contentDescription = "Estadísticas"
                                        )
                                    },
                                    label = { Text("Stats") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = LilaClaro,
                                        selectedTextColor = LilaClaro,
                                        unselectedIconColor = TextoApagado,
                                        unselectedTextColor = TextoApagado,
                                        indicatorColor = Morado.copy(alpha = 0.3f)
                                    )
                                )
                                NavigationBarItem(
                                    selected = rutaActual == Pantalla.Ajustes.ruta,
                                    onClick = {
                                        navController.navigate(Pantalla.Ajustes.ruta) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = "Ajustes"
                                        )
                                    },
                                    label = { Text("Ajustes") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = LilaClaro,
                                        selectedTextColor = LilaClaro,
                                        unselectedIconColor = TextoApagado,
                                        unselectedTextColor = TextoApagado,
                                        indicatorColor = Morado.copy(alpha = 0.3f)
                                    )
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Pantalla.Inicio.ruta,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        composable(Pantalla.Inicio.ruta) {
                            InicioScreen(
                                onNuevoSueno = {
                                    navController.navigate(Pantalla.RegistroSueno.ruta)
                                },
                                onVerDetalle = { id ->
                                    navController.navigate(Pantalla.DetalleSueno.crearRuta(id))
                                }
                            )
                        }
                        composable(Pantalla.RegistroSueno.ruta) {
                            RegistroScreen(
                                onTerminar = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = Pantalla.DetalleSueno.ruta,
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: return@composable
                            DetalleSuenoScreen(
                                id = id,
                                onVolver = { navController.popBackStack() }
                            )
                        }
                        composable(Pantalla.Estadisticas.ruta) {
                            EstadisticasScreen()
                        }
                        composable(Pantalla.Ajustes.ruta) {
                            AjustesScreen()
                        }
                    }
                }
            }
        }
    }
}
