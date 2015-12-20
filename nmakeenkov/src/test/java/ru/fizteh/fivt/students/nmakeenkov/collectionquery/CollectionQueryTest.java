package ru.fizteh.fivt.students.nmakeenkov.collectionquery;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.fizteh.fivt.students.nmakeenkov.collectionquery.impl.Tuple;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.fizteh.fivt.students.nmakeenkov.collectionquery.Aggregates.avg;
import static ru.fizteh.fivt.students.nmakeenkov.collectionquery.Aggregates.count;
import static ru.fizteh.fivt.students.nmakeenkov.collectionquery.CollectionQuery.Student.student;
import static ru.fizteh.fivt.students.nmakeenkov.collectionquery.Conditions.rlike;
import static ru.fizteh.fivt.students.nmakeenkov.collectionquery.OrderByConditions.asc;
import static ru.fizteh.fivt.students.nmakeenkov.collectionquery.OrderByConditions.desc;
import static ru.fizteh.fivt.students.nmakeenkov.collectionquery.Sources.list;
import static ru.fizteh.fivt.students.nmakeenkov.collectionquery.impl.FromStmt.from;

import ru.fizteh.fivt.students.nmakeenkov.collectionquery.CollectionQuery.Statistics;
import ru.fizteh.fivt.students.nmakeenkov.collectionquery.CollectionQuery.Student;
import ru.fizteh.fivt.students.nmakeenkov.collectionquery.CollectionQuery.Group;

@RunWith(JUnit4.class)
public class CollectionQueryTest {
    @Test
    public void testMain1() throws Exception {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("2006-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(rlike(Student::getName, ".*ov").and(s -> s.age() > 20))
                        .groupBy(Student::getGroup)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Statistics::getGroup), desc(Statistics::getCount))
                        .limit(100)
                        .union()
                        .from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .selectDistinct(Statistics.class, s -> "all", count(s -> 1), avg(Student::age))
                        .execute();

        List<Statistics> correct = new ArrayList<>();
        Statistics[] my = { new Statistics("494", 1, 29.0),
                new Statistics("495", 1, 29.0),
                new Statistics("all", 1, 30.0) };
        for (Statistics i : my) {
            correct.add(i);
        }
        List<Statistics> answer = new ArrayList<>();
        statistics.forEach(t -> answer.add(t));

        Assert.assertArrayEquals(answer.toArray(), correct.toArray());
    }

    @Test
    public void testMain2() throws Exception {
        Iterable<Tuple<String, String>> mentorsByStudent =
                from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .join(list(new Group("494", "mr.sidorov")))
                        .on((s, g) -> Objects.equals(s.getGroup(), g.getGroup()))
                        .select(sg -> sg.getFirst().getName(), sg -> sg.getSecond().getMentor())
                        .execute();


        List<Tuple<String, String>> correct = new ArrayList<>();
        correct.add(new Tuple<>("ivanov", "mr.sidorov"));
        List<Tuple<String, String>> answer = new ArrayList<>();
        mentorsByStudent.forEach(t -> answer.add(t));

        Assert.assertArrayEquals(answer.toArray(), correct.toArray());
    }

    @Test
    public void testMy1() throws Exception {
        List<TupleStr> names =
                from(list(
                        student("A", LocalDate.parse("1985-08-06"), "494"),
                        student("C", LocalDate.parse("1985-08-06"), "495")))
                        .join(list(
                                student("B", LocalDate.parse("1985-08-06"), "494"),
                                student("D", LocalDate.parse("1985-08-06"), "495")))
                        .on(Student::getGroup, Student::getGroup)
                        .select(TupleStr.class, (sg) -> sg.getFirst().getName(),
                                (sg) -> sg.getSecond().getName())
                        .stream().collect(Collectors.toList());;

        Assert.assertEquals(names.get(0), new TupleStr("A", "B"));
        Assert.assertEquals(names.get(1), new TupleStr("C", "D"));
    }

    public static class TupleStr {
        private final String first;
        private final String second;

        public TupleStr(String first, String second) {
            this.first = first;
            this.second = second;
        }

        public String getFirst() {
            return first;
        }

        public String getSecond() {
            return second;
        }

        @Override
        public String toString() {
            return "TupleStr{"
                    + "first=" + first
                    + ", second=" + second
                    + '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TupleStr)) {
                return false;
            }
            TupleStr oth = (TupleStr) obj;
            return this.first.equals(oth.first)
                    && this.second.equals(oth.second);
        }


        @Override
        public int hashCode() {
            return first.hashCode() * 227 + second.hashCode();
        }
    }

}