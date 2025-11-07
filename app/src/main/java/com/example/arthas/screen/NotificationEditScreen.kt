package com.example.arthas.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.arthas.R
import com.example.arthas.service.NotificationService

@Composable
fun NotificationEditScreen() {
    val context = LocalContext.current
    val notificationService = remember { NotificationService(context) }

    var notificationId by remember { mutableStateOf("") }
    var newText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.edit_notification_title),
            style = MaterialTheme.typography.headlineSmall
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.available_notification_ids),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = stringResource(R.string.main_notification, NotificationService.NOTIFICATION_ID),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = stringResource(R.string.try_using_id),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        TextField(
            value = notificationId,
            onValueChange = { notificationId = it },
            label = { Text(stringResource(R.string.notification_id_field)) },
            placeholder = { Text("Example: ${NotificationService.NOTIFICATION_ID}") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = newText,
            onValueChange = { newText = it },
            label = { Text(stringResource(R.string.new_text_field)) },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val id = notificationId.toIntOrNull()
                if (id != null) {
                    val success = notificationService.updateNotification(id, newText)
                    if (success) {
                        Toast.makeText(context, context.getString(R.string.notification_updated), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, context.getString(R.string.notification_not_found), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.invalid_notification_id), Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.update_notification))
        }

        Button(
            onClick = {
                if (notificationService.hasActiveNotifications()) {
                    notificationService.cancelAllNotifications()
                    Toast.makeText(context, context.getString(R.string.all_notifications_removed), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, context.getString(R.string.no_notifications_to_remove), Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.remove_all_notifications))
        }
    }
}