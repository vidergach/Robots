package gui;

import java.util.Map;

/**
 * Интерфейс для сохранения и восстановления состояния
 */
public interface StateSaveAndRestore {
    /**
     * Сохраняет состояние компоненты в словарь
     */
    Map<String, String> saveState();

    /**
     * Восстанавливает состояние компоненты из словаря
     */
    void restoreState(Map<String, String> state);

    /**
     * Возвращает префикс для ключей окна
     */
    String getPrefix();
}