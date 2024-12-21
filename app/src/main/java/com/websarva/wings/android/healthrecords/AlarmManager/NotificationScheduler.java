package com.websarva.wings.android.healthrecords.AlarmManager;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class NotificationScheduler {

    public static void scheduleNotifications(Context context, int number) {
        switch (number) {
            case 0:
                PeriodicWorkRequest notification1 = createPeriodicWorkRequest(context, "@string/morning", 197001011);
                WorkManager.getInstance(context).enqueue(notification1);
                break;
            case 1:
                PeriodicWorkRequest notification2 = createPeriodicWorkRequest(context, "@string/noon", 197001012);
                WorkManager.getInstance(context).enqueue(notification2);
                break;
            case 2:
                PeriodicWorkRequest notification3 = createPeriodicWorkRequest(context, "@string/evening", 197001013);
                WorkManager.getInstance(context).enqueue(notification3);
                break;
        }
    }

    private static PeriodicWorkRequest createPeriodicWorkRequest(Context context, String title, int id) {
        // 通知内容を設定
        Data inputData = new Data.Builder()
                .putString(NotificationWorker.TITLE_KEY, title)
                .putString(NotificationWorker.MESSAGE_KEY, "@string/medication")
                .putInt(String.valueOf(NotificationWorker.NOTIFICATION_ID), id)
                .build();

        // WorkRequestを作成
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                NotificationWorker.class,
                24,
                TimeUnit.HOURS)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "notification_" + id,
                ExistingPeriodicWorkPolicy.KEEP,
                request
        );
        return request;
    }
}
