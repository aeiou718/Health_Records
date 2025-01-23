package com.websarva.wings.android.healthrecords;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.websarva.wings.android.healthrecords.DataBase.DaoLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.DaoTimeCheck;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealth;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealthSingleton;
import com.websarva.wings.android.healthrecords.DataBase.EntityLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.EntityTimeCheck;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordCalendarList extends AppCompatActivity {
    private DataBaseHealth dbh;
    ExecutorService executorService;
    Calendar calendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.record_calendar_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.record_calendar_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        executorService = Executors.newSingleThreadExecutor();
        dbh = DataBaseHealthSingleton.getInstance(getApplicationContext());
//ツールバー
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> finish());

        calendar = Calendar.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataRead();
    }

    void dataRead() {
        executorService.submit(new DataRead());
    }

    class DataRead implements Runnable {
        private List<EntityLevelHealth> levelHealthList;
        private List<EntityTimeCheck> timeCheckList;
        private final int currentDate;   //カレンダー
        private final int startDate;

        DataRead() {
            // 日曜日の値を入手
            int dayObSat = calendar.get(Calendar.DAY_OF_WEEK);
            while (dayObSat != Calendar.SATURDAY) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                dayObSat = calendar.get(Calendar.DAY_OF_WEEK);
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            currentDate = year * 10000 + (month + 1) * 100 + day;

            int dayObSun = calendar.get(Calendar.DAY_OF_WEEK);
            while (dayObSun != Calendar.SUNDAY) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                dayObSun = calendar.get(Calendar.DAY_OF_WEEK);
            }

            int yearW = calendar.get(Calendar.YEAR);
            int monthW = calendar.get(Calendar.MONTH);
            int dayW = calendar.get(Calendar.DAY_OF_MONTH);
            startDate = yearW * 10000 + (monthW + 1) * 100 + dayW;

            Log.d("日付current", String.valueOf(currentDate));
            Log.d("日付start", String.valueOf(startDate));


        }

        @Override
        public void run() {
            DaoLevelHealth daoLevelHealth = dbh.daoLevelHealth();
            DaoTimeCheck daoTimeCheck = dbh.daoTimeCheck();

            for (int i = startDate; i <= currentDate; i++) {
                EntityLevelHealth elh_top = daoLevelHealth.getElhById(i);
                EntityTimeCheck etc_top = daoTimeCheck.getEtcById(i);
                if (elh_top == null) {
                    elh_top = new EntityLevelHealth(-1);
                    elh_top.setId(i);
                    daoLevelHealth.insert(elh_top);
                }
                if (etc_top == null) {
                    etc_top = new EntityTimeCheck(false, false, false);
                    etc_top.setId(i);
                    daoTimeCheck.insert(etc_top);
                }
            }
            // ラジオボタン
            levelHealthList = daoLevelHealth.getElhForWeek(startDate, currentDate);
            // チェックボックス
            timeCheckList = daoTimeCheck.getTimeCheckForWeek(startDate, currentDate);

            new Handler(Looper.getMainLooper()).post(this::onPostExecute);
        }

        void onPostExecute() {
            RecyclerView recyclerView = findViewById(R.id.health_records_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(RecordCalendarList.this));
            ViewAdapter adapter = new ViewAdapter(getApplicationContext(), levelHealthList, timeCheckList);
            recyclerView.setAdapter(adapter);
        }
    }
}