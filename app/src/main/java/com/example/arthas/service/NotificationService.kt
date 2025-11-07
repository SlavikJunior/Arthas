package com.example.arthas.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.arthas.Keys
import com.example.arthas.MainActivity
import com.example.arthas.R
import com.example.arthas.model.NotificationData
import com.example.arthas.notifications.NotificationRate
import com.example.arthas.receiver.ReplyReceiver

class NotificationService(private val context: Context) {

    companion object {
        const val CHANNEL_HIGH = "high_importance"
        const val CHANNEL_DEFAULT = "default_importance"
        const val CHANNEL_LOW = "low_importance"
        const val CHANNEL_MIN = "min_importance"
        const val NOTIFICATION_ID = 1
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_HIGH,
                    context.getString(R.string.high_priority_channel),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.high_priority_description)
                },
                NotificationChannel(
                    CHANNEL_DEFAULT,
                    context.getString(R.string.default_priority_channel),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.default_priority_description)
                },
                NotificationChannel(
                    CHANNEL_LOW,
                    context.getString(R.string.low_priority_channel),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = context.getString(R.string.low_priority_description)
                },
                NotificationChannel(
                    CHANNEL_MIN,
                    context.getString(R.string.min_priority_channel),
                    NotificationManager.IMPORTANCE_MIN
                ).apply {
                    description = context.getString(R.string.min_priority_description)
                }
            )

            notificationManager.createNotificationChannels(channels)
        }
    }

    fun createNotification(notificationData: NotificationData) {
        val channelId = when (notificationData.priority) {
            NotificationRate.HIGH -> CHANNEL_HIGH
            NotificationRate.DEFAULT -> CHANNEL_DEFAULT
            NotificationRate.LOW -> CHANNEL_LOW
            NotificationRate.MIN -> CHANNEL_MIN
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(notificationData.icon)
            .setContentTitle(notificationData.contentTitle)
            .setContentText(notificationData.contentText)
            .setPriority(getPriority(notificationData.priority))

        if (notificationData.shouldExpand && notificationData.contentText?.length ?: 0 > 50) {
            val bigTextStyle = NotificationCompat.BigTextStyle()
                .bigText(notificationData.contentText)
                .setBigContentTitle(notificationData.contentTitle)
            builder.setStyle(bigTextStyle)
        }

        if (notificationData.shouldOpenApp) {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra(Keys.TITLE_KEY, notificationData.contentTitle)
                putExtra(Keys.DESCRIPTION_KEY, notificationData.contentText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                pendingIntentFlags
            )
            builder.setContentIntent(pendingIntent)
        }

        if (notificationData.shouldAddReply) {
            val replyIntent = Intent(context, ReplyReceiver::class.java)

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val replyPendingIntent = PendingIntent.getBroadcast(
                context,
                notificationData.id,
                replyIntent,
                pendingIntentFlags
            )

            val remoteInput = RemoteInput.Builder(Keys.KEY_REPLY)
                .setLabel(context.getString(R.string.reply_action_label))
                .build()

            val action = NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_send,
                context.getString(R.string.reply_button),
                replyPendingIntent
            ).addRemoteInput(remoteInput).build()

            builder.addAction(action)
        }

        notificationManager.notify(notificationData.id, builder.build())
    }

    fun updateNotification(notificationId: Int, newText: String): Boolean {
        val activeNotifications = notificationManager.activeNotifications
        val notificationExists = activeNotifications.any { it.id == notificationId }

        if (notificationExists) {
            val notificationData = NotificationData(
                id = notificationId,
                contentTitle = context.getString(R.string.updated_notification_title),
                contentText = newText,
                icon = android.R.drawable.ic_dialog_info,
                priority = NotificationRate.DEFAULT
            )
            createNotification(notificationData)
            return true
        }
        return false
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    fun hasActiveNotifications(): Boolean {
        return notificationManager.activeNotifications.isNotEmpty()
    }

    private fun getPriority(rate: NotificationRate): Int {
        return when (rate) {
            NotificationRate.HIGH -> NotificationCompat.PRIORITY_HIGH
            NotificationRate.DEFAULT -> NotificationCompat.PRIORITY_DEFAULT
            NotificationRate.LOW -> NotificationCompat.PRIORITY_LOW
            NotificationRate.MIN -> NotificationCompat.PRIORITY_MIN
        }
    }
}