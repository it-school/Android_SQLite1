package ua.com.it_school.sqlite1;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

public class DataBase extends AsyncTask<Void, Void, Void> {
    public static boolean isRunning = false;
    Context appContext;

    public DataBase(Context appContext) {
        this.appContext = appContext;
    }

    public void Insert() {
        ContentValues cv = new ContentValues();
        DBHelper dbHelper = new DBHelper(appContext); // создаем объект для создания и управления версиями БД
        SQLiteDatabase db;

        String name = "";
        String address = "";

        double timer = SystemClock.currentThreadTimeMillis();

        for (int i = 1; i <= 10000; i++) {
            name = "";
            for (int j = 0; j < 7; j++) {
                name += (char) (65 + Math.random() * 26);
            }
            address = "";
            for (int j = 0; j < 15; j++) {
                address += (char) (97 + Math.random() * 26);
            }
            cv.put("name", name);
            cv.put("address", address);

            db = dbHelper.getWritableDatabase(); // подключаемся к БД

            // вставляем запись и получаем ее ID
            db.beginTransaction();
            try {
                long rowID = db.insert("students", null, cv);
                db.setTransactionSuccessful();
                //Log.d(LOG_TAG, "Добавлена 1 запись с ID = " + rowID);
            } finally {
                db.endTransaction();
                isRunning = false;
            }
        }
        dbHelper.close(); // закрываем подключение к БД
        Log.d("TimerLog: ", SystemClock.currentThreadTimeMillis() - timer + "");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Insert();
        return null;
    }
}
