package com.example.cookingmasterclass.presentation.receipelist.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cookingmasterclass.presentation.recipe.cookingClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListTopBar(onClick: () -> Unit) {
    TopAppBar(title = { Text("Список Рецептов") }, actions = {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Добавить рецепт",
            modifier = Modifier
                .cookingClick { onClick() }
        )
    })
}
