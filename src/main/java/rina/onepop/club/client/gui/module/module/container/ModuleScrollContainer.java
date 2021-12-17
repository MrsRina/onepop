package rina.onepop.club.client.gui.module.module.container;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.gui.container.Container;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.api.module.Module;
import rina.onepop.club.api.setting.Setting;
import rina.onepop.club.client.gui.module.category.CategoryFrame;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.module.module.widget.ModuleWidget;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokRect;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

/**
 * @author SrRina
 * @since 20/11/20 at 7:52pm
 */
public class ModuleScrollContainer extends Container {
    private ModuleClickGUI master;
    private CategoryFrame frame;

    private int offsetX;
    private int offsetY;

    private int animationHeight;

    private int offsetWidth;
    private int offsetHeight;

    private boolean hasWheel;
    private TurokRect scrollRect = new TurokRect("Im a girll!!! :)", 0, 0);

    /*
     * Widget list of the components module.
     */
    private ArrayList<Widget> loadedWidgetList;

    public ModuleScrollContainer(ModuleClickGUI master, CategoryFrame frame) {
        super(frame.getRect().getTag() + "Scroll");

        this.master = master;
        this.frame = frame;

        this.offsetX = 1;

        this.init();
    }

    public void init() {
        this.loadedWidgetList = new ArrayList<>();

        int count = 0;

        for (Module modules : Onepop.getModuleManager().getModuleList()) {
            if (modules.getCategory() != this.frame.getCategory()) {
                continue;
            }

            ModuleWidget moduleWidget = new ModuleWidget(this.master, this.frame, this, modules);

            moduleWidget.setAnimationY((int) this.animationHeight);
            moduleWidget.setAnimationX(2);

            this.loadedWidgetList.add(moduleWidget);

            this.animationHeight += moduleWidget.getRect().getHeight() + 1;

            if (count <= this.frame.getMaximumModule()) {
                this.frame.getRect().height += moduleWidget.getRect().getHeight() + 1;
            } else {
                this.frame.setAbleToScissor(true);
            }

            count++;
        }
    }

    public ArrayList<Widget> getLoadedWidgetList() {
        return loadedWidgetList;
    }

    public void refresh(Module module, Setting setting) {
        this.animationHeight = 0;
        this.frame.getRect().setHeight(this.frame.getOffsetHeight() + this.animationHeight);

        int flag = 0;

        for (Widget widgets : this.loadedWidgetList) {
            if (widgets instanceof ModuleWidget) {
                ModuleWidget moduleWidget = (ModuleWidget) widgets;
                moduleWidget.setAnimationY((int) this.animationHeight);
                moduleWidget.setOffsetY((int) this.animationHeight);

                if (moduleWidget.isWidgetOpened()) {
                    if (module.getTag().equalsIgnoreCase(((ModuleWidget) widgets).getModule().getTag())) {
                        moduleWidget.refresh(setting);
                    }

                    this.animationHeight += moduleWidget.getOffsetHeight() + (this.loadedWidgetList.indexOf(widgets) == this.loadedWidgetList.size() ? 1 : 0);
                } else {
                    this.animationHeight += moduleWidget.getRect().getHeight() + 1;
                }

                this.frame.getRect().setHeight(TurokMath.clamp(this.frame.getOffsetHeight() + this.animationHeight, 0, Onepop.getWrapper().clampScrollHeight));
            }
        }
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetWidth(int offsetWidth) {
        this.offsetWidth = offsetWidth;
    }

    public int getOffsetWidth() {
        return offsetWidth;
    }

    public void setOffsetHeight(int offsetHeight) {
        this.offsetHeight = offsetHeight;
    }

    public int getOffsetHeight() {
        return offsetHeight;
    }

    public void setAnimationHeight(int animationHeight) {
        this.animationHeight = animationHeight;
    }

    public int getAnimationHeight() {
        return animationHeight;
    }

    public void clampScroll(int scissorHeight) {
        if (this.offsetY >= 0) {
            this.offsetY--;

            if (this.offsetY <= 1) {
                this.offsetY = 0;
            }
        }

        if (this.offsetY <= (scissorHeight - this.rect.getHeight())) {
            this.offsetY++;

            if (this.offsetX <= (scissorHeight - this.rect.getHeight())) {
                this.offsetX = (int) (scissorHeight - this.rect.getHeight());
            }
        }
    }

    @Override
    public void onScreenOpened() {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onScreenOpened();
        }
    }

