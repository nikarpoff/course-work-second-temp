package com.goldenkoi.progresstracker;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.goldenkoi.progresstracker.databases.ProjectsDB;

public class FormatsOperations {
    public boolean checkName(String name_str, SQLiteDatabase database) {
        String test_str = name_str.replaceAll("[^0-9а-яa-z- А-ЯA-Z]", "");
        if (!test_str.equals(name_str)) {
            return false;
        }
        return (name_str.length() > 0 && name_str.length() < 100 && !isElementRepeating(name_str, database));
    }

    public boolean checkCharacteristic(String characteristic_str) {
        String test_str = characteristic_str.replaceAll("[^0-9а-яa-z- А-ЯA-Z]", "");
        if (!test_str.equals(characteristic_str)) {
            return false;
        }
        return (characteristic_str.length() > 0 && characteristic_str.length() < 50);
    }

    /* Этот метод выозвращает переделанную строку по единым правилам (удаляет пробелы, знаки
    табуляции, перевода каретки, знаки препинания и переводит текст в нижний регистр)
    Он оставляет только буквы русского и латинского алфавита и цифры
     */
    public String remake_str(String str) {
        str = str.toLowerCase();
        str = str.replaceAll("[^0-9а-яa-z-]", "");

        return str;
    }

    private boolean isElementRepeating(String name_str, SQLiteDatabase database) {
        boolean key = false;

        // Курсор для обращения к базе данных ProjectsDB
        @SuppressLint("Recycle") Cursor cursor = database.query(ProjectsDB.TABLE_PROJECTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            // Получаем индексы колонок и запоминаем их
            int nameIndex = cursor.getColumnIndex(ProjectsDB.KEY_NAME);

            // Пока мы можем передвигать курсор, мы добавляем в массив значения из базы данных
            // из соответствующих колонок
            do {
                if (name_str.equals(cursor.getString(nameIndex))) {
                    key = true;
                    break;
                }
            } while (cursor.moveToNext());
        }

        return key;
    }

    //public boolean checkDot(int dotValue) {
    //    boolean key = false;
    //
    //    if (dotValue )
    //
    //    return key;
    //}
}
