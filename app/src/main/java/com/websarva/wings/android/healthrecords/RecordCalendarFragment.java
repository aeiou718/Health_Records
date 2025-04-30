package com.websarva.wings.android.healthrecords;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class RecordCalendarFragment extends Fragment {
    private static final String ARG_CALENDAR = "calendar";
    private Calendar calendar;
    private DataBaseHealth dbh;
    private ExecutorService executorService;
    private RecyclerView recyclerView;

    public static RecordCalendarFragment newInstance(Calendar calendar) {
        RecordCalendarFragment fragment = new RecordCalendarFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CALENDAR, calendar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            calendar = (Calendar) getArguments().getSerializable(ARG_CALENDAR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_calendar_list, container, false);
        recyclerView = view.findViewById(R.id.calendar_view_pager);
        executorService = Executors.newSingleThreadExecutor();
        dbh = DataBaseHealthSingleton.getInstance(requireContext());
        return view;
    }

    @Override
    public void onResume() {
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
            Calendar tempCalendar = (Calendar) calendar.clone();
            int dayObSat = tempCalendar.get(Calendar.DAY_OF_WEEK);
            while (dayObSat != Calendar.SATURDAY) {
                tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
                dayObSat = tempCalendar.get(Calendar.DAY_OF_WEEK);
            }

            int year = tempCalendar.get(Calendar.YEAR);
            int month = tempCalendar.get(Calendar.MONTH);
            int day = tempCalendar.get(Calendar.DAY_OF_MONTH);
            currentDate = year * 10000 + (month + 1) * 100 + day;

            int dayObSun = tempCalendar.get(Calendar.DAY_OF_WEEK);
            while (dayObSun != Calendar.SUNDAY) {
                tempCalendar.add(Calendar.DAY_OF_MONTH, -1);
                dayObSun = tempCalendar.get(Calendar.DAY_OF_WEEK);
            }

            int yearW = tempCalendar.get(Calendar.YEAR);
            int monthW = tempCalendar.get(Calendar.MONTH);
            int dayW = tempCalendar.get(Calendar.DAY_OF_MONTH);
            startDate = yearW * 10000 + (monthW + 1) * 100 + dayW;
        }

        @Override
        public void run() {
            DaoLevelHealth daoLevelHealth = dbh.daoLevelHealth();
            DaoTimeCheck daoTimeCheck = dbh.daoTimeCheck();

            for (int i = startDate; i <= currentDate; i++) {
                EntityLevelHealth elh_top = daoLevelHealth.getElhById(i);
            }
        }
    }
}