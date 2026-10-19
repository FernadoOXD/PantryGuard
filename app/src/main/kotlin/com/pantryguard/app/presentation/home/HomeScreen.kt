package com.pantryguard.app.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pantryguard.app.presentation.navigation.NavDestinations
import com.pantryguard.app.presentation.scanner.ScannerScreen
import com.pantryguard.app.presentation.inventory.InventoryScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Inventory

/**
 * Home screen that provides a bottom navigation bar to switch between the scanner and inventory screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem(
            route = NavDestinations.Scanner.route,
            icon = Icons.Filled.CameraAlt,
            label = "Scan"
        ),
        BottomNavItem(
            route = NavDestinations.Inventory.route,
            icon = Icons.Filled.Inventory,
            label = "Inventory"
        )
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination to avoid building up a large back stack
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestinations.Scanner.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavDestinations.Scanner.route) { ScannerScreen() }
            composable(NavDestinations.Inventory.route) { InventoryScreen() }
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)
