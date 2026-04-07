package log;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Источник логов с ограниченным размером
 */
public class LogWindowSource {

    private final int maxSize;
    private final Deque<LogEntry> messages;
    private final WeakArrayList<LogChangeListener> listeners;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LogWindowSource(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }
        this.maxSize = maxSize;
        this.messages = new ArrayDeque<>(maxSize + 1);
        this.listeners = new WeakArrayList<>();
    }

    /**
     * Регистрация слушателя
     * */
    public void registerListener(LogChangeListener listener) {
        if (listener == null) return;
        synchronized (listeners) {
            // Избегаем дубликатов
            for (LogChangeListener existing : listeners.getLiveElements()) {
                if (existing == listener) return;
            }
            listeners.add(listener);
        }
    }

    /**
     * Добавление записи в лог
     * */
    public void append(LogLevel level, String message) {
        LogEntry entry = new LogEntry(level, message);

        lock.writeLock().lock();
        try {
            // Вытесняем старую запись, если достигнут лимит
            if (messages.size() >= maxSize) {
                messages.pollFirst();
            }
            messages.offerLast(entry);
        } finally {
            lock.writeLock().unlock();
        }

        notifyListeners();
    }

    /**
     * Уведомление только живых слушателей
     * */
    private void notifyListeners() {
        List<LogChangeListener> active;
        synchronized (listeners) {
            active = listeners.getLiveElements();
        }
        for (LogChangeListener listener : active) {
            try {
                listener.onLogChanged();
            } catch (Exception e) {
                // Не даём ошибке в одном слушателе сломать уведомление остальных
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }

    /**
     * Возвращает диапазон записей [startFrom, startFrom+count).
     */
    public Iterable<LogEntry> range(int startFrom, int count) {
        lock.readLock().lock();
        try {
            if (startFrom < 0 || startFrom >= messages.size() || count <= 0) {
                return Collections.emptyList();
            }
            int end = Math.min(startFrom + count, messages.size());
            List<LogEntry> snapshot = new ArrayList<>(end - startFrom);

            Iterator<LogEntry> it = messages.iterator();
            for (int i = 0; i < end; i++) {
                LogEntry e = it.next();
                if (i >= startFrom) {
                    snapshot.add(e);
                }
            }
            return Collections.unmodifiableList(snapshot);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Все записи как snapshot
     **/
    public Iterable<LogEntry> all() {
        return range(0, Integer.MAX_VALUE);
    }

}