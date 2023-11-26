package ru.koshkin.listeners;

public interface MessageDequeListener {
    void newMessage() throws InterruptedException;
}
