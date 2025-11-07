package com.example.arthas.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.arthas.Keys
import com.example.arthas.R
import com.example.arthas.notifications.NotificationRate
import com.example.arthas.utils.isEmpty

@Composable
fun ThirdScreen(navController: NavController? = null) {
    val context = LocalContext.current

    var largeContentIsEnabled by remember { mutableStateOf(false) }
    var checkedState by remember { mutableStateOf(false) }
    var expandedState by remember { mutableStateOf(false) }
    var notificationRateState by remember { mutableStateOf(NotificationRate.DEFAULT) }
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
            onValueChange = {
                description = it
                if (!isEmpty(description))
                    largeContentIsEnabled = true
                else
                    largeContentIsEnabled = false
            },
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
                    navController!!.previousBackStackEntry
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

        IconButton(
            onClick = { expandedState = !expandedState },
            content = {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }
        )
        DropdownMenu(
            expanded = expandedState,
            onDismissRequest = {
                // Do nothing ...
                expandedState = false
            }
        ) {
            DropdownMenuItem(
                text = { Text(text = NotificationRate.MIN.name) },
                onClick = {
                    notificationRateState = NotificationRate.MIN
                },
            )
            DropdownMenuItem(
                text = { Text(text = NotificationRate.LOW.name) },
                onClick = {
                    notificationRateState = NotificationRate.LOW
                }
            )
            DropdownMenuItem(
                text = { Text(text = NotificationRate.DEFAULT.name) },
                onClick = {
                    notificationRateState = NotificationRate.DEFAULT
                }
            )
            DropdownMenuItem(
                text = { Text(text = NotificationRate.HIGH.name) },
                onClick = {
                    notificationRateState = NotificationRate.HIGH
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Switch(
                enabled = largeContentIsEnabled,
                checked = checkedState,
                onCheckedChange = { checkedState = it },
            )
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ThirdScreenPreview() {
    ThirdScreen()
}