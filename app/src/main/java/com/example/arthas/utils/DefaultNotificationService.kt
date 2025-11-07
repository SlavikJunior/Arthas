package com.example.arthas.utils

import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.arthas.model.NotificationData

class DefaultNotificationService(
    private val ctx: Context
) {

    fun showNotification(notificationData: NotificationData) {
        val notification = NotificationCompat.Builder(ctx, DEFAULT_CHANNEL_ID)

            .setSmallIcon(notificationData.icon)
            .setContentTitle(notificationData.contentTitle)
            .setContentText(notificationData.contentText)


    }

    companion object {
        const val DEFAULT_CHANNEL_ID = "default_channel"
    }
}