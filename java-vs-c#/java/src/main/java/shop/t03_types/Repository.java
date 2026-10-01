// VERDICT | T03 Type system & generics | BETTER: C#
// WHY: reified generics (typeof(T), new T(), List<int> without boxing) and generic math; Java has erasure and Integer boxing traps.

package shop.t03_types;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Erasure again: inside the class {@code T} is unknown at runtime, so {@code new T()} and
 * {@code T.class} are impossible. The caller must hand over a Class token and a factory.
 */
public final class Repository<T> {

    private final Class<T> type;
    private final Supplier<T> factory;
    private final List<T> items = new ArrayList<>();

    public Repository(Class<T> type, Supplier<T> factory) {
        this.type = type;
        this.factory = factory;
    }

    public String entityName() {
        return type.getSimpleName();
    }

    public T createNew() {
        T item = factory.get();
        items.add(item);
        return item;
    }

    public int count() {
        return items.size();
    }
}
