package com.goldenkoi.progresstracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.goldenkoi.progresstracker.Adapter.ProjectsAdapter;
import com.goldenkoi.progresstracker.Model.ProjectsContent;
import com.goldenkoi.progresstracker.databases.ProjectsDB;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Объявляем объект нашей базы данных со всеми прогресс-трэкерами в глобальной области видимости
    public static ProjectsDB projectsDB;
    public static SQLiteDatabase database;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Определяем объект типа ProjectsDB, класса реализующего базу данных
        projectsDB = new ProjectsDB(this);
        // Объявляем базу данных SQLite и присваиваем ей созданную нами базу данных на SQLiteOpenHelper
        database = projectsDB.getWritableDatabase();

        // Слушатель кнопки для добавления нового трэкера
        btnNewProjectOnClickListener();

        // Отображаем recycler с созданными трэкерами
        recyclerUpdate();
    }

    private void btnNewProjectOnClickListener(){
        // Находим по id кнопку добавления нового прогресс-трекера
        Button newProject = findViewById(R.id.btnNewProject);

        newProject.setOnClickListener(view -> {
            // Получаем вид с файла new_project_dialog.xml, который применим для диалогового окна:
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.new_project_dialog, null);

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context); // Создаем AlertDialog

            mDialogBuilder.setView(promptsView); // Настраиваем .xml файл для нашего AlertDialog

            // Настраиваем отображение поля для ввода текста в открытом диалоге:
            final EditText nameInput = promptsView.findViewById(R.id.dot_input);
            final EditText characteristicInput = promptsView.findViewById(R.id.characteristic_input);

            Button add_new_project = promptsView.findViewById(R.id.new_dot_add_btn);

            // Создаем AlertDialog:
            AlertDialog alertDialog = mDialogBuilder.create();

            // Когда мы нажимаем "Добавить!", добавляем новое значение в базу данных
            add_new_project.setOnClickListener( dialog -> {
                                FormatsOperations formatsOperations = new FormatsOperations();

                                // Получаем данные из поля ввода и преобразовываем их по общим правилам
                                String nameProject = formatsOperations.remake_str(String.valueOf(nameInput.getText()));
                                String nameCharacteristic = formatsOperations.remake_str(String.valueOf(characteristicInput.getText()));

                                // Проверяем, чтобы данные соответсвовали формату
                                if (formatsOperations.checkName(nameProject, database) &&
                                        formatsOperations.checkCharacteristic(nameCharacteristic)) {

                                    // Получаем дату с устройства и форматируем её
                                    Date Date = new Date();
                                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
                                    String currentDate = dateFormat.format(Date);

                                    // Вставляем в БД новую строку и обновляем recycler (для отображения элементов), закрываем dialog
                                    projectsDB.putNewProject(database, nameProject, nameCharacteristic, currentDate);
                                    recyclerUpdate();
                                    alertDialog.cancel();
                                }

                                else {
                                    // Выводим сообщение об ошибке, если пользователь ошибся :)
                                    Toast toast = Toast.makeText(this, "Неверно заполнены поля :( Проверьте, чтобы " +
                                            "значения содержали буквы или цифры и не были слишком длинными," +
                                            " а имя трэкера ещё не использовалось!",Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            });

            // и отображаем его:
            alertDialog.show();
        });
    }

    private void recyclerUpdate() {
        // Получаем ArrayList<String> со всеми данными
        ArrayList<ProjectsContent> projectsContentList = DataBasesOperations.getProjectsList();

        // Отображаем объекты
        setLectureRecycler(projectsContentList);
    }

    private void setLectureRecycler(List<ProjectsContent> projectsContentList) {
        LinearLayout empty_recycler = findViewById(R.id.empty_recycler);

        // Если вдруг наш массив пустой, то очевидно, отображать в recycler нечего и нужно отобразить
        // сообщение, которое по умолчанию Invisible, если список не пуст, скрываем метки
        if (projectsContentList.isEmpty()) {
            // Показываем метки пустого Recycler
            empty_recycler.setVisibility(View.VISIBLE);
            }
        else {
            // Скрываем метки пустого Recycler
            empty_recycler.setVisibility(View.INVISIBLE);
        }

        // Настраиваем Recycle
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        // Находим по id recycler в главном меню
        RecyclerView recyclerProjectsMenu = findViewById(R.id.recyclerProjectsMenu);
        recyclerProjectsMenu.setLayoutManager(layoutManager);

        // Настраиваем адаптер
        ProjectsAdapter projectsAdapter = new ProjectsAdapter(this, projectsContentList);
        recyclerProjectsMenu.setAdapter(projectsAdapter);
    }
}