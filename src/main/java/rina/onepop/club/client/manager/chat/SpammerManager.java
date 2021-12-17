package rina.onepop.club.client.manager.chat;

import rina.onepop.club.api.manager.Manager;
import rina.onepop.club.api.util.chat.ChatUtil;
import me.rina.turok.util.TurokTick;

import java.util.ArrayList;

/**
 * @author SrRina
 * @since 04/02/2021 at 19:09
 **/
public class SpammerManager extends Manager {
    private ArrayList<String> queue;
    private ArrayList<String> lastQueue;

    private TurokTick tick = new TurokTick();

    private float delay;
    private int limit;

    private boolean isNext;

    public SpammerManager() {
        super("Spammer", "Manage");

        this.queue = new ArrayList<>();
        this.lastQueue = new ArrayList<>();
    }

    public void setQueue(ArrayList<String> queue) {
        this.queue = queue;
    }

    public ArrayList<String> getQueue() {
        return queue;
    }

    public void setLastQueue(ArrayList<String> lastQueue) {
        this.lastQueue = lastQueue;
    }

    public ArrayList<String> getLastQueue() {
        return lastQueue;
    }

    public void setTick(TurokTick tick) {
        this.tick = tick;
    }

    public TurokTick getTick() {
        return tick;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    public float getDelay() {
        return delay;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setNext(boolean next) {
        isNext = next;
    }

    public boolean isNext() {
        return isNext;
    }

    public void send(String message) {
        boolean isAcceptToJoinQueue = true;

        for (String messages : this.queue) {
            if (messages.equalsIgnoreCase(message)) {
                isAcceptToJoinQueue = false;
            }
        }

        if (isAcceptToJoinQueue) {
            this.queue.add(message);
        }
    }

    @Override
    public void onUpdateAll() {
        if (this.queue.size() >= limit) {
            this.queue.clear();
        }

        if (this.queue.isEmpty()) {
            tick.reset();
        }

        for (String messages : new ArrayList<>(this.queue)) {
            if (tick.isPassedMS(delay * 1000f)) {
                this.isNext = true;

                ChatUtil.message(messages);

                this.lastQueue.add(messages);
                this.queue.remove(messages);

                tick.reset();
            } else {
                this.isNext = false;
            }
        }
    }
}
