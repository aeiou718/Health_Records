package com.websarva.wings.android.healthrecords;

import android.app.Activity;
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

import com.websarva.wings.android.healthrecords.DataBase.DaoDetail;
import com.websarva.wings.android.healthrecords.DataBase.DaoLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.DaoTimeCheck;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealth;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealthSingleton;
import com.websarva.wings.android.healthrecords.DataBase.EntityDetail;
import com.websarva.wings.android.healthrecords.DataBase.EntityLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.EntityTimeCheck;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HealthRecordsDetail extends AppCompatActivity {
    ExecutorService executorService;
    private DataBaseHealth dbh;
    //health_level
    RadioGroup health_level_radio;
    RadioButton rbHealth; //健康状態
    //dosage_time_check
    boolean dosage_time_morning;    //服薬時間 朝食
    boolean dosage_time_evening;    //服薬時間 昼食
    boolean dosage_time_afternoon;  //服薬時間 夕食
    //health_records_detail;
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

        dbh = DataBaseHealthSingleton.getInstance(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        //「保存」を押したときの挙動
        findViewById(R.id.check).setOnClickListener(v -> {
            health_level_radio = findViewById(R.id.health_radio);
            health_level_radio.setOnCheckedChangeListener((group, checkedId) ->
                    rbHealth = findViewById(checkedId));

            dosage_time_morning = findViewById(R.id.morning_checkBox).isPressed();
            dosage_time_evening = findViewById(R.id.evening_checkBox).isPressed();
            dosage_time_afternoon = findViewById(R.id.afternoon_checkBox).isPressed();

            inDetail = findViewById(R.id.record_detail_text).toString();

            new RecordSave(dbh, HealthRecordsDetail.this).execute();
        });
    }

    private class RecordSave implements Runnable {
        private final WeakReference<Activity> weakActivity;
        private final DataBaseHealth db;

        private RecordSave(DataBaseHealth db, Activity activity) {
            this.db = db;
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            DaoLevelHealth dlh = db.daoLevelHealth();
            DaoTimeCheck dtc = db.daoTimeCheck();
            DaoDetail dd = db.daoDetail();
            EntityLevelHealth elh = new EntityLevelHealth(health_level_radio.getCheckedRadioButtonId());
            EntityTimeCheck etc = new EntityTimeCheck(dosage_time_morning, dosage_time_evening, dosage_time_afternoon);
            EntityDetail ed = new EntityDetail(inDetail);

            elh.setId(new RecordCalendar().dayId);
            etc.setId(new RecordCalendar().dayId);
            ed.setId(new RecordCalendar().dayId);

            dlh.update(elh);
            dtc.insert(etc);
            dd.update(ed);

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute());
        }

        void execute() {
            executorService.submit(new RecordSave(db, weakActivity.get()));
        }

        void onPostExecute() {
            finish();
        }
    }
}