    @Override
    public void onCustomScreenOpened() {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onCustomScreenOpened();
        }
    }

    @Override
    public void onScreenClosed() {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onScreenClosed();
        }
    }

    @Override
    public void onCustomScreenClosed() {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onCustomScreenClosed();
        }
    }

    @Override
    public void onKeyboardPressed(char charCode, int keyCode) {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onKeyboardPressed(charCode, keyCode);
        }
    }

    @Override
    public void onCustomKeyboardPressed(char charCode, int keyCode) {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onCustomKeyboardPressed(charCode, keyCode);
        }
    }

    @Override
    public void onMouseReleased(int button) {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onMouseReleased(button);
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onCustomMouseReleased(button);
        }
    }

    @Override
    public void onMouseClicked(int button) {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onMouseClicked(button);
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onCustomMouseClicked(button);
        }
    }

    @Override
    public void onRender() {
        double diffValue = this.frame.getRect().getHeight() - (this.frame.getOffsetHeight() + this.animationHeight);
        double diffFinal = TurokMath.sqrt(diffValue * diffValue);

        if (diffFinal < 20f) {
            this.frame.getRect().setHeight(TurokMath.clamp(this.frame.getOffsetHeight() + this.animationHeight, 0, Onepop.getWrapper().clampScrollHeight));
        } else {
            this.frame.getRect().setHeight(TurokMath.lerp(this.frame.getRect().getHeight(), TurokMath.clamp(this.frame.getOffsetHeight() + this.animationHeight, 0, Onepop.getWrapper().clampScrollHeight), this.master.getDisplay().getPartialTicks()));
        }

        this.rect.setX(this.frame.getRect().getX() + this.offsetX);
        this.rect.setY(TurokMath.lerp(this.rect.getY(), this.frame.getRect().getY() + this.frame.getOffsetHeight() + this.offsetY, this.master.getDisplay().getPartialTicks()));

        this.rect.setWidth(this.frame.getRect().getWidth() - (this.offsetX * 2));
        this.rect.setHeight(this.animationHeight);

        int realHeightScissor = (int) (this.frame.getRect().getHeight() - this.frame.getOffsetHeight());

        for (Widget widgets : this.loadedWidgetList) {
            TurokShaderGL.pushScissor();
            TurokShaderGL.drawScissor(this.rect.getX(), this.frame.getRect().getY() + this.frame.getOffsetHeight(), (this.rect.getX() + this.rect.getWidth() >= this.master.getClosedWidth() ? this.master.getClosedWidth() - (this.rect.getX() + this.rect.getWidth()) : this.rect.getWidth()), realHeightScissor);

            widgets.onRender();

            TurokShaderGL.popScissor();

            if (widgets instanceof ModuleWidget) {
                ModuleWidget moduleWidget = (ModuleWidget) widgets;
                moduleWidget.flagMouse = Flag.MOUSE_NOT_OVER;
            }
        }
    }

    @Override
    public void onCustomRender() {
        int realHeightScissor = (int) (this.frame.getRect().getHeight() - this.frame.getOffsetHeight());

        this.scrollRect.set(this.rect.getX(), this.frame.getRect().getY() + this.frame.getOffsetHeight(), this.rect.getWidth(), realHeightScissor);

        int minimumScroll = (int) ((this.frame.getRect().getHeight() - this.frame.getOffsetHeight()) - this.rect.getHeight());
        int maximumScroll = 0;

        boolean isScrollLimit = this.rect.getY() + this.rect.getHeight() >= this.frame.getRect().getY() + this.frame.getRect().getHeight() - realHeightScissor - 1;

        int i = -((this.hasWheel ? Mouse.getDWheel() : 0) / 10);

        if (scrollRect.collideWithMouse(this.master.getMouse())) {
            this.hasWheel = Mouse.hasWheel();
        } else {
            this.hasWheel = false;
        }

        if (this.hasWheel && isScrollLimit && scrollRect.collideWithMouse(this.master.getMouse()) && !(Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))) {
            this.offsetY -= i;
        }

        if (this.offsetY <= minimumScroll) {
            this.offsetY = minimumScroll;
        } else if (this.offsetY >= maximumScroll) {
            this.offsetY = maximumScroll;
        }

        for (Widget widgets : this.loadedWidgetList) {
            if (scrollRect.collideWithMouse(this.master.getMouse())) {
                widgets.onCustomRender();
            }
        }
    }
}