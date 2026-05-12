package com.example.pennywise.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profiles : Screen("profiles")
    object Rates : Screen("rates")
    object EditEntry : Screen("edit?entryId={entryId}") {
        fun createRoute(entryId: Long? = null): String {
            return if (entryId == null || entryId < 0) {
                "edit"
            } else {
                "edit?entryId=$entryId"
            }
        }
    }
}
