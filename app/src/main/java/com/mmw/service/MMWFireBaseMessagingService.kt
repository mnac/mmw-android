package com.mmw.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mmw.R
import com.mmw.activity.tripDetail.TripDetailActivity

/**
 * Created by Mathias on 25/08/2017.
 *
 */

class MMWFireBaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (!remoteMessage.data.isEmpty()) {
            val tripId = remoteMessage.data["tripId"]
            val title = remoteMessage.data["title"]
            val description = remoteMessage.data["description"]
            generateNotification(title, description, tripId)
        }
    }

    private fun generateNotification(title: String?, messageBody: String?, id: String?) {
        val intent = Intent(this, TripDetailActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(TripDetailActivity.TRIP_ID_KEY, id)
        val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "Client id")
                .setSmallIcon(R.drawable.ic_following_24dp)
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