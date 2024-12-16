package com.example.cookingmasterclass.presentation.addrecipe

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cookingmasterclass.data.local.room.IngredientsDatabase
import com.example.cookingmasterclass.data.local.room.RecipeDatabase
import com.example.cookingmasterclass.domain.models.Ingredient
import com.example.cookingmasterclass.domain.models.Recipe
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel
@Inject
constructor(
    val db: RecipeDatabase,
    @ApplicationContext val context: Context,
) :
    ViewModel() {

    fun saveRecipe(recipe: Recipe) {
        viewModelScope.launch {
            db.recipeDao().insert(recipe)
        }
    }



}