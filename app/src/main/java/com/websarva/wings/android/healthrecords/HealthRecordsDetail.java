package com.websarva.wings.android.healthrecords;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.websarva.wings.android.healthrecords.DataBase.AppDataBase;
import com.websarva.wings.android.healthrecords.DataBase.AppDataBaseSingleton;
import com.websarva.wings.android.healthrecords.DataBase.RecordDao;
import com.websarva.wings.android.healthrecords.DataBase.RecordEntity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HealthRecordsDetail extends AppCompatActivity {
    List<RecordEntity> recordEntityList;
    Intent intent;
    private AppDataBase adb;
    //health_level
    RadioGroup health_level_radio;
    RadioButton rbHealth; //健康状態
    RadioButton worst;
    RadioButton bad;
    RadioButton normal;
    RadioButton good;
    RadioButton best;
    //dosage_time_check
    boolean dosage_time_morning;    //服薬時間 朝食
    boolean dosage_time_evening;    //服薬時間 昼食
    boolean dosage_time_afternoon;  //服薬時間 夕食
    //health_records_detail;
    ExecutorService executorService;
    RadioGroup inHealth_level;
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

        adb = AppDataBaseSingleton.getInstance(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        //「保存」を押したときの挙動
        findViewById(R.id.check).setOnClickListener(v -> {
            intent = new Intent();
            health_level_radio = findViewById(R.id.health_radio);
            health_level_radio.setOnCheckedChangeListener((group, checkedId) -> {
                rbHealth = findViewById(checkedId);
                intent.putExtra("health_level", rbHealth.getUrls());         //RadioButtonのIDをセット
            });

            dosage_time_morning = findViewById(R.id.morning_checkBox).isPressed();
            dosage_time_evening = findViewById(R.id.evening_checkBox).isPressed();
            dosage_time_afternoon = findViewById(R.id.afternoon_checkBox).isPressed();

            intent.putExtra("dosage_time_morning", dosage_time_morning);      //morningのCheckBoxの値をセット
            intent.putExtra("dosage_time_evening", dosage_time_evening);      //eveningのCheckBoxの値をセット
            intent.putExtra("dosage_time_afternoon", dosage_time_afternoon);  //afternoonのCheckBoxの値をセット

            inDetail = findViewById(R.id.record_detail_text).toString();
            intent.putExtra("detail", inDetail);                              //detailをセット

            new RecordSave(adb, HealthRecordsDetail.this).execute(intent);
        });
    }

    private class RecordSave implements Runnable {
        private final WeakReference<Activity> weakActivity;
        private final AppDataBase db;

        private RecordSave(AppDataBase db, Activity activity) {
            this.db = db;
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            RecordDao recordDao = db.recordDao();
            RecordEntity re_insert = new RecordEntity(rbHealth.getId(), rbHealth.isChecked(), dosage_time_morning, dosage_time_evening, dosage_time_afternoon, inDetail);
            long id = recordDao.insert(re_insert);

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(id));
        }

        void execute(Intent intent) {
            onPreExecute(intent);
            executorService.submit(new RecordSave(db, weakActivity.get()));
        }

        void onPreExecute(Intent intent) {
            //前処理

        }

        void onPostExecute(long RE_id) {
            intent.putExtra("setId", RE_id);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
