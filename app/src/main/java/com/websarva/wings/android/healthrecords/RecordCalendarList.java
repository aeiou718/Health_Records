package com.websarva.wings.android.healthrecords;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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