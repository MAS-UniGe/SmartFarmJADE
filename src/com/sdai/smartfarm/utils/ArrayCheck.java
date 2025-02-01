package com.sdai.smartfarm.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ArrayCheck {

    private ArrayCheck() {}

    public static <T> boolean areEquivalent(T[] firstArray, T[] secondArray) {

        Set<T> firstArraySet = new HashSet<>();

        Collections.addAll(firstArraySet, firstArray);

        for(T secondArrayElement: secondArray) {
            if (!firstArraySet.contains(secondArrayElement)) return false;
            firstArraySet.remove(secondArrayElement);
        }

        return firstArraySet.isEmpty();
    }
    
    
}
