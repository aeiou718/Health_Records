package com.websarva.wings.android.healthrecords;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.websarva.wings.android.healthrecords.DataBase.AppDataBase;
import com.websarva.wings.android.healthrecords.DataBase.RecordDao;
import com.websarva.wings.android.healthrecords.DataBase.RecordEntity;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordCalendar extends AppCompatActivity {
    private AppDataBase adb;
    private ListView recordList;
    ExecutorService executorService;
    List<RecordEntity> recordEntityList;
    Calendar calendar;
    long currentDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.record_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.record_calendar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        executorService = Executors.newSingleThreadExecutor();
        adb = Room.databaseBuilder(getApplicationContext(),
                AppDataBase.class, "health_records_db").allowMainThreadQueries().build();

        calendar = Calendar.getInstance();
        currentDate = calendar.getTimeInMillis();
        CalendarView healthCalendar = findViewById(R.id.health_calendar);
        healthCalendar.setDate(currentDate);

        RadioGroup radioGroup = findViewById(R.id.health_radio);
        //　　チェック状態
        //  radioGroup.check(R.id.normal);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            Log.d("health_level", String.valueOf(checkedId));
        });

        CheckBox morning = findViewById(R.id.morning_checkBox);
        CheckBox evening = findViewById(R.id.evening_checkBox);
        CheckBox afternoon = findViewById(R.id.afternoon_checkBox);
        morning.setOnClickListener(v -> {
            Log.d("morning", String.valueOf(morning.isChecked()));
        });
        evening.setOnClickListener(v -> {
            Log.d("evening", String.valueOf(evening.isChecked()));
        });
        afternoon.setOnClickListener(v -> {
            Log.d("afternoon", String.valueOf(afternoon.isChecked()));
        });
    }

    class DataSave implements Runnable {
        private final WeakReference<Activity> weakActivity;
        private final AppDataBase db;

        public DataSave(AppDataBase db, Activity activity) {
            this.db = db;
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            RecordDao recordDao = db.recordDao();
//            RecordEntity recordEntity = new RecordEntity();
//            recordDao.update(recordEntity);
            recordEntityList = recordDao.getAll();

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(recordEntityList));
        }

        void onPreExecute() {

        }

        void execute() {
            onPreExecute();
            executorService.submit(new DataSave(db, weakActivity.get()));
        }

        void onPostExecute(List<RecordEntity> recordEntityList) {

        }

    }
}