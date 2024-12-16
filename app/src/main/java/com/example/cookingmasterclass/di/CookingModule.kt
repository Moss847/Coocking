package com.example.cookingmasterclass.di

import android.content.Context
import androidx.room.Room
import com.example.cookingmasterclass.data.local.room.IngredientsDatabase
import com.example.cookingmasterclass.data.local.room.RecipeDatabase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CookingModule {
    @Provides
    fun provideRecipeRoomDatabase(@ApplicationContext context: Context): RecipeDatabase {
        return Room.databaseBuilder(
            context,
            RecipeDatabase::class.java, "Recipes"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    fun provideShoppingRoomDatabase(@ApplicationContext context: Context): IngredientsDatabase {
        return Room.databaseBuilder(
            context,
            IngredientsDatabase::class.java, "Ingredients"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideGson() = Gson()

}