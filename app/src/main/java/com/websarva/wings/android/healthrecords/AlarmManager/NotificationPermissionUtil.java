package com.websarva.wings.android.healthrecords.AlarmManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;

public class NotificationPermissionUtil {

    private static final String PREF_NAME = "NotificationPref";
    private static final String KEY_PROMPT_SHOWN = "NotificationPromptShown";

    public static void checkAndShowNotificationDialog(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isPromptShown = prefs.getBoolean(KEY_PROMPT_SHOWN, false);

        if (!isPromptShown) {
            // ダイアログを表示
            new AlertDialog.Builder(context)
                    .setTitle("通知設定の確認")
                    .setMessage("服用時刻の通知を表示するには通知の許可が必要です。設定しますか？")
                    .setPositiveButton("はい", (dialog, which) -> {
                        openNotificationSettings(context);
                        // プロンプトが表示されたことを記録
                        prefs.edit().putBoolean(KEY_PROMPT_SHOWN, true).apply();
                    })
                    .setNegativeButton("いいえ", (dialog, which) -> {
                        // プロンプトが表示されたことを記録
                        prefs.edit().putBoolean(KEY_PROMPT_SHOWN, true).apply();
                    })
                    .setCancelable(false) // ダイアログを閉じるには選択が必要
                    .show();
        }
    }

    private static void openNotificationSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        context.startActivity(intent);
    }
}