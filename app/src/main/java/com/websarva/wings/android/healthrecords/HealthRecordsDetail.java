package com.websarva.wings.android.healthrecords;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HealthRecordsDetail extends AppCompatActivity {
    RadioGroup health_level;   //健康状態
    boolean dosage_time_morning;    //服薬時間 朝食
    boolean dosage_time_evening;    //服薬時間 昼食
    boolean dosage_time_afternoon;  //服薬時間 夕食
    String detail;                  //詳細

    ExecutorService executorService;
    RadioGroup inHealth_level;
    boolean inDosage_time_morning = true;
    boolean inDosage_time_evening = true;
    boolean inDosage_time_afternoon = true;
    String inDetail = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.health_records_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recorder_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppDataBase adb = AppDataBaseSingleton.getInstance(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        //「保存」を押したときの挙動
        findViewById(R.id.check).setOnClickListener(v -> {
            findViewById< CheckBox>(R.id.morning_check)
            dosage_time_evening;
            dosage_time_afternoon;
            health_level = findViewById(R.id.health_radio);
            health_level.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton rbhealth = findViewById(checkedId);

            });
        });
    }

    private class DataStore implements Runnable {
        private final WeakReference<Activity> weakActivity;
        private final AppDataBase db;

        private DataStore(AppDataBase db, Activity activity) {
            this.db = db;
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            RecordDao recordDao = db.recordDao();
            RecordEntity re_insert = new RecordEntity();

            new Handler(Looper.getMainLooper())
                    .post(() -> onPostExecute());
        }

        void execute(Intent intent) {
            onPreExecute(intent);
            executorService.submit(new DataStore(db, weakActivity.get()));
        }

        void onPreExecute(Intent intent) {
            //前処理

        }

        void onPostExecute() {
            finish();
        }
    }
}
