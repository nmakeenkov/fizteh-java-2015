package ru.fizteh.fivt.students.nmakeenkov.collectionquery;

import java.util.List;
import java.util.function.Function;

public interface AggregationFunction<T, E> extends Function<T, E> {
    E apply(List<T> list);
}
