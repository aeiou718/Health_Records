package com.websarva.wings.android.healthrecords;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HealthRecordsDetail extends AppCompatActivity {
    List<RecordEntity> recordEntityList;
    Intent intent;
    //health_level
    RadioGroup health_level;   //健康状態
    RadioButton worst;
    RadioButton bad;
    RadioButton normal;
    RadioButton good;
    RadioButton best;
    //dosage_time_check
    CheckBox dosage_time_morning;    //服薬時間 朝食
    CheckBox dosage_time_evening;    //服薬時間 昼食
    CheckBox dosage_time_afternoon;  //服薬時間 夕食
    //health_records_detail;
    ExecutorService executorService;
    RadioGroup inHealth_level;
    CheckBox inDosage_time_morning;
    CheckBox inDosage_time_evening;
    CheckBox inDosage_time_afternoon;
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
            intent = new Intent();
            health_level = findViewById(R.id.health_radio);
            health_level.setOnCheckedChangeListener((group, checkedId) -> {
                worst = findViewById(R.id.worst);
                bad = findViewById(R.id.bad);
                normal = findViewById(R.id.normal);
                good = findViewById(R.id.good);
                best = findViewById(R.id.best);

            });
            inDosage_time_morning = findViewById(R.id.morning_checkBox);

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

        void onPostExecute(List<RecordEntity> atList) {
            recordEntityList = atList;
            HealthLevelAdapter adapter = new HealthLevelAdapter(HealthRecordsDetail.this, recordEntityList);
            health_level.setAdapter(adapter);

        }
    }

    void updateList() {
        HealthLevelAdapter adapter = new HealthLevelAdapter(HealthRecordsDetail.this, recordEntityList);

    }

    private class HealthLevelAdapter extends BaseAdapter {
        private final List<RecordEntity> recordEntityList;
        private final LayoutInflater inflater;

        private HealthLevelAdapter(Context context, List<RecordEntity> recordEntityList) {
            this.recordEntityList = recordEntityList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return recordEntityList.size();
        }

        @Override
        public Object getItem(int position) {
            return recordEntityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return recordEntityList.get(position).getId();
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecordEntity record = recordEntityList.get(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.health_level, null);
            }
            inHealth_level = convertView.findViewById(R.id.health_radio);
            View finalConvertView = convertView;
            inHealth_level.setOnCheckedChangeListener((group, checkedId) -> {
                worst = finalConvertView.findViewById(R.id.worst);
                bad = finalConvertView.findViewById(R.id.bad);
                normal = finalConvertView.findViewById(R.id.normal);
                good = finalConvertView.findViewById(R.id.good);
                best = finalConvertView.findViewById(R.id.best);

            });

            return convertView;
        }
    }
}
