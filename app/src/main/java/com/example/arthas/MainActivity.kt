package com.example.arthas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.arthas.ui.MainScreen
import com.example.arthas.ui.MainViewModel
import com.example.arthas.ui.theme.ArthasTheme

class MainActivity : ComponentActivity(), LifecycleEventObserver {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycle.addObserver(this)

        setContent {
            ArthasTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> viewModel.onAppStart()
            Lifecycle.Event.ON_STOP -> viewModel.onAppStop()
            else -> { /* Ignored */ }
        }
    }

    override fun onDestroy() {
        lifecycle.removeObserver(this)
        super.onDestroy()
    }
}
