package com.example.cookingmasterclass.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cookingmasterclass.domain.models.Converter
import com.example.cookingmasterclass.domain.models.Ingredient
import com.example.cookingmasterclass.domain.models.Recipe

@Database(entities = [Recipe::class], version = 1)
@TypeConverters(Converter::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}
@Database(entities = [Ingredient::class], version = 1)
@TypeConverters(Converter::class)
abstract class IngredientsDatabase : RoomDatabase() {
    abstract fun ingredientsDao(): IngredientsDao
}