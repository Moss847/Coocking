package com.example.cookingmasterclass.navigation


sealed class AppScreens(val route: String) {
    object AddRecipe : AppScreens("recipe/add/")
    object RecipeList : AppScreens("recipe/list/")
    object Recipe : AppScreens("recipes/{recipeId}/")
    object ShoppingList : AppScreens("shopping-list/")
    object Splash : AppScreens("splash/")
}