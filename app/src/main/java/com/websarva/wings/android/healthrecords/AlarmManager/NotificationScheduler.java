package com.websarva.wings.android.healthrecords.AlarmManager;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {

    public static void scheduleNotifications(Context context, int number, int hour, int minute) {
        int delay = delayCalculation(hour, minute);
        if (delay < 0) {
            delay += 24 * 60 * 60 * 1000;
        }
        switch (number) {
            case 0:
                PeriodicWorkRequest notification1 = createPeriodicWorkRequest(context, "朝", delay, 197001011);
//                OneTimeWorkRequest notification1 = createOneTimeWorkRequest(context, "朝", delay, 197001011);
                WorkManager.getInstance(context).enqueue(notification1);
                break;
            case 1:
                PeriodicWorkRequest notification2 = createPeriodicWorkRequest(context, "昼", delay, 197001012);
//                OneTimeWorkRequest notification2 = createOneTimeWorkRequest(context, "昼", delay, 197001012);
                WorkManager.getInstance(context).enqueue(notification2);
                break;
            case 2:
                PeriodicWorkRequest notification3 = createPeriodicWorkRequest(context, "夕方", delay, 197001013);
//                OneTimeWorkRequest notification3 = createOneTimeWorkRequest(context, "夕方", delay, 197001013);
                WorkManager.getInstance(context).enqueue(notification3);
                break;
        }
    }

    static int delayCalculation(int targetHour, int targetMinute) {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Tokyo");
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeZone(timeZone);
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        // 遅延時間の計算 (ミリ秒)
        return (targetHour - currentHour) * 60 * 60 * 1000 + (targetMinute - currentMinute) * 60 * 1000;
    }

    private static PeriodicWorkRequest createPeriodicWorkRequest(Context context, String title, int delay, int id) {
        // 通知内容を設定
        Data inputData = new Data.Builder()
                .putString(NotificationWorker.TITLE_KEY, title)
                .putString(NotificationWorker.MESSAGE_KEY, "お薬の時間です")
                .putInt(String.valueOf(NotificationWorker.NOTIFICATION_ID), id)
                .build();

        // WorkRequestを作成
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                NotificationWorker.class,
                24,
                TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
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
