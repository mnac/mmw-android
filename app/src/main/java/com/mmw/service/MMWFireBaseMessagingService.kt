package com.mmw.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mmw.R
import com.mmw.activity.home.HomeActivity

/**
 * Created by Mathias on 25/08/2017.
 *
 */

class MMWFireBaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (!remoteMessage.data.isEmpty()) {
            handlePayload(remoteMessage)
            generateNotification("Nouvelle notif", "Yeaaaaaar")
        }

        if (remoteMessage.notification != null) {
            Log.d("Messaging service: ",
                    "Message Notification Body: " + remoteMessage.notification.body)

            generateNotification(remoteMessage.notification.title, remoteMessage.notification.body)
        }
    }

    private fun handlePayload(remoteMessage: RemoteMessage) {

    }

    private fun generateNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        val notificationBuilder = NotificationCompat.Builder(this, "Client id")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }
}