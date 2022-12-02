package code.datastructure;

import java.util.Objects;

public class Pair<T, B> implements Comparable<Pair<T, B>>, Cloneable {
    public T first;
    public B second;

    public Pair(T first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "_" + first.toString() + "_" + second.toString() + "_";
    }

    @Override
    public int compareTo(Pair<T, B> o) {
        Comparable<T> cmp1 = (Comparable<T>) this.first;
        if (cmp1.compareTo(o.first) != 0) return cmp1.compareTo(o.first);
        Comparable<B> cmp2 = (Comparable<B>) this.second;
        return cmp2.compareTo(o.second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public Pair<T, B> clone() {
        try {
            Pair<T, B> clone = (Pair<T, B>) super.clone();
            // hoping that Integer is not by ref
            // generics in java is really missed up
            clone.first = first;
            clone.second = second;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
