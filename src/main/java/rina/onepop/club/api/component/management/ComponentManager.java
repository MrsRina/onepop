package rina.onepop.club.api.component.management;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.component.Component;
import me.rina.turok.render.font.TurokFont;
import me.rina.turok.util.TurokDisplay;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokRect;
import me.rina.turok.util.TurokTick;
import rina.onepop.club.api.component.impl.ComponentSetting;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class ComponentManager {
    public static ComponentManager INSTANCE;

    private ArrayList<Component> componentList;

    /*
     * The corners, so, we push and drooop;
     */
    private ArrayList<Component> componentListTopLeft;
    private ArrayList<Component> componentListTopRight;

    private ArrayList<Component> componentListBottomLeft;
    private ArrayList<Component> componentListBottomRight;

    private final TurokTick tickRefresh = new TurokTick();
    private boolean isUpdate;

    /*
     * Variables rect to all corner dock;
     */
    private TurokRect rectTopLeft = new TurokRect("TopLeft", 0, 0, 10, 0);
    private TurokRect rectTopRight = new TurokRect("TopRight", 0, 0, 10, 0);
    private TurokRect rectBottomLeft = new TurokRect("BottomLeft", 0, 0, 10, 0);
    private TurokRect rectBottomRight = new TurokRect("BottomRight", 0, 0, 10, 0);

    public TurokFont font;

    private int offsetChat;

    public ComponentManager() {
        INSTANCE = this;

        this.componentList = new ArrayList<>();

        this.componentListTopLeft = new ArrayList<>();
        this.componentListTopRight = new ArrayList<>();

        this.componentListBottomLeft = new ArrayList<>();
        this.componentListBottomRight = new ArrayList<>();

        this.font = new TurokFont(new Font("Whitney", 0, 19), true, true);
    }

    public void registry(Component component) {
        try {
            for (Field fields : component.getClass().getDeclaredFields()) {
                if (ComponentSetting.class.isAssignableFrom(fields.getType())) {
                    if (!fields.isAccessible()) {
                        fields.setAccessible(true);
                    }

                    final ComponentSetting<?> setting = (ComponentSetting<?>) fields.get(component);

                    component.registry(setting);
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        this.componentList.add(component);
    }

    public void setFont(TurokFont font) {
        this.font = font;
    }

    public TurokFont getFont() {
        return font;
    }

    public void setComponentList(ArrayList<Component> componentList) {
        this.componentList = componentList;
    }

    public ArrayList<Component> getComponentList() {
        return componentList;
    }

    public void setComponentListTopLeft(ArrayList<Component> componentListTopLeft) {
        this.componentListTopLeft = componentListTopLeft;
    }

    public ArrayList<Component> getComponentListTopLeft() {
        return componentListTopLeft;
    }

    public void setComponentListTopRight(ArrayList<Component> componentListTopRight) {
        this.componentListTopRight = componentListTopRight;
    }

    public ArrayList<Component> getComponentListTopRight() {
        return componentListTopRight;
    }

    public void setComponentListBottomLeft(ArrayList<Component> componentListBottomLeft) {
        this.componentListBottomLeft = componentListBottomLeft;
    }

    public ArrayList<Component> getComponentListBottomLeft() {
        return componentListBottomLeft;
    }

    public void setComponentListBottomRight(ArrayList<Component> componentListBottomRight) {
        this.componentListBottomRight = componentListBottomRight;
    }

    public ArrayList<Component> getComponentListBottomRight() {
        return componentListBottomRight;
    }

    public void setRectTopLeft(TurokRect rectTopLeft) {
        this.rectTopLeft = rectTopLeft;
    }

    public TurokRect getRectTopLeft() {
        return rectTopLeft;
    }

    public void setRectTopRight(TurokRect rectTopRight) {
        this.rectTopRight = rectTopRight;
    }

    public TurokRect getRectTopRight() {
        return rectTopRight;
    }

    public void setRectBottomLeft(TurokRect rectBottomLeft) {
        this.rectBottomLeft = rectBottomLeft;
    }

    public TurokRect getRectBottomLeft() {
        return rectBottomLeft;
    }

    public void setRectBottomRight(TurokRect rectBottomRight) {
        this.rectBottomRight = rectBottomRight;
    }

    public TurokRect getRectBottomRight() {
        return rectBottomRight;
    }

    public static Component get(Class clazz) {
        for (Component components : INSTANCE.getComponentList()) {
            if (components.getClass() == clazz) {
                return components;
            }
        }

        return null;
    }

    public static Component get(String tag) {
        for (Component components : INSTANCE.getComponentList()) {
            if (components.getRect().getTag().equalsIgnoreCase(tag)) {
                return components;
            }
        }

        return null;
    }

    public void onSaveList() {
        for (Component components : this.componentList) {
            components.onSave();
        }
    }

    public void onLoadList() {
        for (Component components : this.componentList) {
            components.onLoad();
        }
    }

    public void onRenderComponentList() {
        for (Component components : this.componentList) {
            if (components.isEnabled()) {
                components.onRender();
            }
        }
    }

    public void onCornerDetectorComponentList(float partialTicks) {
        partialTicks = Onepop.getClientEventManager().getCurrentRender2DPartialTicks();

        TurokDisplay display = new TurokDisplay(Minecraft.getMinecraft());

        if (Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatOpen()) {
            this.offsetChat = (int) TurokMath.serp(this.offsetChat, 14, partialTicks);
        } else {
            this.offsetChat = (int) TurokMath.serp(this.offsetChat, 0, partialTicks);
        }

        if (Display.wasResized() && !this.isUpdate) {
            this.isUpdate = true;
            this.tickRefresh.reset();
        }

        if (this.tickRefresh.isPassedMS(13500)) {
            this.isUpdate = false;
        }

        for (Component components : this.componentList) {
            if (this.isUpdate) {
                return;
            }

            if (components.isEnabled() && components.getRect().collideWithRect(this.rectTopLeft)) {
                if (!this.componentListTopLeft.contains(components)) {
                    this.componentListTopLeft.add(components);
                }
            } else {
                if (this.componentListTopLeft.contains(components) && !this.isUpdate) {
                    this.componentListTopLeft.remove(components);
                }
            }

            if (components.isEnabled() && components.getRect().collideWithRect(this.rectTopRight)) {
                if (!this.componentListTopRight.contains(components)) {
                    this.componentListTopRight.add(components);
                }
            } else {
                if (this.componentListTopRight.contains(components) && !this.isUpdate) {
                    this.componentListTopRight.remove(components);
                }
            }

            if (components.isEnabled() && components.getRect().collideWithRect(this.rectBottomLeft)) {
                if (!this.componentListBottomLeft.contains(components)) {
                    this.componentListBottomLeft.add(components);
                }
            } else {
                if (this.componentListBottomLeft.contains(components) && !this.isUpdate) {
                    this.componentListBottomLeft.remove(components) ;
                }
            }

            if (components.isEnabled() && components.getRect().collideWithRect(this.rectBottomRight)) {
                if (!this.componentListBottomRight.contains(components)) {
                    this.componentListBottomRight.add(components);
                }
            } else {
                if (this.componentListBottomRight.contains(components) && !this.isUpdate) {
                    this.componentListBottomRight.remove(components);
                }
            }
        }

        int memoryPositionLengthTopLeft = 1;

        for (Component components : this.componentListTopLeft) {
            if (!components.isDragging()) {
                components.getRect().setX(1);
                components.getRect().setY(memoryPositionLengthTopLeft);
            }

            memoryPositionLengthTopLeft = (int) (components.getRect().getY() + components.getRect().getHeight() + 1);
        }

        this.rectTopLeft.setX(1);
        this.rectTopLeft.setY(1);

        this.rectTopLeft.setHeight(memoryPositionLengthTopLeft);

        int memoryPositionLengthTopRight = 1;

        for (Component components : this.componentListTopRight) {
            if (!components.isDragging()) {
                components.getRect().setX(display.getScaledWidth() - components.getRect().getWidth() - 1);
                components.getRect().setY(memoryPositionLengthTopRight);
            }

            memoryPositionLengthTopRight = (int) (components.getRect().getY() + components.getRect().getHeight() + 1);
        }

        this.rectTopRight.setX(display.getScaledWidth() - this.rectTopRight.getWidth() - 1);
        this.rectTopRight.setY(1);

        this.rectTopRight.setHeight(memoryPositionLengthTopRight);

        int memoryPositionLengthBottomLeft = display.getScaledHeight() - this.offsetChat - 1;

        for (Component components : this.componentListBottomLeft) {
            if (!components.isDragging()) {
                components.getRect().setX(1);
                components.getRect().setY(memoryPositionLengthBottomLeft - components.getRect().getHeight() - 1);
            }

            memoryPositionLengthBottomLeft = (int) (components.getRect().getY() - 1);
        }

        this.rectBottomLeft.setX(1);
        this.rectBottomLeft.setY(display.getScaledHeight() - this.rectBottomLeft.getHeight() - 1);

        this.rectBottomLeft.setHeight(display.getScaledHeight() - memoryPositionLengthBottomLeft);

        int memoryPositionLengthBottomRight = display.getScaledHeight();

        for (Component components : this.componentListBottomRight) {
            if (!components.isDragging()) {
                components.getRect().setX(display.getScaledWidth() - components.getRect().getWidth() - 1);
                components.getRect().setY(memoryPositionLengthBottomRight - components.getRect().getHeight() - this.offsetChat - 1);
            }

            memoryPositionLengthBottomRight = (int) (components.getRect().getY() - 1);
        }

        this.rectBottomRight.setX(display.getScaledWidth() - this.rectBottomRight.getWidth() - 1);
        this.rectBottomRight.setY(display.getScaledHeight() - this.rectBottomRight.getHeight() - 1);

        this.rectBottomRight.setHeight(display.getScaledHeight() - memoryPositionLengthBottomRight);
    }
}