package com.websarva.wings.android.healthrecords.AlarmManager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.WorkManager;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.websarva.wings.android.healthrecords.DataBase.DaoNotification;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseNotification;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseNotificationSingleton;
import com.websarva.wings.android.healthrecords.DataBase.EntityNotification;
import com.websarva.wings.android.healthrecords.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationList extends AppCompatActivity {
    private DataBaseNotification dbn;
    ExecutorService executorService;
    private TextView notificationTimeMorning;
    private TextView notificationTimeNoon;
    private TextView notificationTimeEvening;
    private SwitchMaterial notificationSwitchMorning;
    private SwitchMaterial notificationSwitchNoon;
    private SwitchMaterial notificationSwitchEvening;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.notification_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.list_notification), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        executorService = Executors.newSingleThreadExecutor();
        dbn = DataBaseNotificationSingleton.getInstance(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notificationTimeMorning = findViewById(R.id.morning_notification);
        notificationTimeNoon = findViewById(R.id.noon_notification);
        notificationTimeEvening = findViewById(R.id.evening_notification);
        notificationSwitchMorning = findViewById(R.id.morning_checkBox);
        notificationSwitchNoon = findViewById(R.id.noon_checkBox);
        notificationSwitchEvening = findViewById(R.id.evening_checkBox);

        // 通知時刻の設定
        notificationTimeMorning.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("notification_time_morning", MODE_PRIVATE);
            int hour = sharedPreferences.getInt("hour_morning", Calendar.getInstance().get(Calendar.HOUR_OF_DAY)); // デフォルトは現在の時刻
            int minute = sharedPreferences.getInt("minute_morning", Calendar.getInstance().get(Calendar.MINUTE)); // デフォルトは現在の時刻

            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTitleText("Select Notification Time")
                    .setHour(hour)
                    .setMinute(minute)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .build();

            // 時刻設定ダイアログなどを表示
            picker.addOnPositiveButtonClickListener(dialog -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("hour_morning", picker.getHour());
                editor.putInt("minute_morning", picker.getMinute());
                editor.apply();
                int hour_St = picker.getHour();
                int minute_St = picker.getMinute();
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour_St, minute_St);
                notificationTimeMorning.setText(formattedTime);

                executorService.submit(new TimeSetting(dbn, new EntityNotification(hour_St, minute_St, notificationSwitchMorning.isChecked()), 197001011));
            });
            picker.show(getSupportFragmentManager(), "time_picker");
        });
        notificationTimeNoon.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("notification_time_morning", MODE_PRIVATE);
            int hour = sharedPreferences.getInt("hour_noon", Calendar.getInstance().get(Calendar.HOUR_OF_DAY)); // デフォルトは現在の時刻
            int minute = sharedPreferences.getInt("minute_noon", Calendar.getInstance().get(Calendar.MINUTE)); // デフォルトは現在の時刻

            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTitleText("Select Notification Time")
                    .setHour(hour)
                    .setMinute(minute)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .build();

            // 時刻設定ダイアログなどを表示
            picker.addOnPositiveButtonClickListener(dialog -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("hour_noon", picker.getHour());
                editor.putInt("minute_noon", picker.getMinute());
                editor.apply();
                int hour_St = picker.getHour();
                int minute_St = picker.getMinute();
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour_St, minute_St);
                notificationTimeNoon.setText(formattedTime);

                executorService.submit(new TimeSetting(dbn, new EntityNotification(hour_St, minute_St, notificationSwitchMorning.isChecked()), 197001012));
            });
            picker.show(getSupportFragmentManager(), "time_picker");
        });
        notificationTimeEvening.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("notification_time_morning", MODE_PRIVATE);
            int hour = sharedPreferences.getInt("hour_evening", Calendar.getInstance().get(Calendar.HOUR_OF_DAY)); // デフォルトは現在の時刻
            int minute = sharedPreferences.getInt("minute_evening", Calendar.getInstance().get(Calendar.MINUTE)); // デフォルトは現在の時刻

            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTitleText("Select Notification Time")
                    .setHour(hour)
                    .setMinute(minute)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .build();

            // 時刻設定ダイアログなどを表示
            picker.addOnPositiveButtonClickListener(dialog -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("hour_evening", picker.getHour());
                editor.putInt("minute_evening", picker.getMinute());
                editor.apply();
                int hour_St = picker.getHour();
                int minute_St = picker.getMinute();
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour_St, minute_St);
                notificationTimeEvening.setText(formattedTime);

                executorService.submit(new TimeSetting(dbn, new EntityNotification(hour_St, minute_St, notificationSwitchMorning.isChecked()), 197001013));
            });
            picker.show(getSupportFragmentManager(), "time_picker");
        });

        // 通知のON/OFF
        notificationSwitchMorning.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 通知のON/OFFを切り替え
            if (isChecked) {
                // 通知を有効にする処理
                NotificationScheduler.scheduleNotifications(getApplicationContext(), 0);
            } else {
                // 通知を無効にする処理
                cancelSpecificNotification(197001011); // 通知を取り消す
            }
        });
        notificationSwitchNoon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 通知のON/OFFを切り替え
            if (isChecked) {
                // 通知を有効にする処理
                NotificationScheduler.scheduleNotifications(getApplicationContext(), 1);
            } else {
                // 通知を無効にする処理
                cancelSpecificNotification(197001012); // 通知を取り消す
            }
        });
        notificationSwitchEvening.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 通知のON/OFFを切り替え
            if (isChecked) {
                // 通知を有効にする処理
                NotificationScheduler.scheduleNotifications(getApplicationContext(), 2);
            } else {
                // 通知を無効にする処理
                cancelSpecificNotification(197001013); // 通知を取り消す
            }
        });

        // データの保存と読み込み
        // SharedPreferences などを使用して実装
    }

    @Override
    protected void onResume() {
        super.onResume();
        executorService.execute(new DataRead(dbn));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_list, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_settings) {
            finish();
        }
        return true;
    }

    private void cancelSpecificNotification(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        WorkManager.getInstance(getApplicationContext()).cancelUniqueWork("notification_" + notificationId);
    }

    private static class TimeSetting implements Runnable {
        private final DataBaseNotification db;
        EntityNotification entityNotification;
        int Id;

        public TimeSetting(DataBaseNotification db, EntityNotification entityNotification, int Id) {
            this.db = db;
            this.entityNotification = entityNotification;
            this.Id = Id;
        }

        @Override
        public void run() {
            DaoNotification daoNotification = db.daoNotification();
            entityNotification.setId(Id);
            daoNotification.insert(entityNotification);
        }
    }

    private class DataRead implements Runnable {
        private final DataBaseNotification db;
        EntityNotification entityNotificationMorning;
        EntityNotification entityNotificationNoon;
        EntityNotification entityNotificationEvening;

        public DataRead(DataBaseNotification db) {
            this.db = db;
        }

        @Override
        public void run() {
            DaoNotification daoNotification = db.daoNotification();
            if (daoNotification.getAll().isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    EntityNotification entityNotification = new EntityNotification(0, 0, false);
                    entityNotification.setId(197001011 + i);
                    daoNotification.insert(entityNotification);
                }
            }
            entityNotificationMorning = daoNotification.getEnById(197001011);
            entityNotificationNoon = daoNotification.getEnById(197001012);
            entityNotificationEvening = daoNotification.getEnById(197001013);

            new Handler(Looper.getMainLooper()).post(this::onPostExecute);
        }

        void onPostExecute() {
            String formattedMorning = String.format(Locale.getDefault(), "%02d:%02d", entityNotificationMorning.getHour(), entityNotificationMorning.getMinute());
            notificationTimeMorning.setText(formattedMorning);
            notificationSwitchMorning.setChecked(entityNotificationMorning.isNotification_switch());
            String formattedNoon = String.format(Locale.getDefault(), "%02d:%02d", entityNotificationNoon.getHour(), entityNotificationNoon.getMinute());
            notificationTimeNoon.setText(formattedNoon);
            notificationSwitchNoon.setChecked(entityNotificationNoon.isNotification_switch());
            String formattedEvening = String.format(Locale.getDefault(), "%02d:%02d", entityNotificationEvening.getHour(), entityNotificationEvening.getMinute());
            notificationTimeEvening.setText(formattedEvening);
            notificationSwitchEvening.setChecked(entityNotificationEvening.isNotification_switch());
        }
    }
}
