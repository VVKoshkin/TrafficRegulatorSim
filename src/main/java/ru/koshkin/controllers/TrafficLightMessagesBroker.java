package ru.koshkin.controllers;

import ru.koshkin.components.TrafficLight;
import ru.koshkin.entities.Message;

import javax.swing.*;
import java.util.Date;

public class TrafficLightMessagesBroker {

    private static TrafficLight[] allLights;

    private static DefaultListModel log;

    public static void transmitMessageToTrafficLight(TrafficLight t, Message m) {
        t.getDeque().add(m);
    }

    public static void initialize(TrafficLight[] allLights, DefaultListModel log) {
        TrafficLightMessagesBroker.log = log;
        TrafficLightMessagesBroker.allLights = allLights;
    }

    public static int putNewMessage(Message m) {
        try {

            log.addElement(new Date() + " " + m.toString());
            System.out.println(new Date() + " " + m);
            String toId = m.getToId();
            transmitMessageToTrafficLight(getTrafficLightById(toId), m);
            return 1;
        } catch (Throwable t) {
            t.printStackTrace();
            return 0;
        }
    }

    private static TrafficLight getTrafficLightById(String id) throws Exception {
        for (TrafficLight t : allLights)
            if (t.getId().equals(id))
                return t;
        throw new Exception("Not found traffic light by id = " + id);
    }

}
