package com.example.deepwork.deep_work_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.deepwork.deep_work_app.presentation.navigation.bottom_bar.BottomBarTab

@Composable
fun RootNavigationGraph(navController : NavHostController){
    NavHost(
        navController = navController,
        startDestination = BottomBarTab.Statistics.title
    ){
        composable(BottomBarTab.Statistics.title){}
        composable(BottomBarTab.Timer.title){}
        composable(BottomBarTab.Timeline.title){}
        composable(BottomBarTab.Settings.title){}

   }
}