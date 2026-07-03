package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Assistant : Screen("assistant", "AI Analyst", Icons.Default.Chat)
    object Alerts : Screen("alerts", "Alerts", Icons.Default.Notifications)
    object Feedback : Screen("feedback", "Feedback", Icons.Default.Feedback)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Assistant,
    Screen.Alerts,
    Screen.Feedback
)
