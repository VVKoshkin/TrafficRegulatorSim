package ru.koshkin.helpers;

import ru.koshkin.components.TrafficLight;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class TrafficLightsConcurrents {
    public static final HashMap<String, List<String>> concurrentsList = new HashMap<>();
    public static final HashMap<String, String> siblings = new HashMap<>();

    public static void initialize() {
        concurrentsList.clear();
        concurrentsList.put("n1", Arrays.asList("N", "S", "W"));
        concurrentsList.put("n2", Arrays.asList("N", "S", "W"));
        concurrentsList.put("e1", Arrays.asList("N", "E", "W"));
        concurrentsList.put("e2", Arrays.asList("N", "E", "W"));
        concurrentsList.put("w1", Arrays.asList("S", "E", "W"));
        concurrentsList.put("w2", Arrays.asList("S", "E", "W"));
        concurrentsList.put("s1", Arrays.asList("N", "E", "S"));
        concurrentsList.put("s2", Arrays.asList("N", "E", "S"));
        concurrentsList.put("N", Arrays.asList("E", "W", "s1", "s2", "n1", "n2", "e1", "e2"));
        concurrentsList.put("E", Arrays.asList("N", "S", "w1", "w2", "e1", "e2", "s1", "s2"));
        concurrentsList.put("W", Arrays.asList("N", "S", "n1", "n2", "w1", "w2", "e1", "e2"));
        concurrentsList.put("S", Arrays.asList("W", "E", "n1", "n2", "s1", "s2", "w1", "w2"));
        siblings.put("n1", "n2");
        siblings.put("n2", "n1");
        siblings.put("w1", "w2");
        siblings.put("w2", "w1");
        siblings.put("e1", "e2");
        siblings.put("e2", "e1");
        siblings.put("s1", "s2");
        siblings.put("s2", "s1");
    }

    public static List<String> getConcurrentLightsForTrafficLight(TrafficLight light) {
        String id = light.getId();
        return concurrentsList.get(id);
    }

    public static String getMySibling(TrafficLight trafficLight) {
        return siblings.get(trafficLight.getId());
    }
}
