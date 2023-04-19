package com.example.plantario2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.provider.Settings.System.getString



class NotificationReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val plantName = intent.getStringExtra("plantName")
        val plantId = intent.getStringExtra("plantId")

        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
        val notificationChannel = NotificationChannel("plantario_channel", "Plantario", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationIntent = Intent(context, PlantDetailsActivity::class.java)
        notificationIntent.putExtra("plantName", plantName)

        val pendingIntent = PendingIntent.getActivity(context,  0 , intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationId = System.currentTimeMillis().toInt()
        val rnds = (0..1000).random()

        val notification = NotificationCompat.Builder(context, "plantario_channel")
            .setSmallIcon(R.drawable.small_icon)
            .setContentTitle(context.getString(R.string.app_notification_title))
            .setContentText(plantName)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(rnds, notification)
    }
}