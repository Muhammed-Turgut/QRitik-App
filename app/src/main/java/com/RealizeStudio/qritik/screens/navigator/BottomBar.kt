package com.RealizeStudio.qritik.screens.navigator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.RealizeStudio.qritik.R

@Composable
fun BottomBar(navController: NavController, navControllerApp: NavController) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Scanne,
        BottomNavItem.Create,
        BottomNavItem.Favorites
    )
    val currentRoute = currentRoute(navController)

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 8.dp,
            shadowElevation = 10.dp,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            ),
            modifier = Modifier.align(Alignment.Center)
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp)
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (item == BottomNavItem.Scanne) {
                                navControllerApp.navigate("CameraScreen") {
                                    launchSingleTop = true
                                }
                            } else if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = if (isSelected) item.selectedIcon else item.unselectedIcon
                                ),
                                contentDescription = stringResource(id = item.titleResId),
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(if (isSelected) 28.dp else 24.dp)
                            )
                        },
                        label = {},
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color.Unspecified,
                            unselectedIconColor = Color.Unspecified
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

sealed class BottomNavItem(
    val route: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val titleResId: Int
) {
    object Home : BottomNavItem("home", R.drawable.home_selected, R.drawable.home_default, R.string.nav_home)
    object Scanne : BottomNavItem("scanne", R.drawable.scanner_selected, R.drawable.scanner_default, R.string.nav_scan)
    object Create : BottomNavItem("create", R.drawable.create_selected, R.drawable.create_default, R.string.nav_create)
    object Favorites : BottomNavItem("favorites", R.drawable.save_selected, R.drawable.save_default, R.string.nav_favorites)
}
