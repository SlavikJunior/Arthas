package com.example.arthas.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.arthas.Keys
import com.example.arthas.R
import com.example.arthas.model.Note

@Composable
fun ThirdScreen(navController: NavController) {
    val context = LocalContext.current

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text(text = stringResource(R.string.title_text_field_hint)) },
            singleLine = true
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text(text = stringResource(R.string.description_text_field_hint)) }
        )

        Button(
            onClick = {
                if (title.isBlank()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.toast_title_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Keys.TITLE_KEY, title)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Keys.DESCRIPTION_KEY, description)
                    navController.popBackStack()
                }
            },
            content = {
                Text(text = stringResource(R.string.btn_save_and_return))
            }
        )
    }
}
