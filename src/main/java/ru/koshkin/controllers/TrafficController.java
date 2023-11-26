package ru.koshkin.controllers;

import ru.koshkin.Constants;
import ru.koshkin.components.TrafficLight;

import java.util.ArrayList;
import java.util.Random;

public class TrafficController {

    private static ArrayList<Thread> threads = new ArrayList<>();

    public static void initialize(TrafficLight[] allLights) {
        for (int i = 0; i < allLights.length; i++) {
            int finalI = i;
            Thread t = new Thread() {
                @Override
                public void run() {
                    Random r = new Random();
                    while (!this.isInterrupted()) {
                        int toQueueAddSize = Constants.TsimElementArrivalMin + r.nextInt(Constants.TsimElementArrivalMax - Constants.TsimElementArrivalMin);
                        allLights[finalI].addToQueue(Long.valueOf(toQueueAddSize));
                        int sleepTime = (int) (Constants.TsimElemArrivalIntervalMin * 1000 + r.nextInt((int) ((Constants.TsimElemArrivalIntervalMax - Constants.TsimElemArrivalIntervalMin) * 1000)));
                        try {
                            this.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            this.interrupt();
                        }
                    }
                }
            };
            t.start();
            threads.add(t);
        }
    }

    public static void terminate() {
        for (Thread t : threads) {
            t.interrupt();
        }
        threads.clear();
    }
}
