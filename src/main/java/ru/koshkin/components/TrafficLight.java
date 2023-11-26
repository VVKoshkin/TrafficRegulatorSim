package ru.koshkin.components;

import ru.koshkin.Constants;
import ru.koshkin.communication.RequestOptions;
import ru.koshkin.communication.ResponseDetailsOptions;
import ru.koshkin.communication.ResponseOptions;
import ru.koshkin.controllers.TrafficLightMessagesBroker;
import ru.koshkin.entities.Message;
import ru.koshkin.entities.MessageRequest;
import ru.koshkin.entities.MessageResponse;
import ru.koshkin.enums.LIGHTS;
import ru.koshkin.enums.TrafficLightStatus;
import ru.koshkin.helpers.TrafficLightsConcurrents;
import ru.koshkin.listeners.MessageDequeWithListener;
import ru.koshkin.listeners.QueueWithListener;

import javax.swing.*;
import java.util.*;

import static ru.koshkin.enums.LIGHTS.resolveToColor;

public class TrafficLight extends JButton {

    private String id;

    private JLabel statusLabel = new JLabel();
    private TrafficLightStatus status;

    private QueueWithListener queue = new QueueWithListener();
    private JTextField querySizeField = new JTextField(2);

    private Integer uid = null;
    private String bookedBy;

    private MessageDequeWithListener<Message> deque = new MessageDequeWithListener<>();
    private List<String> concurrentTrafficLightsIds;
    private LIGHTS currentLight;
    private HashMap<String, String> answerHashMap = new HashMap<>();
    private Thread passing;

    private Long timeWorkingAsGreen = 0L;
    private Long timeFromBeginningQueryFilling = null;

    private boolean isPedestrianLight;


    public TrafficLight(LIGHTS light, String id) {
        super();
        this.setEnabled(false);
        this.setBackground(light);
        this.currentLight = light;
        this.setText(id);
        this.id = id;
        this.querySizeField.setText(queue.getQueueSize().toString());
        this.querySizeField.setEnabled(false);
        this.concurrentTrafficLightsIds = TrafficLightsConcurrents.getConcurrentLightsForTrafficLight(this);
        setStatus(TrafficLightStatus.IDLE);
        this.isPedestrianLight = this.id.startsWith("n") || this.id.startsWith("w") || this.id.startsWith("e") || this.id.startsWith("s");
    }

    private void setStatus(TrafficLightStatus status) {
        this.status = status;
        this.statusLabel.setText(status.name());
    }

    public JLabel getStatusLabel() {
        return this.statusLabel;
    }

    public JTextField getLightQuerySizeField() {
        return this.querySizeField;
    }

    public void setBackground(LIGHTS l) {
        this.setBackground(resolveToColor(l));
    }

    public void addToQueue(Long amount) {
        queue.addAmount(amount);
        this.querySizeField.setText(queue.getQueueSize().toString());
    }

    public String getId() {
        return id;
    }

    public void startObservingSituation() {
        this.deque.addListener(() -> {
            Message m = this.deque.poll();
            Integer mUid = m.getUid();
            String mFromId = m.getFromId();
            if (m instanceof MessageRequest) {
                MessageRequest mR = (MessageRequest) m;
                String mOption = mR.getOption();
                switch (mOption) {
                    case RequestOptions.ALLOW_BECOME_GREEN: {
                        Long mSize = mR.getQuerySize();
                        if (this.status.equals(TrafficLightStatus.PASSING)) {
                            if (System.currentTimeMillis() - timeWorkingAsGreen < Constants.Tgreen * 1000) {
                                makeAndSendResponse(mUid, mFromId, ResponseOptions.WAIT, ResponseDetailsOptions.JUST_GREEN, null);
                            } else if (bookedBy == null) {
                                if (mSize > queue.getQueueSize()) {
                                    bookedBy = mFromId;
                                    makeAndSendResponse(mUid, mFromId, ResponseOptions.YES, ResponseDetailsOptions.ALLOWED, mFromId);
                                } else
                                    makeAndSendResponse(mUid, mFromId, ResponseOptions.NO, ResponseDetailsOptions.DECLINED, null);
                            } else {
                                makeAndSendResponse(mUid, mFromId, ResponseOptions.NO, ResponseDetailsOptions.BOOKED, bookedBy);
                            }
                        } else {
                            if (bookedBy != null)
                                makeAndSendResponse(mUid, mFromId, ResponseOptions.NO, ResponseDetailsOptions.BOOKED, bookedBy);
                            else if (uid != null)
                                makeAndSendResponse(mUid, mFromId, ResponseOptions.WAIT, ResponseDetailsOptions.RED_BUT_CAN_CHANGE, null);
                            else {
                                bookedBy = mFromId;
                                makeAndSendResponse(mUid, mFromId, ResponseOptions.YES, ResponseDetailsOptions.RED, mFromId);
                            }
                        }
                        break;
                    }
                    case RequestOptions.CONFIRM_ME_GREEN: {
                        this.uid = null;
                        this.bookedBy = null;
                        if (this.status.equals(TrafficLightStatus.PASSING))
                            stopPassingMyQueue();
                        makeAndSendResponse(mUid, mFromId, ResponseOptions.YES, ResponseDetailsOptions.CONFIRMED, null);
                        break;
                    }
                    case RequestOptions.FORGET_ME: {
                        if (this.bookedBy != null && this.bookedBy.equals(mFromId))
                            this.bookedBy = null;
                        break;
                    }
                }
            } else if (m instanceof MessageResponse && uid != null && m.getUid().equals(uid)) {
                MessageResponse mR = (MessageResponse) m;
                String mRResponseOption = mR.getResponseOption();
                String mRResponseDetailsOption = mR.getResponseDetailsOption();
                switch (status) {
                    case PENDING_RESPONSE: {
                        switch (mRResponseOption) {
                            case ResponseOptions.YES: {
                                answerHashMap.put(mFromId, ResponseOptions.YES);
                                break;
                            }
                            case ResponseOptions.NO:
                            case ResponseOptions.WAIT: {
                                if (mRResponseDetailsOption.equals(ResponseDetailsOptions.BOOKED)) {
                                    String mRBookedBy = mR.getBookedBy();
                                    if (!concurrentTrafficLightsIds.contains(mRBookedBy)) {
                                        answerHashMap.put(mFromId, ResponseOptions.YES);
                                    } else {
                                        answerHashMap.put(mFromId, ResponseOptions.NO);
                                    }
                                } else {
                                    answerHashMap.put(mFromId, ResponseOptions.NO);
                                }
                                break;
                            }
                        }
                        if (!answerHashMap.containsValue(ResponseOptions.NO_DATA)) {
                            if (answerHashMap.containsValue(ResponseOptions.NO))
                                repeatRequestAfterAWhile(Constants.Trepeat, Constants.Trepeat + 10);
                            else {
                                setStatus(TrafficLightStatus.PENDING_CONFIRMATION);
                                makeAndSendRequests(RequestOptions.CONFIRM_ME_GREEN);
                            }
                        }
                        break;
                    }
                    case PENDING_CONFIRMATION: {
                        if (mRResponseOption.equals(ResponseOptions.YES)) {
                            answerHashMap.put(mFromId, ResponseOptions.YES);
                        }
                        if (!answerHashMap.containsValue(ResponseOptions.NO_DATA)) {
                            startPassingMyQueue();
                        }
                        break;
                    }
                }

            }
        });
        this.queue.addListener(() -> {
            switch (status) {
                case IDLE: {
                    if (currentLight.equals(LIGHTS.RED)) {
                        if (queue.getQueueSize() == 0L)
                            break;
                        if (queue.getQueueSize() >= Constants.Pmin || (timeFromBeginningQueryFilling != null && timeFromBeginningQueryFilling > Constants.Tmin * 1000) &&
                                bookedBy == null && uid == null) {
                            setStatus(TrafficLightStatus.PENDING_RESPONSE);
                            makeAndSendRequests(RequestOptions.ALLOW_BECOME_GREEN);
                        } else {
                            if (timeFromBeginningQueryFilling == null) {
                                timeFromBeginningQueryFilling = System.currentTimeMillis();
                            }
                        }
                    }
                    break;
                }
                case PASSING:
                case PENDING_RESPONSE:
                case PENDING_CONFIRMATION:
                case ON_TIMER:
                    break;
            }
        });
    }


