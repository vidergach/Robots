package gui;

import java.util.HashMap;
import java.util.Map;

/**
 * класс для хранения состояния окон
 * <префикс, <ключ, значение>>
 */
public class StateMap {
    private Map<String, Map<String, String >> data;

    /**
     * * конструктор по умолчанию
     */
    public StateMap(){
        this.data = new HashMap<>();
    }

    /**
     * сохраняет состояние компонента в словарь
     */
    public void put(String prefix, Map<String, String> state){
        data.put(prefix, state);
    }

    /**
     * возвращает состояние компонента по его префиксу
     */
    public Map<String, String> get(String prefix) {
        return data.getOrDefault(prefix, new HashMap<>());
    }

    /**
     * проверяем, существует ли состояние для компонента с указанным префиксом
     */
    public boolean containsPrefix(String prefix) {
        return data.containsKey(prefix);
    }

    /**
     * возвращаем все данные
     */
    Map<String, Map<String, String>> getAllData() {
        return data;
    }
}