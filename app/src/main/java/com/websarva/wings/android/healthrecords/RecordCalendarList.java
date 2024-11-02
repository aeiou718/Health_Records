package com.websarva.wings.android.healthrecords;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.websarva.wings.android.healthrecords.DataBase.AppDataBase;
import com.websarva.wings.android.healthrecords.DataBase.RecordDao;
import com.websarva.wings.android.healthrecords.DataBase.RecordEntity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordCalendarList extends AppCompatActivity {
    private AppDataBase adb;
    private ListView recordList;
    ExecutorService executorService;
    List<RecordEntity> recordEntityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.record_calendar_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        executorService = Executors.newSingleThreadExecutor();

    }

    @Override
    protected void onResume() {
        super.onResume();
        adb = Room.databaseBuilder(getApplicationContext(),
                AppDataBase.class, "health_records_db").allowMainThreadQueries().build();
        new DataRead(adb, RecordCalendarList.this).execute();
        // recordEntityListがnullにならないようにする
        recordEntityList = adb.recordDao().getAll(); // 修正箇所
        updateList();
    }


    class DataRead implements Runnable {
        private final WeakReference<Activity> weakActivity;
        private final AppDataBase db;

        public DataRead(AppDataBase db, Activity activity) {
            this.db = db;
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            RecordDao recordDao = db.recordDao();
            recordEntityList = recordDao.getAll();

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(recordEntityList));
        }

        void execute() {
            executorService.submit(new DataRead(db, weakActivity.get()));
        }

        void onPostExecute(List<RecordEntity> recordEntityList) {
            updateList();
            recordEntityList.sort((o1, o2) -> o1.getId() - o2.getId());
        }
    }

    void updateList() {
        RecordAdapter adapter = new RecordAdapter(RecordCalendarList.this, recordEntityList);
        recordList = findViewById(R.id.health_records_list);
        recordList.setAdapter(adapter);
    }

    private class RecordAdapter extends BaseAdapter {
        private final List<RecordEntity> recordList;
        private final LayoutInflater inflater;

        public RecordAdapter(Context context, List<RecordEntity> recordList) {
            this.recordList = recordList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return recordList.size();
        }

        @Override
        public Object getItem(int position) {
            return recordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return recordList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecordEntity item = recordList.get(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.parts_health_records_day, parent, false);
            }

            // radio button button button
            RadioGroup radioGroup = convertView.findViewById(R.id.health_radio);
            radioGroup.setId(item.getHealth_id());
            CheckBox morning = convertView.findViewById(R.id.morning);
            morning.setChecked(item.isMorning());
            CheckBox evening = convertView.findViewById(R.id.evening);
            evening.setChecked(item.isEvening());
            CheckBox afternoon = convertView.findViewById(R.id.afternoon);
            afternoon.setChecked(item.isAfternoon());

            // day text
            // ここにrecord_dayに表示する値を入れる
            TextView dayText = convertView.findViewById(R.id.record_day);
            dayText.setText("00");

            return convertView;
        }
    }
}