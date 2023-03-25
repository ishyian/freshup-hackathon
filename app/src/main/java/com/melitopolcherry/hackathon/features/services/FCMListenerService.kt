package com.melitopolcherry.hackathon.features.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.request.ImageRequest
import com.melitopolcherry.hackathon.R
import com.melitopolcherry.hackathon.data.utils.Enums
import com.melitopolcherry.hackathon.features.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import timber.log.Timber
import java.net.URLDecoder

class FCMListenerService : FirebaseMessagingService() {

    override fun onCreate() {
        Timber.d("🤖FCM CREATED")
        super.onCreate()
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Timber.d("🤖FCM\"Refreshed authToken: $p0\"")
    }

    override fun onMessageSent(p0: String) {
        Timber.d("🤖FCM\"Sended: $p0\"")
        super.onMessageSent(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        Timber.d("🤖FCM received :: ${p0.data} || ${p0.notification?.title} || ${p0.notification?.body} || ${p0.notification?.toString()}")
        val imageUrl = p0.notification?.imageUrl
        var bitmap: Bitmap?
        if (imageUrl != null) {
            val loader = Coil.imageLoader(this)
            val request = ImageRequest.Builder(this)
                .data(imageUrl)
                .target { drawable ->
                    bitmap = drawable.toBitmap()
                    try {
                        sendNotification(p0.notification, bitmap)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                .build()
            loader.enqueue(request)
        } else {
            sendNotification(p0.notification, null)
        }
    }

    private fun sendNotification(rmNotification: RemoteMessage.Notification?, bitmap: Bitmap?) {
        Timber.d("🤖FCM SEND PUSH ${rmNotification?.toString()}")

        val title = if (rmNotification?.title != null) {
            URLDecoder.decode(rmNotification.title, "UTF-8")
        } else {
            null
        }
        val body = URLDecoder.decode(rmNotification?.body, "UTF-8")
        var style: NotificationCompat.Style? = null
        if (bitmap != null) {
            style = NotificationCompat.BigPictureStyle().bigPicture(bitmap)
        }

        val notifyId = 0
        val id = "435"
        val pendingIntent: PendingIntent

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, id)
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.putExtra(Enums.BundleCodes.NotificationMove.name, 3)

        pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (title != null) {
            builder.setContentTitle(title)
        }

        builder.setContentText(body)
            .setLargeIcon(bitmap)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .setStyle(style)
            .setContentIntent(pendingIntent)
            .setTicker(body)
            .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))

        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var channel: NotificationChannel? = notificationManager.getNotificationChannel(id)
        if (channel == null) {
            channel = NotificationChannel(id, "Evenz Notification Channel", importance)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        with(NotificationManagerCompat.from(this)) {
            notify(notifyId, notification)
        }
    }
}