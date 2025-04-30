package com.websarva.wings.android.healthrecords;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Calendar;

public class CalendarPagerAdapter extends FragmentStateAdapter {
    private Calendar currentCalendar; // 現在の日付を管理する

    public CalendarPagerAdapter(FragmentActivity fa) {
        super(fa);
        currentCalendar = Calendar.getInstance();
    }

    @Override
    public Fragment createFragment(int position) {
        // position に基づいてFragmentを生成する処理
        // ここで一週間分のデータを渡す
        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.add(Calendar.WEEK_OF_YEAR, position - Integer.MAX_VALUE / 2);
        return RecordCalendarFragment.newInstance(calendar);
    }

    @Override
    public int getItemCount() {
        // ページ数を決める
        return Integer.MAX_VALUE;
    }

    public void setCurrentCalendar(Calendar calendar) {
        currentCalendar = calendar;
        notifyDataSetChanged();
    }

}