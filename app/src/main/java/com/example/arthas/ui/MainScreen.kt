package com.example.arthas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arthas.R

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    paddingValues: PaddingValues = PaddingValues()
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .safeDrawingPadding()
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.padding(mediumPadding * 2),
                shape = RoundedCornerShape(4.dp),
                shadowElevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = mediumPadding),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    RowWithTextAndSwitch(
                        stringResource(R.string.is_sequentially),
                        state.isSequentially
                    ) { viewModel.changeSequentiallyMode() }
                    RowWithTextAndSwitch(
                        stringResource(R.string.is_parallel),
                        state.isParallel
                    ) { viewModel.changeParallelMode() }
                    RowWithTextAndSwitch(
                        stringResource(R.string.is_delayed_start),
                        state.isDelayedStart,
                    ) { viewModel.changeDelayedMode(it) }
                    RowWithTextAndSwitch(
                        stringResource(R.string.is_background_work),
                        state.isBackgroundWork
                    ) { viewModel.changeBackgroundWorkMode(it) }
                }
            }

            Surface(
                modifier = Modifier.padding(mediumPadding * 2),
                shape = RoundedCornerShape(4.dp),
                shadowElevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = mediumPadding),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(mediumPadding),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(stringResource(R.string.coroutine_slider_label))
                    }

                    CoroutineSlider(
                        paddingValues = mediumPadding,
                        value = state.coroutinesCount,
                        onValueChange = { viewModel.changeCoroutinesCount(it) }
                    )

                    DropDownAndButtons(
                        paddingValues = mediumPadding,
                        onStartClicked = { viewModel.startComputation(scope) },
                        onCancelClicked = { viewModel.stopComputation() }
                    ) { pickedDispatcher ->
                        viewModel.changeDispatcher(pickedDispatcher)
                    }
                }
            }
        }

        if (state.isLoading) {
            ProgressState(
                viewModel = viewModel,
                modifier = Modifier
                    .padding(mediumPadding * 4)
                    .align(Alignment.BottomCenter)
                    .size(80.dp)
            )
        }
    }
}

@Composable
private fun ProgressState(viewModel: MainViewModel, modifier: Modifier) {
    val state by viewModel.uiState.collectAsState()

    CircularProgressIndicator(
        progress = { state.progress },
        modifier = modifier
        )
}

@Composable
private fun CoroutineSlider(
    paddingValues: Dp,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingValues),
        horizontalArrangement = Arrangement.Center
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 10f..100f,
            steps = 18
        )
    }
}

@Composable
private fun DropDownAndButtons(
    paddingValues: Dp,
    onStartClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
    onDispatcherChanged: (Dispatchers) -> Unit = {}
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = paddingValues),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onStartClicked() }
        ) {
            Text(stringResource(R.string.start_label))
        }

        Button(
            onClick = { onCancelClicked() }
        ) {
            Text(stringResource(R.string.cancel_label))
        }

        IconButton(onClick = { menuExpanded = !menuExpanded }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.pick_dispatcher_content_description)
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = {
                menuExpanded = false
            }
        ) {
            DropdownMenuItem(
                text = { Text(Dispatchers.Default.name) },
                onClick = {
                    onDispatcherChanged(Dispatchers.Default)
                    menuExpanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(Dispatchers.Main.name) },
                onClick = {
                    onDispatcherChanged(Dispatchers.Main)
                    menuExpanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(Dispatchers.Unconfined.name) },
                onClick = {
                    onDispatcherChanged(Dispatchers.Unconfined)
                    menuExpanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(Dispatchers.IO.name) },
                onClick = {
                    onDispatcherChanged(Dispatchers.IO)
                    menuExpanded = false
                }
            )
        }
    }
}

@Composable
private fun RowWithTextAndSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() =
    MainScreen()