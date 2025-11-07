package com.example.arthas.screen

import android.text.TextUtils.isEmpty
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.arthas.R
import com.example.arthas.model.NotificationData
import com.example.arthas.notifications.NotificationRate
import com.example.arthas.service.NotificationService

@Composable
fun NotificationSettingsScreen() {
    val context = LocalContext.current
    val notificationService = remember { NotificationService(context) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var shouldExpand by remember { mutableStateOf(false) }
    var notificationRate by remember { mutableStateOf(NotificationRate.DEFAULT) }
    var shouldOpenApp by remember { mutableStateOf(false) }
    var shouldAddReply by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val isDescriptionNotEmpty = !isEmpty(description)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.notification_settings_title),
            style = MaterialTheme.typography.headlineSmall
        )

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title_field)) },
            modifier = Modifier.fillMaxWidth(),
            isError = title.isBlank()
        )

        if (title.isBlank()) {
            Text(
                text = stringResource(R.string.title_required),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.description_field)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.expand_long_notifications))
            Switch(
                checked = shouldExpand,
                onCheckedChange = { shouldExpand = it },
                enabled = isDescriptionNotEmpty
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.notification_priority))
            Box {
                Button(onClick = { expanded = true }) {
                    Text(notificationRate.name)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    NotificationRate.values().forEach { rate ->
                        DropdownMenuItem(
                            text = { Text(rate.name) },
                            onClick = {
                                notificationRate = rate
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.open_app_on_click))
            Switch(
                checked = shouldOpenApp,
                onCheckedChange = { shouldOpenApp = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.add_reply_action))
            Switch(
                checked = shouldAddReply,
                onCheckedChange = { shouldAddReply = it }
            )
        }

        Button(
            onClick = {
                if (title.isBlank()) {
                    Toast.makeText(context, context.getString(R.string.title_cannot_be_empty), Toast.LENGTH_SHORT).show()
                } else {
                    val notificationData = NotificationData(
                        id = NotificationService.NOTIFICATION_ID,
                        contentTitle = title,
                        contentText = description,
                        icon = android.R.drawable.ic_dialog_info,
                        priority = notificationRate,
                        shouldExpand = shouldExpand,
                        shouldOpenApp = shouldOpenApp,
                        shouldAddReply = shouldAddReply
                    )
                    notificationService.createNotification(notificationData)
                    Toast.makeText(context, context.getString(R.string.notification_created), Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.create_notification))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationSettingsScreenPreview() {
    NotificationSettingsScreen()
}