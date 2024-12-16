package com.example.cookingmasterclass.presentation.recipelist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cookingmasterclass.domain.models.Difficulty
import com.example.cookingmasterclass.navigation.AppScreens
import com.example.cookingmasterclass.presentation.addrecipe.CookingTimePicker
import com.example.cookingmasterclass.presentation.addrecipe.SegmentedButton
import com.example.cookingmasterclass.presentation.receipelist.components.RecipeCard
import com.example.cookingmasterclass.presentation.receipelist.components.RecipeListTopBar
import com.example.cookingmasterclass.presentation.recipe.cookingClick
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecipeListScreen(navController: NavController) {
    val viewModel: RecipeListViewModel = hiltViewModel()

    val ingredientFilter: MutableState<List<String>> = remember { mutableStateOf(listOf()) }
    val difficultyFilter: MutableState<Difficulty?> = remember { mutableStateOf(null) }
    val timeFilter: MutableState<Int?> = remember { mutableStateOf(null) }

    val showTimePicker = remember { mutableStateOf(false) }
    val expand = remember { mutableStateOf(false) }

    Scaffold(topBar = {
        RecipeListTopBar(
            onClick = {
                navController.navigate(AppScreens.AddRecipe.route)
                viewModel.resetState()
            }
        )
    },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppScreens.ShoppingList.route) },
                content = { Text("Список покупок", modifier = Modifier.padding(5.dp)) })
        }) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .padding(top = it.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {
                Card(
                    modifier = Modifier
                        .animateContentSize()
                        .cookingClick {
                            val a = !expand.value
                            expand.value = a
                        }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(55.dp).padding(horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (expand.value) {
                            SegmentedButton(modifier = Modifier.padding(
                                vertical = 10.dp
                            ),
                                difficultyFilter.value?.id ?: Difficulty.Easy.id,
                                Difficulty.entries.toList().map { it.displayName },
                                onItemSelection = {
                                    difficultyFilter.value =
                                        Difficulty.entries.first { dif -> dif.id == it }
                                }
                            )
                            LazyRow {
                                items(viewModel.recipes.value.flatMap { it.ingredients ?: listOf() }
                                    .distinct()) {
                                    Card(
                                        modifier = Modifier
                                            .padding(horizontal = 3.dp)
                                            .cookingClick { ingredientFilter.value += it },
                                        colors = CardDefaults.cardColors()
                                            .copy(if (it in ingredientFilter.value) Color.White else MaterialTheme.colorScheme.surfaceContainer)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(
                                                vertical = 4.dp,
                                                horizontal = 6.dp
                                            )
                                        ) {
                                            Text(
                                                it,
                                                color = if (it in ingredientFilter.value) Color.Black else Color.White
                                            )
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "delete $it",
                                                modifier = Modifier.cookingClick {
                                                    ingredientFilter.value -= it
                                                },
                                                tint = if (it in ingredientFilter.value) Color.Black else Color.White
                                            )
                                        }
                                    }
                                }
                            }

                           Row (verticalAlignment = Alignment.CenterVertically){   Text("Время приготовления ${timeFilter.value ?: 0 / 60}:${timeFilter.value ?: 0 % 60}",
                                modifier = Modifier.cookingClick {
                                    showTimePicker.value = true
                                }.padding(vertical = 10.dp , horizontal = 10.dp))
                               Icon(
                                   Icons.Default.Clear,
                                   contentDescription = "delete $it",
                                   modifier = Modifier.cookingClick {
                                       timeFilter.value = null
                                   },
                               )
                           }

                        } else {
                            Text("Фильтры")
                        }
                    }
                }

                if (showTimePicker.value)
                    CookingTimePicker(
                        onConfirm = {
                            showTimePicker.value = false
                            timeFilter.value = it
                        },
                        onDismiss = { showTimePicker.value = false }
                    )
            }

            items(viewModel.recipes.value.filter {
                (difficultyFilter.value == null || difficultyFilter.value!!.id == it.difficulty!!.id) &&
                        (timeFilter.value == null || timeFilter.value!! >= it.cookingTime ?: 0) &&
                        (it.ingredients!!.containsAll(ingredientFilter.value))
            }) {
                RecipeCard(
                    it,
                    context = viewModel.context,
                    onClick = {
                        navController.navigate(
                            AppScreens.Recipe.route.replace(
                                "{recipeId}", it.id.toString()
                            )
                        )
                        viewModel.resetState()
                    })
            }
            item {
                LaunchedEffect(Unit) {
                    viewModel.loadMoreItems()
                }
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

