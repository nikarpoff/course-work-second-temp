package com.goldenkoi.progresstracker.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.goldenkoi.progresstracker.FormatsOperations;
import com.goldenkoi.progresstracker.MainActivity;
import com.goldenkoi.progresstracker.Model.ProjectsContent;
import com.goldenkoi.progresstracker.ProjectActivity;
import com.goldenkoi.progresstracker.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.goldenkoi.progresstracker.MainActivity.database;
import static com.goldenkoi.progresstracker.MainActivity.projectsDB;

// Адаптер, отвечающий за отображение и наполнение модели
public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectsViewHolder> {

    static Context context;
    static List<ProjectsContent> projectsContentList;

    // Стандартный конструктор
    public ProjectsAdapter(Context context, List<ProjectsContent> projectsContentList) {
        ProjectsAdapter.context = context;
        ProjectsAdapter.projectsContentList = projectsContentList;
    }

    // Стандартный метод onCreate для View Holder с созданием View на основе модели project_info
    @NonNull
    @Override
    public ProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View projectsItems = LayoutInflater.from(context).inflate(R.layout.project_info, parent, false);
        return new ProjectsViewHolder(projectsItems);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsAdapter.ProjectsViewHolder holder, int position) {
        // Изменяем в виджетах значения по умолчанию на те, что были извлечены из базы данных
        holder.name.setText(projectsContentList.get(position).getName());
        holder.lastChange.setText(projectsContentList.get(position).getLastChange());
    }

    @Override
    public int getItemCount() {
        return projectsContentList.size();
    }

    // Вложенный класс ProjectViewHolder
    public static final class ProjectsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Button name;
        TextView lastChange;
        ImageView deleteProject;
        public ProjectsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameProject);
            lastChange = itemView.findViewById(R.id.lastChangeInfo);
            deleteProject = itemView.findViewById(R.id.btnDelete);
            // Вызываем метод, обрабатывающий нажатия на элемент Recycler View
            onClick(itemView);
        }


        // Кликер на элемент RecyclerView (перевод Activity на ProjectActivity)
        @Override
        public void onClick(View view) {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();

            name.setOnClickListener(
                    view1 -> {
                        // Передаём в другую activity имя проекта (там будем по нему искать данные в БД)
                        Intent i = new Intent(activity, ProjectActivity.class);
                        i.putExtra("nameProject", name.getText());
                        activity.startActivity(i);
                    }
            );

            deleteProject.setOnClickListener(
                    view2 -> {
                        // Получаем вид с файла new_project_dialog.xml, который применим для диалогового окна:
                        LayoutInflater li = LayoutInflater.from(context);
                        View promptsView = li.inflate(R.layout.delete_dialog, null);

                        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context); // Создаем AlertDialog

                        mDialogBuilder.setView(promptsView); // Настраиваем .xml файл для нашего AlertDialog

                        // Настраиваем сообщение в диалоговом окне:
                        mDialogBuilder
                                .setPositiveButton("Да",
                                        (dialog, id) -> {
                                            // Когда пользователь нажимает на кнопку удаления, необходимо обратиться к
                                            // базе данных с запросом на удаление и запустить MainActivity для обновления страницы
                                            FormatsOperations formatsOperations = new FormatsOperations();
                                            projectsDB.deleteProject(database, formatsOperations.remake_str(String.valueOf(name.getText())));
                                            Intent i = new Intent(activity, MainActivity.class);
                                            activity.startActivity(i);
                                        })
                                .setNegativeButton("Отмена",
                                        (dialog, id) -> dialog.cancel());
                        // Создаем AlertDialog:
                        AlertDialog alertDialog = mDialogBuilder.create();
                        // и отображаем его:
                        alertDialog.show();
                    });
        }
    }
}
