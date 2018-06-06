package org.edx.mobile.notifications.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.edx.mobile.R;
import org.edx.mobile.base.MainApplication;
import org.edx.mobile.core.IEdxEnvironment;
import org.edx.mobile.view.MainDashboardActivity;


public class NotificationService extends FirebaseMessagingService {
    public static final String NOTIFICATION_TOPIC_RELEASE = "edx_release_notification";

    public static void subscribeToTopics(IEdxEnvironment environment){
        if(environment.getConfig().areNotificationsEnabled()) {
            FirebaseMessaging.getInstance().subscribeToTopic(
                    NotificationService.NOTIFICATION_TOPIC_RELEASE
            );
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        IEdxEnvironment environment = MainApplication.getEnvironment(this);

        if(!environment.getConfig().areNotificationsEnabled()){
            // Do not process Notifications when they are disabled.
            return;
        }

        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            Log.d(this.getClass().getSimpleName(),
                    "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Build out the Notification and set the intent to direct the user to the application
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody());
        Intent resultIntent = new Intent(this, MainDashboardActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainDashboardActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        try {
            notificationManager.notify(99, builder.build());
        } catch (NullPointerException ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }

    }
}
