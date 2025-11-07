package com.example.arthas.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.RemoteInput
import com.example.arthas.Keys
import com.example.arthas.service.MessageRepository
import com.example.arthas.service.NotificationService

class ReplyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        val replyText = remoteInput?.getString(Keys.KEY_REPLY)

        replyText?.let { text ->
            MessageRepository.addMessage("Reply: $text")
            Toast.makeText(context, "Reply received: $text", Toast.LENGTH_SHORT).show()
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.cancel(NotificationService.NOTIFICATION_ID)
        }
    }
}