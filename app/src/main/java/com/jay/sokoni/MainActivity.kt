package com.jay.sokoni

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.jay.sokoni.ui.navigation.NavGraph
import com.jay.sokoni.ui.theme.SokoniTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SokoniTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
