package com.example.cookingmasterclass.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.cookingmasterclass.domain.models.Ingredient
import com.example.cookingmasterclass.domain.models.Recipe

@Dao
interface IngredientsDao {
    @Query("SELECT * FROM Ingredients")
    suspend fun getAll(): List<Ingredient>

    @Insert
    suspend fun insert (ingredient : Ingredient)
    @Update
    suspend fun update (ingredient : Ingredient)

    @Query("SELECT * FROM ingredients WHERE title = :name LIMIT 1")
    suspend fun getIngredientByName(name: String): Ingredient?

    suspend fun insertOrUpdate(ingredient : Ingredient){
        getIngredientByName(ingredient.title).also {
            if (it ==null){
                insert(ingredient)
            }
            else (
                update(it.copy(quantity = it.quantity+1))
            )
        }
    }

    @Query("DELETE FROM Ingredients")
    suspend fun deleteAll()
}

@Dao
interface RecipeDao {
    @Query("SELECT * FROM Recipes")
    suspend fun getAll(): List<Recipe>

    @Insert
    suspend fun insert(Recipe: Recipe)

    @Delete
    suspend fun delete(Recipe: Recipe)

    @Update
    suspend fun update(Recipe: Recipe)

    @Query("SELECT COUNT(*) FROM Recipes")
    suspend fun getCount(): Int

    @Query("SELECT * FROM Recipes LIMIT :limit OFFSET :offset")
    suspend fun getItems(offset: Int, limit: Int): List<Recipe>?

    @Query("SELECT * FROM Recipes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Recipe?

    @Query("SELECT Ingredients FROM Recipes")
    fun getAllIngredients(): List<String>
}

