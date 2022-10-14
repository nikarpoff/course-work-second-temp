package com.goldenkoi.progresstracker;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.goldenkoi.progresstracker.Model.ProjectsContent;
import com.goldenkoi.progresstracker.databases.ProjectsDB;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.goldenkoi.progresstracker.MainActivity.database;
import static com.goldenkoi.progresstracker.MainActivity.projectsDB;

// Класс с открытыми операциями с базами данных
public class DataBasesOperations {
    // Получает массив с данными из базы с проектами для дальнейшего отображения на главном экране
    public static ArrayList<ProjectsContent> getProjectsList() {
        // В этой строке храним все данные из базы
        ArrayList <ProjectsContent> data = new ArrayList<>();

        // Курсор для обращения к базе данных ProjectsDB
        @SuppressLint("Recycle") Cursor cursor = database.query(ProjectsDB.TABLE_PROJECTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            // Получаем индексы колонок и запоминаем их
            int nameIndex = cursor.getColumnIndex(ProjectsDB.KEY_NAME);
            int characteristicIndex = cursor.getColumnIndex(ProjectsDB.KEY_CHARACTERISTIC);
            int lastChangeIndex = cursor.getColumnIndex(ProjectsDB.KEY_LAST_CHANGE);
            int index = 0;

            // Пока мы можем передвигать курсор, мы добавляем в массив значения из базы данных
            // из соответствующих колонок
            do {
                data.add(new ProjectsContent(index, cursor.getString(nameIndex), cursor.getString(characteristicIndex), cursor.getString(lastChangeIndex)));
                index++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (data.size() > 1) sortByDateModified(data);

        return data;
    }

    public static String getCurrentCharacteristic(String nameProject) {
        String characteristicName = "Empty";

        // Курсор для обращения к базе данных ProjectsDB
        @SuppressLint("Recycle") Cursor cursor = database.query(ProjectsDB.TABLE_PROJECTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            // Получаем индексы колонок и запоминаем их
            int nameIndex = cursor.getColumnIndex(ProjectsDB.KEY_NAME);
            int characteristicIndex = cursor.getColumnIndex(ProjectsDB.KEY_CHARACTERISTIC);

            // Пока мы не встретим нужную строку, мы будем двигать курсор вперёд
            do {
                if (cursor.getString(nameIndex).equals(nameProject)) {
                    characteristicName = cursor.getString(characteristicIndex);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return characteristicName;
    }

    public static String[] getDotsAndDatesStringArray(String nameProject) {
        // arrays[0] хранит значения точек, arrays[1] хранит значения дат
        String[] arrays = new String[2];

        // Создаём курсор для поиска в базе данных
        @SuppressLint("Recycle") Cursor cursor = database.query(ProjectsDB.TABLE_PROJECTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            // Получаем индексы колонок и запоминаем их
            int nameIndex = cursor.getColumnIndex(ProjectsDB.KEY_NAME);
            int dotIndex = cursor.getColumnIndex(ProjectsDB.KEY_DOTS);
            int dateIndex = cursor.getColumnIndex(ProjectsDB.KEY_DATES);

            // Пока мы не встретим нужную строку, мы будем двигать курсор вперёд
            do {
                if (cursor.getString(nameIndex).equals(nameProject)) {
                    arrays[0] = cursor.getString(dotIndex);
                    arrays[1] = cursor.getString(dateIndex);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return arrays;
    }

    public static void addNewDotToDB(int newDotValue, String nameProject) {
        // Получаем текущие массивы дат и точек из базы данных
        String[] array = getDotsAndDatesStringArray(nameProject);
        // Получаем дату с устройства и форматируем её
        Date Date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        String currentDate = dateFormat.format(Date);

        // Записываем в строчки соответствующие массивы точек и дат
        String dotsValues = array[0];
        String datesValues = array[1];

        // Добавляем к текущим массивам новые значения
        if (dotsValues != null) {
            dotsValues = dotsValues + " " + newDotValue;
            datesValues = datesValues + " " + currentDate;
        }
        else {
            dotsValues = " " + newDotValue;
            datesValues = " " + currentDate;
        }

        // Обращаемся к базе данных с запросом sql на изменение данных в таблице
        projectsDB.updateDots(database, dotsValues, datesValues, nameProject);
    }

    private static void sortByDateModified(ArrayList<ProjectsContent> data) {
        try {
            quickSortDates(data, 0, data.size() - 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void quickSortDates(ArrayList<ProjectsContent> data, int left, int right) throws ParseException {
        int i, j;
        ProjectsContent x, y;
        Date x_date;

        i = left;
        j = right;

        x = data.get((right - left) / 2);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        x_date = dateFormat.parse(x.getLastChange());


        do {
            while ((dateFormat.parse(data.get(i).getLastChange()).before(x_date)) && (i < right))
                i++;
            while ((x_date.before(dateFormat.parse(data.get(i).getLastChange()))) && (left < j))
                j--;

            if (i <= j) {
                y = data.get(i);
                data.add(i, data.get(j));
                data.remove(i+1);
                data.add(j, y);
                data.remove(j+1);
                i++;
                j--;
            }

        } while (i <= j);

        if (left < j) quickSortDates(data, left, j);
        if (i < right) quickSortDates(data, i, right);
    }
}
