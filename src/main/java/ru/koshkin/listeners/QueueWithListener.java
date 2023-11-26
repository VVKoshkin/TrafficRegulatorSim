package ru.koshkin.listeners;

public class QueueWithListener {

    private QueueListener listener;

    private Long queueSize = 0L;

    public void addListener(QueueListener toAdd) {
        this.listener = toAdd;
    }

    public void removeListener() {
        this.listener = null;
    }


    public Long getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Long queueSize) {
        this.queueSize = queueSize;
        if (this.listener != null)
            this.listener.onChange();
    }

    public void addAmount(Long amount) {
        queueSize += amount;
        if (this.listener != null)
            this.listener.onChange();
    }

    public void removeAmount(Long amount) {
        if (queueSize > 0 && queueSize - amount > 0)
            queueSize -= amount;
    }
}
