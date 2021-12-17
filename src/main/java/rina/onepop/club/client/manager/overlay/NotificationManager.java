package rina.onepop.club.client.manager.overlay;

import rina.onepop.club.api.manager.Manager;
import rina.onepop.club.client.component.ComponentNotification;
import me.rina.turok.util.TurokTick;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 29/05/2021 at 00:45
 **/
public class NotificationManager extends Manager {
    public static NotificationManager INSTANCE;

    public static class Notification {

        private String notify;
        private Priority priority;

        private boolean alive;
        private boolean reached;

        public Notification(String notify, Priority priority) {
            this.notify = notify;
            this.priority = priority;

            this.alive = true;
        }

        public void setNotify(String notify) {
            this.notify = notify;
        }

        public String getNotify() {
            return notify;
        }

        public void setPriority(Priority priority) {
            this.priority = priority;
        }

        public Priority getPriority() {
            return priority;
        }

        public void setAlive(boolean alive) {
            this.alive = alive;
        }

        public boolean isAlive() {
            return alive;
        }

        public void setReached(boolean reached) {
            this.reached = reached;
        }

        public boolean isReached() {
            return reached;
        }
    }

    private final List<Notification> notificationList = new ArrayList<>();
    private final TurokTick reachTimer = new TurokTick();

    private int delay;
    private int indexRefresh;

    public NotificationManager() {
        super("Notification Manager", "Notify some actions in client/event.");

        INSTANCE = this;
    }

    public void setTimer(int delay) {
        this.delay = delay;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public static void newNotification(String notify, Priority priority) {
        INSTANCE.notificationList.add(new Notification(notify, priority));
    }

    @Override
    public void onUpdateAll() {
        this.delay = ComponentNotification.settingNormalPriorityTime.getValue().intValue() * 1000;

        if (this.notificationList.isEmpty()) {
            this.reachTimer.reset();

            return;
        }

        for (Notification notifications : new ArrayList<>(this.notificationList)) {
            if (notifications.isReached() && !notifications.isAlive()) {
                this.notificationList.remove(notifications);
            }

            if (notifications.getPriority() == Priority.HIGH && !notifications.isReached()) {
                continue;
            }

            if (this.reachTimer.isPassedMS(this.delay)) {
                notifications.setReached(true);

                this.reachTimer.reset();
            }
        }
    }
}
