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
     * Сохраняет StateMap в файл
     */
    public void save(StateMap stateMap) {
        Properties props = new Properties();
        props.putAll(transformation(stateMap));
        try (FileOutputStream out = new FileOutputStream(path)) {
            props.store(out, "Window states");
        } catch (IOException e) {
            System.err.println("Error saving state: " + e.getMessage());
        }
    }

    /**
     * Загружает StateMap из файла
     */
    public StateMap load() {
        StateMap stateMap = new StateMap();
        File file = new File(path);
        if (!file.exists()) return stateMap;

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("Error loading state: " + e.getMessage());
            return stateMap;
        }
        return transformationPr(props);
    }

    /**
     * Преобразует StateMap в плоский словарь
     */
    private Map<String, String> transformation(StateMap stateMap) {
        Map<String, String> dictionary = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> component : stateMap.getAllData().entrySet()) {
            String prefix = component.getKey();
            for (Map.Entry<String, String> entry : component.getValue().entrySet()) {
                dictionary.put(prefix + "." + entry.getKey(), entry.getValue());
            }
        }
        return dictionary;
    }

    /**
     * Преобразует Properties в StateMap
     */
    private StateMap transformationPr(Properties props) {
        StateMap stateMap = new StateMap();
        for (String key : props.stringPropertyNames()) {
            int dotIndex = key.indexOf('.');
            if (dotIndex > 0) {
                String prefix = key.substring(0, dotIndex);
                String actualKey = key.substring(dotIndex + 1);
                String value = props.getProperty(key);

                Map<String, String> componentState = stateMap.get(prefix);
                componentState.put(actualKey, value);
                stateMap.put(prefix, componentState);
            }
        }
        return stateMap;
    }
}