package rina.onepop.club.client.component;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.Dock;
import rina.onepop.club.api.component.StringType;
import rina.onepop.club.api.component.impl.ComponentSetting;
import rina.onepop.club.client.manager.overlay.NotificationManager;
import me.rina.turok.render.opengl.deprecated.TurokRenderGL;
import me.rina.turok.util.TurokDisplay;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokRect;
import me.rina.turok.util.TurokTick;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author SrRina
 * @since 29/05/2021 at 00:56
 **/
public class ComponentNotification extends Component {
    public static class Rect extends TurokRect {
        private NotificationManager.Notification notification;

        public Rect(NotificationManager.Notification notification, float x, float y) {
            super(notification.getNotify(), x, y);

            this.notification = notification;
        }

        public NotificationManager.Notification getNotification() {
            return notification;
        }
    }

    /* Misc. */
    public static ComponentSetting<Boolean> settingAnimation = new ComponentSetting<>("Animation", "Animation", "Cool animations for the notifications", true);
    public static ComponentSetting<Integer> settingBackgroundAlpha = new ComponentSetting<>("Background Alpha", "Background Alpha", "Background alpha!", 100, 0, 255);
    public static ComponentSetting<Boolean> settingMiddlePosition = new ComponentSetting<>("Middle Position", "MiddlePosition", "Sets component position at middle screen.", true);
    public static ComponentSetting<Number> settingNormalPriorityTime = new ComponentSetting<>("Normal Priority Time", "NormalPriorityTime", "Time for a notification with normal priority.", 10, 1, 60);

    private final List<Rect> rectList = new ArrayList<>();
    private final TurokTick timer = new TurokTick();

    public ComponentNotification() {
        super("Notification", "Notification", "Notify some actions in client/event.", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        final TurokDisplay display = new TurokDisplay(mc);

        if (settingMiddlePosition.getValue()) {
            this.setDock(Dock.TOP_LEFT);

            this.rect.setX((display.getScaledWidth() / 2f) - (this.rect.getWidth() / 2f));
            this.rect.setY(10);
        }

        final Comparator<NotificationManager.Notification> comparatorNotification = (notification1, notification2) -> {
            String k = notification1.getNotify();
            String s = notification2.getNotify();

            float diff = getStringWidth(s) - getStringWidth(k);

            if (this.getDock() == Dock.TOP_LEFT || this.getDock() == Dock.TOP_RIGHT) {
                return diff != 0 ? (int) diff : s.compareTo(k);
            } else {
                return (int) diff;
            }
        };

        final Comparator<Rect> comparatorRect = (rect1, rect2) -> {
            String k = rect1.getNotification().getNotify();
            String s = rect2.getNotification().getNotify();

            float diff = getStringWidth(s) - getStringWidth(k);

            if (this.getDock() == Dock.TOP_LEFT || this.getDock() == Dock.TOP_RIGHT) {
                return diff != 0 ? (int) diff : s.compareTo(k);
            } else {
                return (int) diff;
            }
        };

        boolean top = true;

        List<NotificationManager.Notification> notificationList;

        if (this.getDock() == Dock.TOP_LEFT || this.getDock() == Dock.TOP_RIGHT) {
            notificationList = Onepop.getNotificationManager().getNotificationList().stream().sorted(comparatorNotification).collect(Collectors.toList());
        } else {
            top = false;

            notificationList = Onepop.getNotificationManager().getNotificationList().stream().sorted(Comparator.comparing(notify -> getStringWidth(notify.getNotify()))).collect(Collectors.toList());
        }

        if (!settingAnimation.getValue()) {
            this.rectList.clear();
        }

        for (NotificationManager.Notification notifications : notificationList) {
            Rect rect = new Rect(notifications, 0, 0);

            if (!notifications.isReached()) {
                if (!this.rectList.contains(rect)) {
                    this.rectList.add(rect);
                }
            }

            if (!notifications.isAlive() && this.rectList.contains(rect)) {
                this.rectList.remove(rect);
            }
        }

        if (top) {
            this.rectList.sort(comparatorRect);
        } else {
            this.rectList.sort(Comparator.comparing(rects -> getStringWidth(rects.getNotification().getNotify())));
        }

        TurokRenderGL.enableState(GL11.GL_SCISSOR_TEST);
        TurokRenderGL.drawScissor(this.rect.getX(), this.rect.getY(), this.rect.getWidth(), this.rect.getHeight(), display);

        int cache = 16;

        int offsetX = (this.dock == Dock.TOP_LEFT || this.dock == Dock.BOTTOM_LEFT) ? 0 : 0;
        int offsetW = (this.dock == Dock.TOP_RIGHT || this.dock == Dock.BOTTOM_RIGHT) ? 0 : 0;

        this.rect.setWidth(10);

        for (Rect rects : this.rectList) {
            if (rects.getNotification() == null) {
                continue;
            }

            String tag = rects.getNotification().getNotify();

            rects.setWidth(this.getStringWidth(tag));
            rects.setHeight(this.getStringHeight(tag));

            if (settingBackgroundAlpha.getValue() != 0) {
                this.render((int) rects.getX() + offsetW, cache, this.getStringWidth(tag) + offsetX, this.getStringHeight(tag), new Color(0, 0, 0, settingBackgroundAlpha.getValue()));
            }

            this.render(tag, rects.getX(), cache);

            if (rects.getWidth() >= this.rect.getWidth()) {
                this.rect.setWidth(rects.getWidth());
            }

            if (rects.getNotification().isReached()) {
                rects.setX(TurokMath.lerp(rects.getX(), verifyDock(this.getClamp(rects, 0), rects.getWidth()), partialTicks * 0.1f));
            } else {
                rects.setX(TurokMath.lerp(rects.getX(),0, partialTicks * 0.1f));
            }

            if (!this.verifyClamp(rects, 2)) {
                cache += rects.getHeight();
            } else {
                rects.getNotification().setAlive(false);
            }
        }

        this.rect.setHeight(cache);

        TurokRenderGL.disable(GL11.GL_SCISSOR_TEST);
    }

    public TurokRect getRect(String tag) {
        for (TurokRect rects : this.rectList) {
            if (rects.getTag().equalsIgnoreCase(tag)) {
                return rects;
            }
        }

        return null;
    }

    public boolean verifyClamp(Rect k, int diff) {
        boolean flag = false;

        if (this.dock == Dock.TOP_LEFT || this.dock == Dock.BOTTOM_LEFT) {
            int clamp = this.getClamp(k, diff);

            if (k.getX() <= clamp) {
                flag = true;
            }
        }

        if (this.dock == Dock.TOP_RIGHT || this.dock == Dock.BOTTOM_RIGHT) {
            int clamp = this.getClamp(k, diff);

            if (verifyDock(k.getX(), k.getWidth()) >= clamp) {
                flag = true;
            }
        }

        return flag;
    }

    public int getClamp(Rect k, int diff) {
        if (this.dock == Dock.TOP_LEFT || this.dock == Dock.BOTTOM_LEFT) {
            return (int) ((-k.getWidth()) + diff);
        }

        if (this.dock == Dock.TOP_RIGHT || this.dock == Dock.BOTTOM_RIGHT) {
            return (int) ((this.rect.getWidth() * 2) - diff);
        }

        return (int) this.rect.getX();
    }
}
