package ua.com.it_school.sqlite1;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    final String LOG_TAG = "Logs";

    Button btnAdd, btnRead, btnClear, btnUpd, btnDel;
    EditText editName, editAddress, editID, editSearch;

    public static Button button;

    DataBase dataBase;

    DBHelper dbHelper;
    ContentValues cv;
    SQLiteDatabase db;

    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBase = new DataBase(getApplicationContext());

        btnAdd = findViewById(R.id.btnAdd);
        btnRead = findViewById(R.id.btnRead);
        btnClear = findViewById(R.id.btnClear);
        btnUpd = findViewById(R.id.btnUpd);
        btnDel = findViewById(R.id.btnDel);
        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editID = findViewById(R.id.editID);
        editSearch = findViewById(R.id.editSearch);
        button = findViewById(R.id.buttonCreatePeople);

        dbHelper = new DBHelper(this); // создаем объект для создания и управления версиями БД
        cv = new ContentValues(); // создаем объект для данных
    }

    public void addClick(View view) {
        Log.d(LOG_TAG, "Вставка записи в таблицу students: ");
        // получаем данные из полей ввода
        String id = editID.getText().toString();
        String name = editName.getText().toString();
        String address = editAddress.getText().toString();
        // подготовим данные для вставки в виде пар: наименование столбца - значение
        cv.put("name", name);
        cv.put("address", address);

        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        // вставляем запись и получаем ее ID
        db.beginTransaction();
        try {
            long rowID = db.insert("students", null, cv);
            db.setTransactionSuccessful();
            Log.d(LOG_TAG, "Добавлена 1 запись с ID = " + rowID);
        } finally {
            db.endTransaction();
        }

        dbHelper.close(); // закрываем подключение к БД
    }

    public void clearClick(View view) {
        Log.d(LOG_TAG, "Очистка содержимого таблицы students:");

        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        // удаляем все записи
        db.beginTransaction();
        try {
            int deleteCount = db.delete("students", null, null);
            db.setTransactionSuccessful();
            Log.d(LOG_TAG, "Всего записей удалено: " + deleteCount);
        } finally {
            db.endTransaction();
        }

        dbHelper.close(); // закрываем подключение к БД
    }

    public void readClick(View view) {
        Log.d(LOG_TAG, "Выборка всех записей из таблицы students:");

        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        // делаем запрос всех данных из таблицы students, получаем Cursor
        Cursor c = db.query("students", null, null, null, null, null, null);

        // ставим позицию курсора на первую строку выборки. Если в выборке нет строк, вернется false
        if (c.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int addressColIndex = c.getColumnIndex("address");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG, "ID = " + c.getInt(idColIndex) + ", name = " + c.getString(nameColIndex) + ", address = " + c.getString(addressColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 строк");
        c.close();

        dbHelper.close(); // закрываем подключение к БД
    }

    public void updateClick(View view) {
        String id = editID.getText().toString();
        if (id.equalsIgnoreCase(""))
            return;

        String name = editName.getText().toString();
        String address = editAddress.getText().toString();

        Log.d(LOG_TAG, "Обновление записей таблицы по условию: ");
        // подготовим значения для обновления
        cv.put("name", name);
        cv.put("address", address);

        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        // обновляем по id
        db.beginTransaction();
        try {
            int updCount = db.update("students", cv, "id = ?", new String[]{id});
            db.setTransactionSuccessful();
            Log.d(LOG_TAG, "Всего записей обновлено: " + updCount);
        } finally {
            db.endTransaction();
        }

        dbHelper.close(); // закрываем подключение к БД
    }

    public void deleteClick(View view) {
        String id = editID.getText().toString();

        if (id.equalsIgnoreCase(""))
            return;

        Log.d(LOG_TAG, "Удалить записи из таблицы students, у которых ID = " + id);

        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        // удаляем по id
        int delCount = db.delete("students", "id = " + id, null);
        Log.d(LOG_TAG, "Всего записей удалено: " + delCount);

        // закрываем подключение к БД
        dbHelper.close();
    }

    public void searchClick(View view) {
        db = dbHelper.getWritableDatabase(); // подключаемся к БД

        Cursor c = null; // курсор для получения результатов

        // переменные для параметров запроса query
        String[] columns = null;
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String queryParam = "%" + editSearch.getText().toString() + "%";

        Log.d(LOG_TAG, "Записи, в которых поле Имя или Адрес содержит: " + queryParam);
        selection = " name LIKE ? or address LIKE ?";
        selectionArgs = new String[]{queryParam, queryParam};

        c = db.query("students", null, selection, selectionArgs, null, null, null);

        // обработка результатов запроса
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames())
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");

                    Log.d(LOG_TAG, str);

                } while (c.moveToNext());
            }
            c.close();
        } else
            Log.d(LOG_TAG, "Нет результатов (курсор пуст)");


        dbHelper.close(); // закрываем подключение к БД

    }


    public void OnPeopleCreate(View view) {


        button.setVisibility(View.INVISIBLE);

        //

        //
        DataBase.isRunning = true;
        //while(DataBase.isRunning)
        {
            new CountDownTimer(100, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    System.out.println(millisUntilFinished);

                    if (i == 0) {
                        dataBase.Insert();
                        i++;
                    }

                }

                @Override
                public void onFinish() {
                    if (!DataBase.isRunning) {
                        button.setVisibility(View.VISIBLE);
                        i = 0;
                    }
                }
            }.start();
        }
        //
        //readClick(view);
    }


}