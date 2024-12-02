package com.websarva.wings.android.healthrecords;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HealthRecordsDetail extends AppCompatActivity {
    ExecutorService executorService;
    private DataBaseHealth dbh;
    int dayId;
    //health_level
    RadioGroup health_level_radio;
    RadioButton rbHealth; //健康状態
    //dosage_time_check
    CheckBox morning;
    CheckBox evening;
    CheckBox afternoon;
    boolean dosage_time_morning;    //服薬時間 朝食
    boolean dosage_time_evening;    //服薬時間 昼食
    boolean dosage_time_afternoon;  //服薬時間 夕食
    //health_records_detail;
    EditText inDetail;

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
        dayId = getIntent().getIntExtra("dayId", 0);

        health_level_radio = findViewById(R.id.health_radio);

        morning = findViewById(R.id.morning_checkBox);
        evening = findViewById(R.id.evening_checkBox);
        afternoon = findViewById(R.id.afternoon_checkBox);

        inDetail = findViewById(R.id.record_detail_text);

        //「保存」を押したときの挙動
        findViewById(R.id.check).setOnClickListener(v -> {
            health_level_radio.setOnCheckedChangeListener((group, checkedId) ->
                    rbHealth = findViewById(checkedId));

            dosage_time_morning = morning.isChecked();
            dosage_time_evening = evening.isChecked();
            dosage_time_afternoon = afternoon.isChecked();

            new RecordSave(dbh).execute();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataRead();
    }

    void dataRead() {
        executorService.submit(new DataRead());
    }

    private class DataRead implements Runnable {
        private EntityLevelHealth elh_top;
        private EntityTimeCheck etc_top;
        private EntityDetail ed_top;

        @Override
        public void run() {
            DaoLevelHealth daoLevelHealth = dbh.daoLevelHealth();
            DaoTimeCheck daoTimeCheck = dbh.daoTimeCheck();
            DaoDetail daoDetail = dbh.daoDetail();

            elh_top = daoLevelHealth.getElhById(dayId);
            etc_top = daoTimeCheck.getEtcById(dayId);
            ed_top = daoDetail.getEdById(dayId);

            new Handler(Looper.getMainLooper()).post(this::onPostExecute);
        }

        void onPostExecute() {
            health_level_radio.check(elh_top.getLevel());
            morning.setChecked(etc_top.isMorning());
            evening.setChecked(etc_top.isEvening());
            afternoon.setChecked(etc_top.isAfternoon());
            if (ed_top == null) {
                inDetail.setText("");
            } else {
                inDetail.setText(ed_top.getDetail());
            }
        }
    }

    private class RecordSave implements Runnable {
        private final DataBaseHealth db;

        private RecordSave(DataBaseHealth db) {
            this.db = db;
        }

        @Override
        public void run() {
            DaoLevelHealth dlh = db.daoLevelHealth();
            DaoTimeCheck dtc = db.daoTimeCheck();
            DaoDetail dd = db.daoDetail();
            EntityLevelHealth elh = new EntityLevelHealth(health_level_radio.getCheckedRadioButtonId());
            EntityTimeCheck etc = new EntityTimeCheck(dosage_time_morning, dosage_time_evening, dosage_time_afternoon);
            EntityDetail ed = new EntityDetail(inDetail.getText().toString());

            elh.setId(dayId);
            etc.setId(dayId);
            ed.setId(dayId);

            dlh.insert(elh);
            dtc.insert(etc);
            dd.insert(ed);

            new Handler(Looper.getMainLooper()).post(this::onPostExecute);
        }

        void execute() {
            executorService.submit(new RecordSave(db));
        }

        void onPostExecute() {
            finish();
        }
    }
}
