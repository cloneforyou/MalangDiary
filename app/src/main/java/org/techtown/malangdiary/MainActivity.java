package org.techtown.malangdiary;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    SQLiteDatabase database;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialCalendarView materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator()
        );

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener(){
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                writeOrViewDiary(timeFormat.format(date.getDate()));
            }
        });
    }

    public void todayDiary(View v) {
        Date today = new Date();
        String todayString = timeFormat.format(today);
        writeOrViewDiary(todayString);
    }

    public void writeOrViewDiary(String date){
        database  = openOrCreateDatabase("Diary.db", MODE_PRIVATE, null);
        String CREATE_SQL = "CREATE TABLE IF NOT EXISTS DIARIES (DATE TEXT PRIMARY KEY, TITLE TEXT, CONTENT TEXT, IMG BLOB);";
        database.execSQL(CREATE_SQL);

        String SELECT_SQL = "SELECT DATE FROM DIARIES WHERE DATE='" + date + "';";
        Cursor c = database.rawQuery(SELECT_SQL, null);
        int count = c.getCount();
        c.close();

        if(count==1) {
            Intent viewDiary = new Intent(MainActivity.this, DiaryViewActivity.class);
            viewDiary.putExtra("date", date);
            startActivityForResult(viewDiary, 101);
        }
        else {
            Intent writeDiary = new Intent(MainActivity.this, DiaryActivity.class);
            writeDiary.putExtra("date", date);
            startActivityForResult(writeDiary, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && data != null) {
            String alarm = data.getStringExtra("alarm");
            Toast.makeText(getApplicationContext(), " " + alarm, Toast.LENGTH_LONG).show();
        }
    }
}
