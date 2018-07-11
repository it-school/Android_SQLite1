package ua.com.it_school.sqlite1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    final String LOG_TAG = "Logs";

    Button btnAdd, btnRead, btnClear, btnUpd, btnDel;
    EditText editName, editAddress, editID, editSearch;

    DBHelper dbHelper;
    ContentValues cv;
    SQLiteDatabase db;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        btnRead = findViewById(R.id.btnRead);
        btnClear = findViewById(R.id.btnClear);
        btnUpd = findViewById(R.id.btnUpd);
        btnDel = findViewById(R.id.btnDel);
        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editID = findViewById(R.id.editID);
        editSearch = findViewById(R.id.editSearch);

        dbHelper = new DBHelper(this); // создаем объект для создания и управления версиями БД
        cv = new ContentValues(); // создаем объект для данных
    }

    public void addClick(View view)
    {
        Log.d(LOG_TAG, "--- Insert in students: ---");
        // получаем данные из полей ввода
        String id = editID.getText().toString();
        String name = editName.getText().toString();
        String address = editAddress.getText().toString();
        // подготовим данные для вставки в виде пар: наименование столбца - значение
        cv.put("name", name);
        cv.put("address", address);

        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        // вставляем запись и получаем ее ID
        long rowID = db.insert("students", null, cv);
        Log.d(LOG_TAG, "row inserted, ID = " + rowID);

        dbHelper.close(); // закрываем подключение к БД
    }

    public void clearClick(View view)
    {
        Log.d(LOG_TAG, "--- Clear students: ---");

        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        // удаляем все записи
        int clearCount = db.delete("students", null, null);
        Log.d(LOG_TAG, "deleted rows count = " + clearCount);

        dbHelper.close(); // закрываем подключение к БД
    }

    public void readClick(View view)
    {
        Log.d(LOG_TAG, "--- Rows in students: ---");

        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        // делаем запрос всех данных из таблицы students, получаем Cursor
        Cursor c = db.query("students", null, null, null, null, null, null);

        // ставим позицию курсора на первую строку выборки. Если в выборке нет строк, вернется false
        if (c.moveToFirst())
        {
            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int addressColIndex = c.getColumnIndex("address");

            do
            {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG, "ID = " + c.getInt(idColIndex) + ", name = " + c.getString(nameColIndex) + ", address = " + c.getString(addressColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        }
        else
            Log.d(LOG_TAG, "0 rows");
        c.close();

        dbHelper.close(); // закрываем подключение к БД
    }

    public void updateClick(View view)
    {
        String id = editID.getText().toString();
        if (id.equalsIgnoreCase(""))
            return;

        String name = editName.getText().toString();
        String address = editAddress.getText().toString();

        Log.d(LOG_TAG, "--- Update students: ---");
        // подготовим значения для обновления
        cv.put("name", name);
        cv.put("address", address);

        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        // обновляем по id
        int updCount = db.update("students", cv, "id = ?",
                new String[] { id });
        Log.d(LOG_TAG, "updated rows count = " + updCount);

        dbHelper.close(); // закрываем подключение к БД
    }

    public void deleteClick(View view)
    {
        String id = editID.getText().toString();

        if (id.equalsIgnoreCase(""))
            return;

        Log.d(LOG_TAG, "--- Delete from students record with ID = " + id + ": ---");

        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        // удаляем по id
        int delCount = db.delete("students", "id = " + id, null);
        Log.d(LOG_TAG, "deleted rows count = " + delCount);

        // закрываем подключение к БД
        dbHelper.close();
    }

    public void searchClick(View view)
    {
        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        // курсор
        Cursor c = null;

        // переменные для query
        String[] columns = null;
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String queryParam = "%"+editSearch.getText().toString()+"%";

        Log.d(LOG_TAG, "--- Имя или адрес содержит: " + queryParam + " ---");
        selection = " name LIKE ? or address LIKE ?";
        selectionArgs = new String[] { queryParam, queryParam};

        c = db.query("students", null, selection, selectionArgs, null, null,null);

        // обработка результатов запроса
        if (c != null)
        {
            if (c.moveToFirst())
            {
                String str;
                do
                {
                    str = "";
                    for (String cn : c.getColumnNames())
                    {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);

                } while (c.moveToNext());
            }
            c.close();
        } else
            Log.d(LOG_TAG, "Cursor is null");


        dbHelper.close(); // закрываем подключение к БД
    }

    class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper(Context context)
        {
            super(context, "StudentsDB", null, 1); // конструктор суперкласса
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            Log.d(LOG_TAG, "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table students ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "address text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {

        }
    }
}