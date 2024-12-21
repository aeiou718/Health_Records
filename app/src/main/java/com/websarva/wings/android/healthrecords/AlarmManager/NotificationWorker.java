package com.websarva.wings.android.healthrecords.AlarmManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.websarva.wings.android.healthrecords.R;

public class NotificationWorker extends Worker {

    private static final String CHANNEL_ID = "default_channel_id";
    private static final String CHANNEL_NAME = "薬の服用通知";
    public static final String TITLE_KEY = "notification_title";
    public static final String MESSAGE_KEY = "notification_message";
    public static final int NOTIFICATION_ID = 123;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // 通知の表示処理
        showNotification();
        return Result.success();
    }

    private void showNotification() {
        Context context = getApplicationContext();

        String title = getInputData().getString(TITLE_KEY);
        String message = getInputData().getString(MESSAGE_KEY);
        int notificationId = getInputData().getInt(String.valueOf(NOTIFICATION_ID), 0);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        Notification builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.alarm_timer)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(notificationId, builder);
    }
}