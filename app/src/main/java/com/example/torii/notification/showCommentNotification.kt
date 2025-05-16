package com.example.torii.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.torii.R

fun showCommentNotification(context: Context, ownerName: String, commentText: String) {
    // 🔐 Kiểm tra quyền trước
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    val channelId = "comment_channel"
    val notificationId = System.currentTimeMillis().toInt()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Comment Notifications"
        val descriptionText = "Thông báo khi có người bình luận"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // 🔔 Tạo và gửi thông báo
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.notifications_active)
        .setContentTitle("Bình luận mới trên Torii")
        .setContentText("$ownerName đã bình luận: $commentText")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}

