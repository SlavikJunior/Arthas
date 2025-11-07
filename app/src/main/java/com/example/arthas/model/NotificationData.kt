package com.example.arthas.model

import androidx.annotation.DrawableRes
import com.example.arthas.notifications.NotificationRate

data class NotificationData(
    val id: Int,
    val contentTitle: String,
    val contentText: String? = null,
    @DrawableRes
    val icon: Int,
    val priority: NotificationRate = NotificationRate.DEFAULT,
    val shouldExpand: Boolean = false,
    val shouldOpenApp: Boolean = false,
    val shouldAddReply: Boolean = false,
    val type: NotificationType = NotificationType.DEFAULT
)

enum class NotificationType {
    AUTH,
    PROMO,
    SETTINGS,
    DEFAULT,
}