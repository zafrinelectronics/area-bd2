package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    const val CHANNEL_ID = "apple_area_shop_notifications"
    private const val CHANNEL_NAME = "Apple Area Shop নোটিফিকেশন"
    private const val CHANNEL_DESC = "দোকানের বিক্রি, স্টক ও হিসাবের গুরুত্বপূর্ণ আপডেট"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled safely
        }
    }

    fun notifySale(context: Context, model: String, amount: Double, invoice: String) {
        showNotification(
            context = context,
            title = "🎉 নতুন বিক্রি সম্পন্ন!",
            message = "$model বিক্রি হয়েছে ৳ ${amount.toInt()} মূল্যে। ইনভয়েস: $invoice",
            notificationId = (System.currentTimeMillis() % 100000).toInt()
        )
    }

    fun notifyStockAdded(context: Context, model: String, imei: String) {
        showNotification(
            context = context,
            title = "📦 নতুন স্টক যুক্ত হয়েছে",
            message = "$model সফলভাবে স্টকে যুক্ত হয়েছে। IMEI: $imei",
            notificationId = (System.currentTimeMillis() % 100000).toInt()
        )
    }

    fun notifyDueCollected(context: Context, customer: String, amount: Double) {
        showNotification(
            context = context,
            title = "💰 বকেয়া টাকা আদায় হয়েছে",
            message = "$customer এর থেকে ৳ ${amount.toInt()} বকেয়া টাকা জমা হয়েছে।",
            notificationId = (System.currentTimeMillis() % 100000).toInt()
        )
    }

    fun notifyCloudBackup(context: Context, message: String) {
        showNotification(
            context = context,
            title = "☁️ GitHub ব্যাকআপ আপডেট",
            message = message,
            notificationId = 1001
        )
    }
}
