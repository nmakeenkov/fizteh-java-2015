package ru.fizteh.fivt.students.nmakeenkov.collectionquery.impl;

import java.util.stream.Stream;

/**
 * @author akormushin
 */
public interface Query<R> {

    Iterable<R> execute() throws ReflectiveOperationException;

    Stream<R> stream() throws ReflectiveOperationException;
}
