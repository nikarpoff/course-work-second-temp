package com.goldenkoi.progresstracker.Model;

// Этот класс хранит контент для каждого из объектов RecyclerView
public class ProjectsContent {
    // id - номер в RecyclerView
    int id;
    // name - имя трэкера, characteristic - величина измерения, lastChange - дата последнего изменения
    String name, characteristic, lastChange;

    // Конструктор
    public ProjectsContent(int id, String name, String characteristic, String lastChange) {
        this.id = id;
        this.name = name;
        this.characteristic = characteristic;
        this.lastChange = lastChange;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(String characteristic) {
        this.characteristic = characteristic;
    }

    public String getLastChange() {
        return lastChange;
    }

    public void setLastChange(String lastChange) {
        this.lastChange = lastChange;
    }
}
