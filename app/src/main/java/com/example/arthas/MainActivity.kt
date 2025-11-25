package com.example.arthas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.arthas.ui.MainScreen
import com.example.arthas.ui.MainViewModel
import com.example.arthas.ui.TopBar
import com.example.arthas.ui.theme.ArthasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArthasTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBar() }
                ) { innerPadding ->
                    MainScreen(
//                        viewModel = ViewModelProvider(
//                            this,
//                            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
//                        )[MainViewModel::class],
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}