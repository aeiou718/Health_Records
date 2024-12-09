package com.websarva.wings.android.healthrecords;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.websarva.wings.android.healthrecords.DataBase.DaoLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.DaoTimeCheck;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealth;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealthSingleton;
import com.websarva.wings.android.healthrecords.DataBase.EntityLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.EntityTimeCheck;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {
    private final List<EntityLevelHealth> tlh_list;
    private final List<EntityTimeCheck> tlc_list;
    List<String> day = new ArrayList<>();
    List<String> week = new ArrayList<>();
    ExecutorService executorService;
    DataBaseHealth dbh;

    public ViewAdapter(Context context, List<EntityLevelHealth> tlhList, List<EntityTimeCheck> tlcList) {
        dbh = DataBaseHealthSingleton.getInstance(context.getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();
        tlh_list = tlhList;
        tlc_list = tlcList;
        weekDaySt();
        weekSt();
        setHasStableIds(true);
    }

    String dayOfFormatter(Calendar calendar) {
        Instant instant = calendar.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
        return zonedDateTime.format(formatter);
    }

    void weekDaySt() {
        Calendar calendar = Calendar.getInstance();
        int dayObSun = calendar.get(Calendar.DAY_OF_WEEK);
        while (dayObSun != Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            dayObSun = calendar.get(Calendar.DAY_OF_WEEK);
        }
        String dayOfFormatter = dayOfFormatter(calendar);
        day.add(dayOfFormatter);
        Log.d("day", String.valueOf(day));
        while (dayObSun != Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dayObSun = calendar.get(Calendar.DAY_OF_WEEK);
            dayOfFormatter = dayOfFormatter(calendar);
            day.add(dayOfFormatter);
            Log.d("day", String.valueOf(day));
        }
    }

    void weekSt() {
        week.add("日");
        week.add("月");
        week.add("火");
        week.add("水");
        week.add("木");
        week.add("金");
        week.add("土");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parts_health_records_day, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemId = view.getId();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.radioGroup.check(tlh_list.get(position).getLevel());
        holder.morning.setChecked(tlc_list.get(position).isMorning());
        holder.evening.setChecked(tlc_list.get(position).isEvening());
        holder.afternoon.setChecked(tlc_list.get(position).isAfternoon());
        holder.textDay.setText(String.valueOf(day.get(position)));
        holder.textWeek.setText(week.get(position));

        holder.radioGroup.setOnCheckedChangeListener((group, checkedId) ->
                executorService.submit(new LevelHealthData(tlh_list.get(position).getId(), dbh, new EntityLevelHealth(checkedId))));
        holder.morning.setOnCheckedChangeListener((buttonView, isChecked) ->
                executorService.submit(new CheckBoxData(tlc_list.get(position).getId(), dbh, new EntityTimeCheck(holder.morning.isChecked(), holder.evening.isChecked(), holder.afternoon.isChecked()))));
        holder.evening.setOnCheckedChangeListener((buttonView, isChecked) ->
                executorService.submit(new CheckBoxData(tlc_list.get(position).getId(), dbh, new EntityTimeCheck(holder.morning.isChecked(), holder.evening.isChecked(), holder.afternoon.isChecked()))));
        holder.afternoon.setOnCheckedChangeListener((buttonView, isChecked) ->
                executorService.submit(new CheckBoxData(tlc_list.get(position).getId(), dbh, new EntityTimeCheck(holder.morning.isChecked(), holder.evening.isChecked(), holder.afternoon.isChecked()))));
    }

    @Override
    public long getItemId(int position) {
        return tlh_list.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return day.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int itemId;
        RadioGroup radioGroup;
        CheckBox morning;
        CheckBox evening;
        CheckBox afternoon;
        TextView textDay;
        TextView textWeek;
        ExecutorService executorService;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioGroup = itemView.findViewById(R.id.health_radio);
            morning = itemView.findViewById(R.id.morning_checkBox);
            evening = itemView.findViewById(R.id.evening_checkBox);
            afternoon = itemView.findViewById(R.id.afternoon_checkBox);
            textDay = itemView.findViewById(R.id.record_day);
            textWeek = itemView.findViewById(R.id.day_of_week);
            executorService = Executors.newSingleThreadExecutor();

        }
    }

    private static class LevelHealthData implements Runnable {
        private final DataBaseHealth db;
        EntityLevelHealth entityLevelHealth;
        int dayId;

        public LevelHealthData(int dayId, DataBaseHealth db, EntityLevelHealth entityLevelHealth) {
            this.dayId = dayId;
            this.db = db;
            this.entityLevelHealth = entityLevelHealth;
        }

        @Override
        public void run() {
            DaoLevelHealth daoLevelHealth = db.daoLevelHealth();
            entityLevelHealth.setId(dayId);
            daoLevelHealth.insert(entityLevelHealth);
        }
    }

    //CheckBoxのデータベース
    private static class CheckBoxData implements Runnable {
        private final DataBaseHealth db;
        EntityTimeCheck entityTimeCheck;
        int dayId;

        public CheckBoxData(int dayId, DataBaseHealth db, EntityTimeCheck entityTimeCheck) {
            this.dayId = dayId;
            this.db = db;
            this.entityTimeCheck = entityTimeCheck;
        }

        @Override
        public void run() {
            DaoTimeCheck daoTimeCheck = db.daoTimeCheck();
            entityTimeCheck.setId(dayId);
            daoTimeCheck.insert(entityTimeCheck);
        }
    }
}
