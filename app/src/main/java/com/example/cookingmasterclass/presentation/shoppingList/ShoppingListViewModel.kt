package com.example.cookingmasterclass.presentation.shoppingList

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookingmasterclass.data.local.room.IngredientsDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    val ingredientsDb: IngredientsDatabase,
) : ViewModel() {
    val ingredients = mutableStateOf( runBlocking { ingredientsDb.ingredientsDao().getAll() })


    fun clear() {
        viewModelScope.launch {
            ingredients.value = listOf()
            ingredientsDb.ingredientsDao().deleteAll()
        }
    }
}