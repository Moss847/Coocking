package com.example.cookingmasterclass.presentation.recipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookingmasterclass.data.local.room.IngredientsDatabase
import com.example.cookingmasterclass.data.local.room.RecipeDatabase
import com.example.cookingmasterclass.domain.models.Ingredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    val db: RecipeDatabase,
    val ingredientsDb: IngredientsDatabase,
    savedState: SavedStateHandle,
) : ViewModel() {
    private val recipeId: Int = checkNotNull(savedState["recipeId"])
    val recipe = runBlocking { db.recipeDao().getById(recipeId) }

    fun addIngredientsToShoppingList(ingredient: List<String>) {
        viewModelScope.launch {
            ingredient.forEach {
                ingredientsDb.ingredientsDao()
                    .insertOrUpdate(ingredient = Ingredient(title = it))
            }
        }
    }
}