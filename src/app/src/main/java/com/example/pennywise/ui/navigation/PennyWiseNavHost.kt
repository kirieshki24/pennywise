package com.example.pennywise.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pennywise.ui.AppContainer
import com.example.pennywise.ui.AppViewModelFactory
import com.example.pennywise.ui.edit.EditEntryScreen
import com.example.pennywise.ui.edit.EditEntryViewModel
import com.example.pennywise.ui.home.HomeScreen
import com.example.pennywise.ui.home.HomeViewModel
import com.example.pennywise.ui.profiles.ProfilesScreen
import com.example.pennywise.ui.profiles.ProfilesViewModel
import com.example.pennywise.ui.rates.RatesScreen
import com.example.pennywise.ui.rates.RatesViewModel

@Composable
fun PennyWiseNavHost(
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(
                factory = AppViewModelFactory(appContainer)
            )
            HomeScreen(
                viewModel = viewModel,
                onAddEntry = { navController.navigate(Screen.EditEntry.createRoute()) },
                onOpenProfiles = { navController.navigate(Screen.Profiles.route) },
                onOpenRates = { navController.navigate(Screen.Rates.route) },
                onOpenEntry = { entryId ->
                    navController.navigate(Screen.EditEntry.createRoute(entryId))
                }
            )
        }
        composable(Screen.Profiles.route) {
            val viewModel: ProfilesViewModel = viewModel(
                factory = AppViewModelFactory(appContainer)
            )
            ProfilesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Rates.route) {
            val viewModel: RatesViewModel = viewModel(
                factory = AppViewModelFactory(appContainer)
            )
            RatesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.EditEntry.route,
            arguments = listOf(
                navArgument("entryId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: -1L
            val viewModel: EditEntryViewModel = viewModel(
                factory = AppViewModelFactory(appContainer, entryId)
            )
            EditEntryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
