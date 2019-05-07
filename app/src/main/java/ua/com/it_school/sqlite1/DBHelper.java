package ua.com.it_school.sqlite1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {
    // http://www.sqlitetutorial.net/
    public DBHelper(Context context) {
        super(context, "StudentsDB", null, 1); // конструктор суперкласса
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицу с полями
        db.execSQL("create table students (" +
                "id integer primary key autoincrement," +
                "name text," +
                "address text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}