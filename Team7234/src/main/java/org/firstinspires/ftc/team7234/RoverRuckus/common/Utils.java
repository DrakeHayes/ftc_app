package org.firstinspires.ftc.team7234.RoverRuckus.common;

import java.util.*;

public final class Utils {

    public static <E> E mode(List<E> list){
        Map<E, Integer> hashMap = new HashMap<>();
        for (E o :
                list) {
            if (!hashMap.containsKey(o)){ //If element is not in Map, adds element to map with one entry
                hashMap.put(o, 1);
            }
            else {
                hashMap.replace(o, hashMap.get(o)+1 ); //Increments value of element by one
            }
        }

        return hashMap
                .entrySet()
                .stream()
                .max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1)
                .get().getKey();

    }
}
