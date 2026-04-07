package log;

import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Список со слабыми ссылками для слушателей событий.
 * Объекты автоматически удаляются при сборке мусора, предотвращая утечки памяти.
 * @param <T> тип хранимых элементов (интерфейсы-слушатели)
 */
public class WeakArrayList<T> extends AbstractList<T> {

    private final List<WeakReference<T>> items;

    public WeakArrayList() {
        items = new ArrayList<>();
    }

    @Override
    public T get(int index) {
        WeakReference<T> ref = items.get(index);
        return ref != null ? ref.get() : null;
    }

    @Override
    public int size() {
        cleanDeadReferences();
        return items.size();
    }

    @Override
    public boolean add(T element) {
        if (element == null) {
            throw new NullPointerException("Cannot store null in WeakArrayList");
        }
        if (items.size() > 100) cleanDeadReferences();
        return items.add(new WeakReference<>(element));
    }

    @Override
    public boolean remove(Object element) {
        cleanDeadReferences();
        return items.removeIf(ref -> {
            T item = ref.get();
            return item == null || item.equals(element);
        });
    }

    @Override
    public T remove(int index) {
        WeakReference<T> ref = items.remove(index);
        return ref != null ? ref.get() : null;
    }

    @Override
    public void clear() {
        items.clear();
    }

    /**
     * Удаляет все ссылки на объекты, уже собранные сборщиком мусора
     */
    public void cleanDeadReferences() {
        items.removeIf(ref -> ref.get() == null);
    }

    /**
     * Возвращает список живых элементов — удобно для итерации
     */
    public List<T> getLiveElements() {
        cleanDeadReferences();
        List<T> live = new ArrayList<>(items.size());
        for (WeakReference<T> ref : items) {
            T item = ref.get();
            if (item != null) {
                live.add(item);
            }
        }
        return live;
    }
}