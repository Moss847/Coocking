package com.example.cookingmasterclass.presentation.recipelist

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookingmasterclass.data.local.room.RecipeDatabase
import com.example.cookingmasterclass.domain.models.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    val db: RecipeDatabase,
    @ApplicationContext val context: Context,
) : ViewModel() {
    private var currentPage = 0
    private val pageSize = 10
    var recipes: MutableState<List<Recipe>> = mutableStateOf(listOf())
    var recipesCount: Int? = null

    fun resetState() {
        recipes.value = listOf()
        currentPage = 0
    }


    fun loadMoreItems() {
        Log.e(TAG, "load items from db")
        if (recipesCount ?: Int.MAX_VALUE > pageSize * currentPage )
            viewModelScope.launch {
                recipesCount = db.recipeDao().getCount()

                recipes.value += (
                        db.recipeDao()
                            .getItems(pageSize * currentPage, pageSize * currentPage + pageSize)
                            ?: listOf()
                        )
                Log.e(
                    TAG,
                    "recipes.size ${recipes.value.size}  $recipesCount  ${pageSize * currentPage}"
                )
                currentPage += 1
            }
    }
}