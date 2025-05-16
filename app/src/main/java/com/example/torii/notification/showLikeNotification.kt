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

fun showLikeNotification(context: Context, postOwner: String) {
    val channelId = "like_channel_id"
    val notificationId = 1

    // Nếu Android 13+ thì phải kiểm tra quyền trước khi hiển thị
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            // Không có quyền thì không gửi thông báo
            return
        }
    }

    // Tạo Notification Channel nếu cần (Android 8+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Like Notifications"
        val descriptionText = "Thông báo khi bài viết được thích"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Tạo và hiển thị Notification
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.notifications_active) // Đảm bảo có icon trong drawable
        .setContentTitle("Lượt thích mới trên Torii")
        .setContentText("$postOwner đã bấm thích bài viết của bạn.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setOngoing(false)

    NotificationManagerCompat.from(context).notify(notificationId, builder.build())
}
