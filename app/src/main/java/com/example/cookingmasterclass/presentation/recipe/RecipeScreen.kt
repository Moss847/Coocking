package com.example.cookingmasterclass.presentation.recipe

import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cookingmasterclass.presentation.addrecipe.CookingButton
import kotlinx.coroutines.delay
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeScreen(navController: NavController) {
    val viewModel: RecipeViewModel = hiltViewModel()
    val state = rememberLazyListState()
    val selectedIngredients: MutableState<List<String>> = remember { mutableStateOf(listOf()) }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var isRunning by remember { mutableStateOf(false) }
    var timeInSeconds by remember { mutableStateOf(0) }
    val timer: MutableState<Int?> = remember { mutableStateOf(null) }

    LaunchedEffect(state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset) {
        val current =
            viewModel.recipe?.step?.get(if (state.firstVisibleItemIndex == 0 &&state.layoutInfo.visibleItemsInfo.size<3) 0 else state.layoutInfo.visibleItemsInfo[1].index)
        val valueInBrackets = current?.let {
            val regex =
                "((\\d{1,2}:)?\\d{1,2}:\\d{1,2})\\s*-?\\s*((\\d{1,2}:)?\\d{1,2}:\\d{1,2})".toRegex()
            val matchResult = regex.find(it)
            if (matchResult != null) {
                val startTime = stringToMin(matchResult.groups[1]?.value)
                val endTime = stringToMin(matchResult.groups[3]?.value)
                timer.value = endTime - startTime
                isRunning = true
                timeInSeconds = 0
            } else {
                isRunning = false
                timer.value = null
            }
        }
    }




    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            timeInSeconds++
        }
    }
    Scaffold(topBar = {
        TopAppBar(title = { Text(viewModel.recipe?.title!!) }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }
        })
    }) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .padding(top = it.calculateTopPadding())
        ) {
            item {
                val file = try {
                    File(viewModel.recipe?.photo!!)
                } catch (e: Exception) {
                    null
                }
                Log.e(TAG, file.toString())
                if (file != null) {
                    val bitmap = BitmapFactory.decodeFile(file?.absolutePath)
                    Image(
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(5.dp)),
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "recipe photo"
                    )
                }
                LazyRow(
                    state = state,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
                    modifier = Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                )
                {
                    itemsIndexed(viewModel.recipe?.step!!) { index, item ->
                        Card(
                            modifier = Modifier
                                .width((screenWidth * 0.85).dp)
                                .aspectRatio(2f)
                        ) {
                            Text("${index + 1}. $item", modifier = Modifier.padding(10.dp))
                        }
                    }
                }
                if (timer.value != null) {
                    Column (modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(7.dp)){
                        Text("Таймер: ${(timer.value!! - timeInSeconds) / 60}:${(timer.value!! - timeInSeconds) % 60}")
                        LinearProgressIndicator(
                            progress = (timeInSeconds.toFloat() / timer!!.value!!.toFloat()),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                        )
                    }
                }

                FlowRow(
                    maxLines = 3,
                    modifier = Modifier.padding(15.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Ингредиенты : ", modifier = Modifier.padding(vertical = 4.dp))
                    viewModel.recipe!!.ingredients?.forEach {
                        Card(
                            modifier = Modifier
                                .cookingClick {
                                    if (it in selectedIngredients.value) {
                                        selectedIngredients.value -= (it)
                                    } else {
                                        selectedIngredients.value += it
                                    }
                                }
                                .padding(horizontal = 3.dp, vertical = 3.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (it in selectedIngredients.value) Color.White else MaterialTheme.colorScheme.surfaceVariant,
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    vertical = 4.dp,
                                    horizontal = 6.dp
                                )
                            ) {
                                Text(
                                    it,
                                    color = if (it in selectedIngredients.value) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
                CookingButton(
                    text = "Добавить продукты в список покупок",
                    isEnabled = selectedIngredients.value.size > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    viewModel.addIngredientsToShoppingList(selectedIngredients.value)
                    selectedIngredients.value = listOf()
                }
            }
        }
    }
}


@Composable
fun Modifier.cookingClick(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(
        indication = null,
        interactionSource = interactionSource
    ) { onClick() }
}

fun stringToMin(string: String?): Int {
    val time = string?.split(":")

    return if (time?.size == 3) (time?.get(0)?.toIntOrNull() ?: 0) * 3600 +
            (time?.get(1)?.toIntOrNull() ?: 0) * 60 +
            (time?.get(2)?.toIntOrNull() ?: 0)
    else
        (time?.get(0)?.toIntOrNull() ?: 0) * 60 +
                (time?.get(1)?.toIntOrNull() ?: 0)
}

