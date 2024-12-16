package com.example.cookingmasterclass.presentation.shoppingList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cookingmasterclass.presentation.addrecipe.CookingButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(navController: NavController) {
    val viewModel: ShoppingListViewModel = hiltViewModel()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Список покупок") }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            })
        }
    )
    {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .padding(horizontal = 15.dp)
                    .fillMaxSize()
            ) {
                itemsIndexed(viewModel.ingredients.value) { index, ingredient ->
                    Text("${index + 1}.  ${ingredient.title} - ${ingredient.quantity}")
                }
            }
            CookingButton(
                text = "Очистить список",
                onClick = {viewModel.clear()},
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            )
        }
    }
}