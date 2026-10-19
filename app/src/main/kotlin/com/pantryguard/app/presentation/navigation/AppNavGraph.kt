package com.pantryguard.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pantryguard.app.presentation.scanner.ScannerScreen
import com.pantryguard.app.presentation.inventory.InventoryScreen

/**
 * Defines the navigation graph for the app.
 */
@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = NavDestinations.Scanner.route,
        modifier = modifier
    ) {
        composable(NavDestinations.Scanner.route) {
            ScannerScreen()
        }
        composable(NavDestinations.Inventory.route) {
            InventoryScreen()
        }
    }
}

/** Navigation routes used in the app. */
sealed class NavDestinations(val route: String) {
    object Scanner : NavDestinations("scanner")
    object Inventory : NavDestinations("inventory")
    object AddEditItem : NavDestinations("add_edit_item")
}
