package com.sdai.smartfarm.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class VectorUtils {

    private VectorUtils() {}

    public static <T> boolean areEquivalentSets(T[] vector1, T[] vector2) {

        Set<T> vectorSet = new HashSet<>();

        Collections.addAll(vectorSet, vector1);

        for(T secondArrayElement: vector2) {
            if (!vectorSet.contains(secondArrayElement)) return false;
            vectorSet.remove(secondArrayElement);
        }

        return vectorSet.isEmpty();
    }

        public static double[] multiply(double x, double[] vector) {

        double[] result = new double[vector.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = x * vector[i];
        }

        return result;
    }

    public static double[] sum(double[] vector1, double[] vector2) {

        double[] result = new double[vector1.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = vector1[i] + vector2[i];
        }

        return result;
    }

    // overflowing values are clamped and collected at marginal bins
    public static double[] convolve(double[] vector1, double[] vector2, int offset) {
        
        double[] result = new double[vector1.length];
        Arrays.fill(result, 0.0);

        for(int i = 0; i < vector1.length; i++) {
            for(int j = 0; j < vector2.length; j++) {
                int k = i + j + offset;
                if (k < 0) k = 0;
                if (k >= vector1.length) k = vector1.length - 1;
                result[k] += vector1[i] * vector2[j];
            }
        }

        return result;
    }
    

    // I hate this function, but there is no class in standard Java that implements both List and Deque, 
    // and uses an Array/Contiguous chunk of memory as underlying data structure (a.k.a no bidirectional vector)
    public static void sumInPlace(Deque<Double> queue1, Deque<Double> queue2) {

        if (queue1.size() != queue2.size()) throw new IllegalArgumentException("the 2 queues must be the same lenght!");

        Iterator<Double> iterator = queue2.iterator();

        while(iterator.hasNext()) {

            Double x1 = queue1.poll();
            Double x2 = iterator.next();

            if (x1 == null) x1 = 0.0;
            if (x2 == null) x2 = 0.0;
            
            queue1.addLast(x1 + x2);
        }
        
    }

    // I hate this one as well
    public static void sumAndSubtractInPlace(Deque<Double> queue1, Deque<Double> queue2, Deque<Double> queue3) {

        if (queue1.size() != queue2.size() || queue1.size() != queue3.size()) throw new IllegalArgumentException("the 3 queues must be the same lenght!");

        Iterator<Double> iterator2 = queue2.iterator();
        Iterator<Double> iterator3 = queue3.iterator();

        while(iterator2.hasNext() && iterator3.hasNext()) {

            Double x1 = queue1.poll();
            Double x2 = iterator2.next();
            Double x3 = iterator3.next();

            if (x1 == null) x1 = 0.0;
            if (x2 == null) x2 = 0.0;
            if (x3 == null) x3 = 0.0;

            queue1.addLast(x1 + x2 - x3);
            
            
        }
    }
    
}
