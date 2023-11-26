package ru.koshkin.listeners;

import java.util.ArrayDeque;

public class MessageDequeWithListener<E> extends ArrayDeque<E> {

    private MessageDequeListener listener;

    public void addListener(MessageDequeListener toAdd) {
        this.listener = toAdd;
    }
    public void removeListener() {
        this.listener = null;
    }

    @Override
    public boolean add(E e) {
        super.add(e);
        try {
            if (this.listener != null)
                listener.newMessage();
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

}
