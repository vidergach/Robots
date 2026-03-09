package gui;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Класс для сохранения и загрузки состояния окон в файл
 */
public class StateFileManager{
    private String path;

    /**
     * Создает объект для работы с файлом состояния
     */
    public StateFileManager(String surname) {
        this.path = System.getProperty("user.home") + "/" + surname + "/state.cfg";
        new File(path).getParentFile().mkdirs();
        //предоставляет фаил по указанному пути
        //получаем род директорию
        //создает необходимую директорию, если ее нет
    }

    /**
     * Сохраняет словарь с данными в файл
     * @param data словарь для сохранения
     */
    public void save(Map<String, String> data) {
        Properties props = new Properties();//ключ=значение
        props.putAll(data);//копируем данные
        try (FileOutputStream out = new FileOutputStream(path)) {//создали поток
            props.store(out, "Window states");//сохр все свойства в выходной поток
        } catch (IOException e) {
            System.err.println("Error saving state: " + e.getMessage());
        }
    }

    /**
     * Загружает данные из файла в словарь
     * @return загруженный словарь
     */
    public Map<String, String> load() {
        Map<String, String> data = new HashMap<>();
        File file = new File(path);
        if (!file.exists()) return data;//проверка сущ

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {//поток для чтения
            props.load(in);//читаем
        } catch (IOException e) {
            System.err.println("Error loading state: " + e.getMessage());
            return data;
        }

        for (String key : props.stringPropertyNames()) {//все ключи
            data.put(key, props.getProperty(key));//добавляем в итоговый словарь
        }
        return data;
    }
}