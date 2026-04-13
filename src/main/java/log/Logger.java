package log;

public final class Logger
{

    private static final LogWindowSource DEFAULT_LOG_SOURCE =
            new LogWindowSource(5);//ограничим до 5

    /**
     * Конструктор - предотвращает создание копий
     */
    private Logger()
    {
    }

    /**
     *   Добавляет отладочное сообщение в системный лог
     */
    public static void debug(String strMessage)
    {
        DEFAULT_LOG_SOURCE.append(LogLevel.Debug, strMessage);
    }

    /**
     * Добавляет сообщение об ошибке в системный лог
     */
    public static void error(String strMessage)
    {
        DEFAULT_LOG_SOURCE.append(LogLevel.Error, strMessage);
    }

    /**
     *  Возвращает глобальный источник логов для прямого доступ
     */
    public static LogWindowSource getDefaultLogSource()
    {
        return DEFAULT_LOG_SOURCE;
    }

}
