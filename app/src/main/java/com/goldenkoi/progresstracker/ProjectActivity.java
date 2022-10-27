package com.goldenkoi.progresstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import im.dacer.androidcharts.LineView;

import static com.goldenkoi.progresstracker.MainActivity.database;
import static com.goldenkoi.progresstracker.MainActivity.projectsDB;
import static java.lang.Integer.parseInt;

public class ProjectActivity extends AppCompatActivity {
    final Context context = this;

    private String nameProject;

    private final int WEEK = 7;
    private final int TWO_WEEKS = 14;
    private final int MONTH = 31;
    private final int YEAR = 365;
    private final int ALL_TIME = 1;

    private int currentRange = WEEK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Получаем из адаптера данные о том, какой проект выбрал пользователь
        Bundle arguments = getIntent().getExtras();
        nameProject = arguments.get("nameProject").toString();

        // Первоначальная настройка текстовых виджетов
        initialSettingOfLabels();

        spinnerChooseListener();

        btnNewDotListener();

        btnDeleteDotListener();

        // При первом запуске отображаем на графике 7 последних точек
        printGraph(WEEK);
    }

    private void initialSettingOfLabels() {
        // В виджет с названием проекта записываем название проекта в верхнем регистре
        TextView nameProjectText = findViewById(R.id.nameCurrentProject);
        nameProjectText.setText(nameProject.toUpperCase());

        // В виджет с названием замеряемой характеристики записываем полученное из базы данных значение
        String characteristicName = DataBasesOperations.getCurrentCharacteristic(nameProject);
        TextView characteristicText = findViewById(R.id.characteristicGraph);
        characteristicText.setText(characteristicName);
    }

    private void btnNewDotListener() {
        // Находим по id кнопку добавления нового прогресс-трекера
        Button newDot = findViewById(R.id.btnNewDot);

        newDot.setOnClickListener(view -> {
            // Получаем вид с файла new_project_dialog.xml, который применим для диалогового окна:
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.new_dot_dialog, null);

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context); // Создаем AlertDialog

            mDialogBuilder.setView(promptsView); // Настраиваем .xml файл для нашего AlertDialog

            // Настраиваем отображение поля для ввода текста в открытом диалоге:
            final EditText dotInput = promptsView.findViewById(R.id.dot_input);

            Button add_new_dot = promptsView.findViewById(R.id.new_dot_add_btn);

            // Создаем AlertDialog:
            AlertDialog alertDialog = mDialogBuilder.create();

            // Когда мы нажимаем "Добавить!", добавляем новое значение в базу данных
            add_new_dot.setOnClickListener( dialog -> {
                boolean isValidValue = true;
                int dotValue = 0;

                try {
                    dotValue = parseInt(String.valueOf(dotInput.getText()));
                } catch (NumberFormatException exception) {
                    isValidValue = false;
                }

                if (isValidValue) {
                    // Добавить новое значение в базу данных
                    DataBasesOperations.addNewDotToDB(dotValue, nameProject);
                    printGraph(currentRange);
                    alertDialog.cancel();
                }
                else {
                    // Выводим сообщение об ошибке, если пользователь ошибся :)
                    Toast toast = Toast.makeText(this, "Ещё раз проверьте введённые данные, " +
                            "ими должно быть целое число!",Toast.LENGTH_LONG);
                    toast.show();
                }
            });

            // и отображаем его:
            alertDialog.show();
        });
    }

    private void btnDeleteDotListener() {
        Button deleteDot = findViewById(R.id.deleteDot);

        deleteDot.setOnClickListener(view -> {
            // Получаем вид с файла new_project_dialog.xml, который применим для диалогового окна:
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.delete_dot_dialog, null);

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context); // Создаем AlertDialog

            mDialogBuilder.setView(promptsView); // Настраиваем .xml файл для нашего AlertDialog

            // Настраиваем сообщение в диалоговом окне:
            mDialogBuilder
                    .setPositiveButton("Да",
                            (dialog, id) -> {
                                // Когда пользователь нажимает на кнопку удаления, необходимо обратиться к
                                // базе данных с запросом на удаление и заново отрисовываем график
                                DataBasesOperations.deleteLastDot(nameProject);
                                printGraph(currentRange);
                            })
                    .setNegativeButton("Отмена",
                            (dialog, id) -> dialog.cancel());
            // Создаем AlertDialog:
            AlertDialog alertDialog = mDialogBuilder.create();
            // и отображаем его:
            alertDialog.show();
        });
    }

    // Слушатель выбора формата отображения (количества точек на графике)
    private void spinnerChooseListener() {
        Spinner spinner = findViewById(R.id.spinner);

        // При нажатии реализуем методы абстрактного класса AdapterView.OnItemSelectedListener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // При выборе нового элемента заново отрисовываем график с новым диапозоном (количеством точек)
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemSelected,
                                       int selectedItemPosition, long selectedId) {
                // Если заглянуть в содержимое спиннера, то можно понять, что 0 - 7 записей, 1 - 14;
                // 2 - 31, 3 - 365 и 4 - все точки из базы данных
                switch(selectedItemPosition) {
                    case 0:
                        currentRange = WEEK;
                        printGraph(currentRange);
                        break;
                    case 1:
                        currentRange =  TWO_WEEKS;
                        printGraph(currentRange);
                        break;
                    case 2:
                        currentRange = MONTH;
                        printGraph(currentRange);
                        break;
                    case 3:
                        currentRange = YEAR;
                        printGraph(currentRange);
                        break;
                    case 4:
                        currentRange = ALL_TIME;
                        printGraph(currentRange);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void scrollGraphDown() {
        HorizontalScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_RIGHT));
    }

    private void printGraph(int range) {
        // Получаем массивы с точками и датами
        String[] arrays = DataBasesOperations.getDotsAndDatesStringArray(nameProject);
        int size = getArraySize(arrays[0]); // Получаем количество записей в массиве

        if (size > 0) {
            if (range == ALL_TIME) {
                range = size;
            }

            // Массив с датами
            ArrayList<String> dates = getDatesArray(arrays[1], range);

            // Массив со значениями для каждой даты (возможно построение нескольких линий, поэтому
            // это массив из массивов)
            ArrayList<ArrayList<Integer>> dataLists = getDotsArray(arrays[0], range);

            // Ищем в проекте виджет с графиком
            LineView lineView = findViewById(R.id.line_view);
            lineView.setDrawDotLine(true); //optional
            lineView.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
            lineView.setBottomTextList(dates); // Устанавливаем координаты по Ox
            // Устанавливаем цвета
            lineView.setColorArray(new int[]{Color.BLACK, Color.GREEN, Color.GRAY, Color.CYAN});
            lineView.setDataList(dataLists); //or lineView.setFloatDataList(floatDataLists)}

            scrollGraphDown();
        }
    }

    private ArrayList<ArrayList<Integer>> getDotsArray(String array, int range) {
        ArrayList<ArrayList<Integer>> dotsArray = new ArrayList<>();
        StringBuilder dot = new StringBuilder();
        // Промежуточный массив для данных одной из линий на графике
        ArrayList<Integer> dataList = new ArrayList<>();
        // Счётчик добавленных в массив чисел
        int counter = 0;

        // Пока мы не дойдём до начала массива или не добавим указанное пользователем количество точек
        for (int i = array.length() - 1; i >= 0 && counter < range; i--) {
            // Будем добавлять эти точки
            if (array.charAt(i) != ' ') {
                dot.append(array.charAt(i));
            }
            else {
                // Так как мы шли задом наперёд (чтобы добавлять в график точки, которые бали добавлены
                // последними, а не первыми), то на каждой итерации добавляем в начало списка новое
                // перевёрнутое значение точки и обновляем буферные переменные и счётчики
                dataList.add(0, parseInt(String.valueOf(dot.reverse())));
                dot.delete(0, dot.length());
                counter++;
            }
        }
        dotsArray.add(dataList);

        return dotsArray;
    }

    private ArrayList<String> getDatesArray(String array, int range) {
        ArrayList<String> datesArray = new ArrayList<>();
        StringBuilder date = new StringBuilder();
        int counter = 0, dotsCounter = 0;

        // Здесь всё по аналогии

        for (int i = array.length() - 1; i >= 0 && counter < range; i--) {
            if (array.charAt(i) != ' ') {
                if (array.charAt(i) == '.') {
                    dotsCounter++;
                }
                if (dotsCounter >= 1 && !(dotsCounter == 1 && array.charAt(i) == '.')) {
                    date.append(array.charAt(i));
                }
            }
            else {
                datesArray.add(0, String.valueOf(date.reverse()));
                date.delete(0, date.length());
                counter++;
                dotsCounter = 0;
            }
        }

        return datesArray;
    }

    private int getArraySize(String array) {
        int size = 0;

        for (int i = 0; i < array.length(); i++) {
            if (array.charAt(i) == ' ') {
                size++;
            }
        }

        return size;
    }

    private ArrayList<ArrayList<Integer>> getMonthDotsArray() {
        ArrayList<ArrayList<Integer>> dotsArray = new ArrayList<>();
        return dotsArray;
    }

    private ArrayList<ArrayList<Integer>> getMonthDatesArray() {
        ArrayList<ArrayList<Integer>> dotsArray = new ArrayList<>();
        return dotsArray;
    }
}