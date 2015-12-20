package ru.fizteh.fivt.students.nmakeenkov.collectionquery.impl;

/**
 * @author akormushin
 */
public class Tuple<F, S> {

    private final F first;
    private final S second;

    public Tuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Tuple{"
                + "first=" + first
                + ", second=" + second
                + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple)) {
            return false;
        }
        Tuple oth = (Tuple) obj;
        return this.first.equals(oth.first)
                && this.second.equals(oth.second);
    }


    @Override
    public int hashCode() {
        return first.hashCode() * 227 + second.hashCode();
    }
}
