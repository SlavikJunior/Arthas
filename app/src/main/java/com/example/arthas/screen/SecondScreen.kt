package com.example.arthas.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arthas.Keys
import com.example.arthas.R
import com.example.arthas.model.Note
import com.example.arthas.navigation.NavRoute
import com.example.arthas.ui.theme.BluePrimary
import com.example.arthas.ui.theme.BrownPrimary
import com.example.arthas.ui.theme.ThemeController
import com.example.arthas.ui.theme.ThemeType
import com.example.arthas.ui.theme.ViolettePrimary

@Composable
fun SecondScreen(
    navController: NavController? = null,
    email: String = "sample@mail.ru"
) {
    var notes by rememberSaveable { mutableStateOf(listOf<Note>()) }

    val savedStateHandle = navController!!.currentBackStackEntry?.savedStateHandle
    val title = savedStateHandle?.get<String>(Keys.TITLE_KEY)
    val description = savedStateHandle?.get<String>(Keys.DESCRIPTION_KEY)

    if (!title.isNullOrBlank()) {
        val newNote = Note(title, description ?: "")
        notes = notes + newNote

        savedStateHandle.remove<String>(Keys.TITLE_KEY)
        savedStateHandle.remove<String>(Keys.DESCRIPTION_KEY)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SetUpDropdownMenu()
            Text(
                modifier = Modifier.weight(1F),
                text = email
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.5F)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(2.dp)
                )
        ) {
            items(notes) { note ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = note.title)
                    if (note.description.isNotBlank()) {
                        Text(text = note.description)
                    }
                }
            }
        }

        Button(
            onClick = {
                navController.navigate(NavRoute.SettingsScreen.destination)
            },
            content = {
                Text(text = stringResource(R.string.btn_navigate_to_the_third_screen))
            }
        )
    }
}

@Composable
fun SetUpDropdownMenu() {
    Box(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        var expanded by remember { mutableStateOf(false) }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.theme_brown),
                            color = BrownPrimary
                        )
                    },
                    onClick = { ThemeController.currentTheme.value = ThemeType.BROWN }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.theme_violette),
                            color = ViolettePrimary
                        )
                    },
                    onClick = { ThemeController.currentTheme.value = ThemeType.VIOLETTE }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.theme_blue),
                            color = BluePrimary
                        )
                    },
                    onClick = { ThemeController.currentTheme.value = ThemeType.BLUE }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.theme_system),
                            color = Color.LightGray
                        )
                    },
                    onClick = { ThemeController.currentTheme.value = ThemeType.SYSTEM }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SecondScreenPreview() {
    SecondScreen()
}