    private void repeatRequestAfterAWhile(float tWaitMin, float tWaitMax) {
        Thread temp = new Thread(() -> {
            try {
                setStatus(TrafficLightStatus.ON_TIMER);
                makeAndSendRequests(RequestOptions.FORGET_ME);
                uid = null;
                Random r = new Random();
                float tWait = tWaitMin + r.nextInt((int) (tWaitMax - tWaitMin));
                Thread.sleep((long) (tWait * 1000));
                setStatus(TrafficLightStatus.PENDING_RESPONSE);
                makeAndSendRequests(RequestOptions.ALLOW_BECOME_GREEN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        temp.start();
    }

    private void prepareAnswerHashMap() {
        answerHashMap.clear();
        for (String concurrentTrafficLightsId : concurrentTrafficLightsIds) {
            answerHashMap.put(concurrentTrafficLightsId, ResponseOptions.NO_DATA);
        }

    }


    private void startPassingMyQueue() {
        passing = new Thread(() -> {
            if (!this.isPedestrianLight) {
                setColor(LIGHTS.YELLOW);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            setColor(LIGHTS.GREEN);
            setStatus(TrafficLightStatus.PASSING);
            System.out.println("Traffic light " + this.id + " became green! AHM: " + answerHashMap);
            timeWorkingAsGreen = System.currentTimeMillis();
            prepareAnswerHashMap();
            Random r = new Random();
            while (!passing.isInterrupted()) {
                queue.removeAmount(1 + (long) r.nextInt(4));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    passing.interrupt();
                }
            }
        });
        passing.start();
    }

    private void stopPassingMyQueue() {
        if (passing != null && passing.isAlive()) {
            passing.interrupt();
        }
        Thread temp = new Thread(() -> {
            if (!this.isPedestrianLight) {
                setColor(LIGHTS.YELLOW);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            setColor(LIGHTS.RED);
            setStatus(TrafficLightStatus.IDLE);
            System.out.println("Traffic light " + this.id + " became RED!");
        });
        temp.start();
    }

    private void setColor(LIGHTS l) {
        this.currentLight = l;
        this.setBackground(l);
    }

    private void makeAndSendRequests(String option) {
        if (this.uid == null)
            this.uid = generateUid();
        prepareAnswerHashMap();
        for (String concurrentTrafficLightsId : concurrentTrafficLightsIds) {
            Message m = new MessageRequest(uid, this.getId(), concurrentTrafficLightsId, queue.getQueueSize(), option);
            TrafficLightMessagesBroker.putNewMessage(m);
        }
    }


    private void makeAndSendResponse(Integer uid, String toId, String response, String responseDetails, String bookedBy) {
        Message m = new MessageResponse(uid, this.getId(), toId, response, responseDetails, bookedBy);
        TrafficLightMessagesBroker.putNewMessage(m);
    }

    private static Integer generateUid() {
        Random r = new Random();
        return 500000 + r.nextInt(500000);
    }

    public Deque<Message> getDeque() {
        return deque;
    }

    public void stopObservingSituation() {
        this.deque.removeListener();
        this.queue.removeListener();
    }
}
