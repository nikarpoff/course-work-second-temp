package com.goldenkoi.progresstracker.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Теперь параметр Context может быть null
import androidx.annotation.Nullable;

import java.sql.ResultSet;
import java.util.Arrays;

// База данных наследуется от класса SQLiteOpenHelper
public class ProjectsDB extends SQLiteOpenHelper {
    // Строковые константы для дальнейшей работы

    public static final int DATABASE_VERSION = 1;                     // Версия Базы Данных
    public static final String DATABASE_NAME = "progress_tracker_db"; // Имя Базы Данных


    public static final String TABLE_PROJECTS = "projects";          // Таблица с трэкерами:

    public static final String KEY_NAME = "name";                    // Имя элемента (Проекта, Трэкера)
    public static final String KEY_CHARACTERISTIC = "characteristic";// Измеряемая величина
    public static final String KEY_LAST_CHANGE = "last_change";      // Дата последнего изменения
    public static final String KEY_DOTS = "dots";                    // Массив с точками для графика
    public static final String KEY_DATES = "dates";                  // Массив с датами добавления точек


    // Конструктор Базы данных с помощью Context - интерфейса предоставляющего глобальную
    // информацию о среде приложения и доступ к функциям Android. Context может быть null
    public ProjectsDB(@Nullable Context context) {
        // Передаём родительскому классу требуемые данные
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Переопределяем метод onCreate()
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Обращаемся к базе данных SQLite с запросом на создание таблицы с данными
        db.execSQL("create table " + TABLE_PROJECTS + " (" + KEY_NAME + " text not null unique, " +
                KEY_CHARACTERISTIC + " text not null, " + KEY_LAST_CHANGE + " text not null, " +
                KEY_DOTS + " text, " + KEY_DATES + " text" + ");");

        // Так выглядит запрос sql на самом деле:
        //CREATE TABLE "projects" (
        //        "name"	        TEXT NOT NULL UNIQUE,
        //        "characteristic"  TEXT NOT NULL,
        //        "last_change"	    TEXT NOT NULL,
        //        "dots"            TEXT,
        //        "dates"	        TEXT);
    }

    // Метод для изменения версии базы данных
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_PROJECTS);
        onCreate(db);
    }

    public void putNewProject(SQLiteDatabase db, String newProjectName,
                              String newProjectCharacteristic, String currentDate) {
        // Добавляем по запросу sql данные в таблицу
        db.execSQL("insert into " + TABLE_PROJECTS + " values" + " ('" + newProjectName + "', '" +
                newProjectCharacteristic + "', '" + currentDate + "', '', '')");

        // Так выглядит запрос на самом деле:
        // INSERT INTO "projects"
        // VALUES ('String ...', 'String ...', 'String ...', '', '');
    }

    public void deleteProject(SQLiteDatabase db, String projectName) {
        db.execSQL("delete from " + TABLE_PROJECTS + " where " + KEY_NAME + " = '" + projectName + "'");

        //DELETE FROM projects WHERE name = 'projectName';
    }

    public void updateDots(SQLiteDatabase db, String dotsArray, String datesArray, String projectName) {
        db.execSQL("update " + TABLE_PROJECTS +
                " set " + KEY_DOTS + " = '" + dotsArray + "', " +
                KEY_DATES + " = '" + datesArray +
                "' where " + KEY_NAME + " = '" + projectName + "'");


        //UPDATE projects SET dots = 'dotsArray', dates = 'datesArray' WHERE projectName = 'projectName';
    }

}
