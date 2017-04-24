package com.example.mac.yandexdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Инициализируем наши элементы данных
    public static final String DATABASE_NAME = "mylist.db";
    public static final String TABLE_FAVS = "mylist_data";
    public static final String COL1 = "ID";
    public static final String COL2 = "ITEM1";
    public static final String COL3 = "ITEM_FAV";
    public static final String TABLE_NAME = "mylist_data";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //создаем таблицу
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ITEM_FAV BLOB" + " ITEM1 TEXT)";
        db.execSQL(createTable);
    }

    @Override
    //запрос в базу данных на уничтожение таблиц
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        //создания версии таблицы с обновленной структурой
        onCreate(db);
    }
     //// добавление начальных данных
    public boolean addData(String item1) {
        DatabaseHelper myDB = null;
        SQLiteDatabase db;
        //Вызов метода getWritableDatabase() может завершиться неудачно из-за проблем с полномочиями или нехваткой места на диске, поэтому лучше предусмотреть откат к методу getReadableDatabase().
        try {
            db = myDB.getWritableDatabase();
        }
        catch (SQLiteException ex){
            db = myDB.getReadableDatabase();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item1);
       //сожаем их на listView
        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //выборка-получаем все слова в ListView
    public Cursor getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        //sql запрос пишем чтоб получить все слова
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }

    //метод для избранных слов
    public Cursor getListOfFavorites()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //sql запрос пишем чтоб всех их получить favorites/избранные
        Cursor data1 = db.rawQuery("SELECT COL3 " + TABLE_NAME, null);
        return data1;
    }

}


