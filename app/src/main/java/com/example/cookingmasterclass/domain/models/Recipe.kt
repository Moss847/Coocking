package com.example.cookingmasterclass.domain.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverter


@Entity(tableName = "Recipes")
data class Recipe(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "Title") var title: String? ="",
    @ColumnInfo(name = "Ingredients") var ingredients: List<String>? = listOf(),
    @ColumnInfo(name = "RecipePhoto") var photo: String? = "",
    @ColumnInfo(name = "RecipeStep") var step: List<String>? =listOf(),
    @ColumnInfo(name = "CookingTime") val cookingTime: Int? = 0,
    @ColumnInfo(name = "Difficulty") var difficulty: Difficulty?= Difficulty.Easy,
)

class Converter {
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(",")
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}

@Entity(tableName = "Ingredients")
data class Ingredient(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "Title") val title : String,
    @ColumnInfo(name = "Quantity") val quantity : Int = 1,
)
