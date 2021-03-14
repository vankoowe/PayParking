package com.example.payparking.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.payparking.MainActivity
import com.example.payparking.R

class ReminderBroadcast: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //val name = getString(R.string.channel_name)
            //val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notifyLemubit", "CHANNEL_NAME", importance).apply {
                description = "descriptionText"
                lightColor = Color.GREEN
                enableLights(true)
            }
            // Register the channel with the system
            val notification =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notification.createNotificationChannel(channel)
        }
        val intenti = Intent(context, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intenti)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notification = NotificationCompat.Builder(context, "notifyLemubit")
            .setContentTitle("Pay Parking")
            .setContentText("Времето ви изтича!!!")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        val notManager = NotificationManagerCompat.from(context)
        notManager.notify(200, notification)
    }

}