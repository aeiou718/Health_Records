package com.websarva.wings.android.healthrecords.AlarmManager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        //ツールバー
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> finish());

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
                if (!notificationSwitchMorning.isChecked()) {
                    notificationSwitchMorning.setChecked(true);
                }
                executorService.submit(new TimeSetting(dbn, new EntityNotification(hour_St, minute_St, true), 197001011));
                NotificationScheduler.scheduleNotifications(getApplicationContext(), 0, hour, minute);
            });
            picker.show(getSupportFragmentManager(), "time_picker");
            // 通知を有効にする処理
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
                if (notificationSwitchNoon.isChecked()) {
                    notificationSwitchNoon.setChecked(false);
                }
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
                if (notificationSwitchEvening.isChecked()) {
                    notificationSwitchEvening.setChecked(false);
                }
                executorService.submit(new TimeSetting(dbn, new EntityNotification(hour_St, minute_St, notificationSwitchMorning.isChecked()), 197001013));
            });
            picker.show(getSupportFragmentManager(), "time_picker");
        });

        // 通知のON/OFF
        notificationSwitchMorning.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 通知のON/OFFを切り替え
            Calendar calendar = Calendar.getInstance();
            if (isChecked) {
                String text = notificationTimeMorning.getText().toString();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                try {
                    Date date = format.parse(text);

                    if (date != null) {
                        calendar.setTime(date);
                    }
                    // 時と分を取得
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    // 通知を有効にする処理
                    NotificationScheduler.scheduleNotifications(getApplicationContext(), 0, hour, minute);
//                    executorService.submit(new TimeSetting(dbn, new EntityNotification(hour, minute, true), 197001011));
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 通知を無効にする処理
                cancelSpecificNotification(197001011); // 通知を取り消す
//                executorService.submit(new TimeSetting(dbn, new EntityNotification(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false), 197001011));
            }
        });
        notificationSwitchNoon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 通知のON/OFFを切り替え
            if (isChecked) {
                String text = notificationTimeMorning.getText().toString();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                try {
                    Date date = format.parse(text);

                    Calendar calendar = Calendar.getInstance();
                    if (date != null) {
                        calendar.setTime(date);
                    }
                    // 時と分を取得
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    // 通知を有効にする処理
                    NotificationScheduler.scheduleNotifications(getApplicationContext(), 0, hour, minute);
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 通知を無効にする処理
                cancelSpecificNotification(197001012); // 通知を取り消す
            }
        });
        notificationSwitchEvening.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 通知のON/OFFを切り替え
            if (isChecked) {
                String text = notificationTimeMorning.getText().toString();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                try {
                    Date date = format.parse(text);

                    Calendar calendar = Calendar.getInstance();
                    if (date != null) {
                        calendar.setTime(date);
                    }
                    // 時と分を取得
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    // 通知を有効にする処理
                    NotificationScheduler.scheduleNotifications(getApplicationContext(), 0, hour, minute);
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), "エラーが発生しました", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 通知を無効にする処理
                cancelSpecificNotification(197001013); // 通知を取り消す
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        executorService.execute(new DataRead(dbn));
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
