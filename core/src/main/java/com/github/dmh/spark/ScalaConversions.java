package com.github.dmh.spark;

public final class ScalaConversions {
    private ScalaConversions() {}

    public static <T> java.util.List<T> fromSequence(scala.collection.Seq<T> seq) {
        java.util.ArrayList<T> list = new java.util.ArrayList<>();
        scala.collection.Iterator<T> it = seq.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    public static <T> java.util.List<T> fromImmutableSequence(
            scala.collection.immutable.Seq<T> seq) {
        java.util.ArrayList<T> list = new java.util.ArrayList<>();
        scala.collection.Iterator<T> it = seq.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }
}
