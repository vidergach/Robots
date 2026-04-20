package gui;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Локализация приложения ру/англ
 * Реализована как синглтон
 */
public class LocaleManager {
    private static final String BUNDLE_NAME = "messages";

    private static volatile LocaleManager instance;
    private Locale currentLocale;
    private ResourceBundle bundle;

    /**
     * Конструктор
     */
    private LocaleManager() {
        currentLocale = new Locale("ru");
        bundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
    }

    /**
     * Получить единственный экземпляр LocaleManager
     */
    public static LocaleManager getInstance() {
        if (instance == null) {
            synchronized (LocaleManager.class) {
                if (instance == null) {
                    instance = new LocaleManager();
                }
            }
        }
        return instance;
    }

    /**
     * Устанавливает локаль из строки ("ru", "en")
     */
    public void setLocaleFromString(String language) {
        this.currentLocale = new Locale(language);
        this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
    }

    /**
     * Получить строку по ключу
     */
    public String getString(String key) {
            return bundle.getString(key);
    }

    /**
     * Возвращает текущую локаль в виде строки ("ru" или "en")
     */
    public String getLocaleString() {
        return currentLocale.getLanguage();
    }
}