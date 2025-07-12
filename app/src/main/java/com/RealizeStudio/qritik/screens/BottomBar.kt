package com.RealizeStudio.qritik.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.ui.theme.Primary

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Scanne,
        BottomNavItem.Create
    )
    val currentRoute = currentRoute(navController)

    androidx.compose.material3.NavigationBar(
        containerColor = Color.White) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Geriye dönüp yığılmayı önle
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(painterResource(
                        id = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon),
                        contentDescription = item.title,
                        tint = Color.Unspecified,

                    )
                },
                label = {


                    if (currentRoute == item.route){
                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            color = Primary // veya istediğin başka renk
                        )
                    }
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent, // seçili öğe arkası şeffaf
                    selectedIconColor = Color.Unspecified, // renk geçersiz kıl
                    unselectedIconColor = Color.Unspecified, // renk geçersiz kıl
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color.Gray
                )
                


            )
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
    val title: String
) {
    object Home : BottomNavItem("home", R.drawable.home_selected, R.drawable.home_default, "Anasayfa")
    object Scanne : BottomNavItem("scanne", R.drawable.scanner_selected, R.drawable.scanner_default, "Tarat")
    object Create : BottomNavItem("create", R.drawable.create_selected, R.drawable.create_default, "Oluştru")

}

