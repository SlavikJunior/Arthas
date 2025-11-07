package com.example.arthas

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.VibrationEffect
import androidx.annotation.RequiresApi
import com.example.arthas.utils.DefaultNotificationService.Companion.DEFAULT_CHANNEL_ID

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name_default)
            val descriptionText = getString(R.string.channel_description_default)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(
                DEFAULT_CHANNEL_ID,
                name,
                importance
            ).apply {
                description = descriptionText
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    vibrationEffect = VibrationEffect.createWaveform(
                        longArrayOf(200),
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                }
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}