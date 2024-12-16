package com.example.cookingmasterclass.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cookingmasterclass.presentation.addrecipe.AddRecipeScreen
import com.example.cookingmasterclass.presentation.recipe.RecipeScreen
import com.example.cookingmasterclass.presentation.recipelist.RecipeListScreen
import com.example.cookingmasterclass.presentation.shoppingList.ShoppingListScreen
import com.example.cookingmasterclass.presentation.splash.SplashScreen


@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Splash.route
    ) {
        composable(route = AppScreens.Splash.route) {
            SplashScreen(navController)
        }
        composable(route = AppScreens.RecipeList.route) {
            RecipeListScreen(navController)
        }
        composable(route = AppScreens.AddRecipe.route) {
            AddRecipeScreen(navController)
        }
        composable(route = AppScreens.ShoppingList.route) {
            ShoppingListScreen(navController)
        }
        composable(
            route = AppScreens.Recipe.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.IntType },
            )
        ) {
            RecipeScreen(navController)
        }
    }
}