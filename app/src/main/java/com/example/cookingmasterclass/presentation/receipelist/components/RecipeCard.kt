package com.example.cookingmasterclass.presentation.receipelist.components

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.cookingmasterclass.domain.models.Recipe
import com.example.cookingmasterclass.presentation.recipe.cookingClick
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit, context: Context) {
    Card(modifier = Modifier.cookingClick { onClick() }) {
        Column(modifier = Modifier.padding(vertical = 5.dp)) {
            Text(
                recipe.title!!,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 15.dp)
            )

            if (recipe.photo != null) {
                val file = try {
                    File(recipe.photo)
                } catch (e: Exception) {
                    null
                }
                Log.e(TAG, file.toString())
                if (file != null) {
                    val bitmap = BitmapFactory.decodeFile(file?.absolutePath)
                    Image(
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 10.dp)
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(5.dp)),
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "recipe photo"
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(recipe.cookingTime.toString() +" мин")
                Text("${recipe.difficulty?.displayName}")
            }
            FlowRow(
                maxLines = 3,
                modifier = Modifier.padding(15.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Ингредиенты : ", modifier = Modifier.padding(vertical = 4.dp))
                recipe.ingredients?.forEach {
                    Card(modifier = Modifier.padding(horizontal = 3.dp)) {
                        Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)) {
                            Text("$it,")
                        }
                    }
                }
            }
//            Text("Шаги приготовления:", modifier = Modifier.padding(start = 15.dp))
//            recipe.step?.forEachIndexed { index, step ->
//                Text("$index. $step", modifier = Modifier.padding(start = 15.dp))
//            }

        }
    }
}