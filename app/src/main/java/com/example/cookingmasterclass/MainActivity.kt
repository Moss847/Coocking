package com.example.cookingmasterclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cookingmasterclass.navigation.NavigationGraph
import com.example.cookingmasterclass.ui.theme.CookingMasterClassTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//                    this.deleteDatabase("Recipes")

            CookingMasterClassTheme {
                NavigationGraph()
            }
        }
    }
}