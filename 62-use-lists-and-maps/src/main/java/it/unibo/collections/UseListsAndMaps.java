package it.unibo.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * Example class using {@link List} and {@link Map}.
 *
 */
public final class UseListsAndMaps {

    private static final int ELEMS = 100_000;
    private static final int READS = 1_000;

    private UseListsAndMaps() {
    }

    /**
     * @param s
     *            unused
     */
    public static void main(final String... s) {

        final List<Integer> arrayList = new ArrayList<>();
        for(int i = 1000; i<2000; i++) {
            arrayList.add(i);
        }
        
        final List<Integer> linkedList = new LinkedList<>(arrayList);
        
        final int temp = arrayList.get(0);
        arrayList.set(0, arrayList.get(arrayList.size() - 1));
        arrayList.set(arrayList.size() -1, temp);

        for(final int elem : arrayList) {
            System.out.println(elem);
        }
        
        long time = System.nanoTime();
        for (int i = 0; i < ELEMS; i++) {
            arrayList.add(0, i);
        }
        time = System.nanoTime() - time;
        final var millis = TimeUnit.NANOSECONDS.toMillis(time);
        System.out.println(
            "ArrayList: inserting " + ELEMS + " elements at the head took  "
            + time + "ns (" + millis + "ms)"
        );

        time = System.nanoTime();
        for (int i = 0; i < ELEMS; i++) {
            linkedList.add(0, i);
        }
        time = System.nanoTime() - time;
        final var millisLinked = TimeUnit.NANOSECONDS.toMillis(time);
        System.out.println(
            "LinkedList: inserting " + ELEMS + " elements at the head took  "
            + time + "ns (" + millisLinked + "ms)"
        );
        
        final int middlePosition = arrayList.size() / 2;
        time = System.nanoTime();
        for(int i = 0; i < READS; i++) {
            arrayList.get(middlePosition);
        }
        time = System.nanoTime() - time;
        System.out.println("ArrayList: ... " + time + "ns");

        final Map<String, Long> worldPopulation = new HashMap<>();
        worldPopulation.put("Africa", 1_110_635_000L);
        worldPopulation.put("Americas", 972_005_000L);
        worldPopulation.put("Antartica", 0L);
        worldPopulation.put("Asia", 4_298_723_000L);
        worldPopulation.put("Europe", 742_452_000L);
        worldPopulation.put("Oceania",38_304_000L);

        long totalPopulation = 0;
        for(final long population : worldPopulation.values()){
            totalPopulation += population;
        }
        System.out.println("World population: " + totalPopulation);
    }
}
