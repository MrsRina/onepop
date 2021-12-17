package rina.onepop.club.client.gui.module.preset;

import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokDisplay;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokRect;
import org.lwjgl.input.Keyboard;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.frame.Frame;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.api.preset.Preset;
import rina.onepop.club.api.preset.management.PresetManager;
import rina.onepop.club.client.gui.imperador.ImperadorLabel;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.module.preset.ui.PresetUI;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 13/08/2021 at 00:28
 **/
public class PresetFrame extends Frame {
    public static final float OFFSET = 9f;
    public static final float SIZE   = 3f;
    public static final float SIDJAW = 4f;

    private final ModuleClickGUI master;

    private float offsetX;
    private float offsetY;

    private float offsetW;
    private float offsetH;

    private float w;
    private float alpha;

    private float lastTickW;
    private float lastTickAlpha;

    private float resizeX;
    private float resizeY;

    private boolean isLocked;
    private boolean isShow;

    private boolean isResizing;
    protected boolean isUpdate;

    protected boolean isCanceledToCloseFrame;

    private List<Widget> loadedWidgetList;
    private ImperadorLabel labelCurrentPreset;

    private final TurokRect rectShow = new TurokRect("Show", 0, 0);
    private final TurokRect rectList = new TurokRect("List", 0, 0);

    private final TurokRect rectResize = new TurokRect("Resize", 0, 0);
    private PresetUI presetUI;

    public Flag flagMouse = Flag.MOUSE_NOT_OVER;

    public PresetFrame(ModuleClickGUI master) {
        super("Preset");

        this.master = master;
        this.offsetW = 100;
    }

    public void init() {
        this.loadedWidgetList = new ArrayList<>();

        this.loadedWidgetList.add(new ImperadorLabel(this.master.fontFrameCategory, "Preset Manager"));
        this.loadedWidgetList.add(this.labelCurrentPreset = new ImperadorLabel(this.master.fontWidgetModule, "Default"));

        this.labelCurrentPreset.setOffsetY(OFFSET);

        this.presetUI = new PresetUI(this.master, this);
        this.presetUI.init();
    }

    public ModuleClickGUI getMaster() {
        return master;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetW(float offsetW) {
        this.offsetW = offsetW;
    }

    public float getOffsetW() {
        return offsetW;
    }

    public void setOffsetH(float offsetH) {
        this.offsetH = offsetH;
    }

    public float getOffsetH() {
        return offsetH;
    }

    public void setLastTickW(float lastTickW) {
        this.lastTickW = lastTickW;
    }

    public float getLastTickW() {
        return lastTickW;
    }

    public void setLastTickAlpha(float lastTickAlpha) {
        this.lastTickAlpha = lastTickAlpha;
    }

    public float getLastTickAlpha() {
        return lastTickAlpha;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setResizing(boolean resizing) {
        isResizing = resizing;
    }

    public boolean isResizing() {
        return isResizing;
    }

    public TurokRect getRectList() {
        return rectList;
    }

    public TurokRect getRectResize() {
        return rectResize;
    }

    public TurokRect getRectShow() {
        return rectShow;
    }

    public float getCurrentTickAlpha(int alpha) {
        return TurokMath.clamp(this.getLastTickAlpha(), 0, TurokMath.clamp(alpha, 0, 255));
    }

    public void setCanceledToCloseFrame(boolean canceledToCloseFrame) {
        isCanceledToCloseFrame = canceledToCloseFrame;
    }

    public boolean isCanceledToCloseFrame() {
        return isCanceledToCloseFrame;
    }

    @Override
    public void onScreenClosed() {
        this.setResizing(false);
        this.setShow(false);
        this.setLocked(false);

        this.presetUI.onScreenClosed();
    }

    @Override
    public void onCustomScreenClosed() {

    }

    @Override
    public void onScreenOpened() {
        this.presetUI.onScreenOpened();
    }

    @Override
    public void onCustomScreenOpened() {

    }

    @Override
    public void onKeyboardPressed(char character, int key) {
        if (this.isLocked()) {
            this.presetUI.onKeyboardPressed(character, key);

            if (key == Keyboard.KEY_ESCAPE && !this.isCanceledToCloseFrame()) {
                this.setLocked(false);
            }
        }
    }

    @Override
    public void onCustomKeyboardPressed(char character, int key) {
        if (this.isLocked()) {
            this.presetUI.onCustomKeyboardPressed(character, key);
        }
    }

    @Override
    public void onMouseReleased(int button) {
        if (this.isResizing()) {
            this.setResizing(false);
        }

        if (this.isLocked()) {
            this.presetUI.onMouseReleased(button);
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {

    }

    @Override
    public void onMouseClicked(int button) {
        if (this.isLocked()) {
            if (this.rectResize.collideWithMouse(this.master.getMouse()) && button == 0) {
                this.resizeX = this.master.getMouse().getX() - this.rectResize.getX();
                this.resizeY = this.master.getMouse().getY() - this.rectResize.getY();

                this.setResizing(true);
            }

            this.presetUI.onMouseClicked(button);
        }

        if (this.isShow()) {
            this.master.setCanceledCloseGUI(true);
            this.setLocked(true);
        }

        if (!this.isShow() && this.flagMouse == Flag.MOUSE_NOT_OVER) {
            this.master.setCanceledCloseGUI(false);
            this.setLocked(false);
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {

    }

    @Override
    public void onRender() {
        final TurokDisplay display = this.master.getDisplay();

        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
        this.isShow = this.rectShow.collideWithMouse(this.master.getMouse());

        this.rect.setX(0);
        this.rect.setY(0);

        this.rect.setWidth(this.getLastTickW());
        this.rect.setHeight(display.getScaledHeight());

        this.setLastTickW(TurokMath.lerp(this.getLastTickW(), this.w, display.getPartialTicks()));
        this.setLastTickAlpha(TurokMath.lerp(this.getLastTickAlpha(), this.alpha, display.getPartialTicks()));

        this.rectList.setX(0);
        this.rectList.setY(0);

        this.rectList.setWidth(this.rect.getWidth());

        this.rectShow.setX(0);
        this.rectShow.setY(0);

        this.rectShow.setWidth(4);
        this.rectShow.setHeight(display.getScaledHeight());

        this.rectResize.setX(this.rect.getWidth() - SIDJAW);
        this.rectResize.setY(0);

        this.rectResize.setWidth(SIDJAW);
        this.rectResize.setHeight(display.getScaledHeight());

        final Preset preset = PresetManager.current();

        this.labelCurrentPreset.setText(preset != null ? preset.getTag() + " - " + preset.getData() : "Default");
        this.labelCurrentPreset.scissor();

        TurokGL.color(0, 0, 0, this.getCurrentTickAlpha(230));
        TurokRenderGL.drawSolidRect(this.rect);

        if (this.isLocked) {
            this.master.setCanceledCloseGUI(true);
            this.isUpdate = true;
        } else {
            if (this.isUpdate) {
                this.master.setCanceledCloseGUI(false);
                this.isUpdate = false;
            }
        }

        ((ImperadorLabel) this.loadedWidgetList.get(0)).setFont(this.master.fontFrameCategory);
        ((ImperadorLabel) this.loadedWidgetList.get(1)).setFont(this.master.fontWidgetModule);

        if (this.isShow || this.isLocked) {
            this.w = this.offsetW;
            this.alpha = 255;

            float wayY = 0;

            for (Widget widgets : this.loadedWidgetList) {
                widgets.onRender();

                if (widgets instanceof ImperadorLabel) {
                    final ImperadorLabel label = (ImperadorLabel) widgets;

                    label.left(SIZE);

                    label.getRect().setX(0);
                    label.getRect().setY(wayY);

                    label.setShadow(false);
                    label.scissor();

                    label.string = new int[] {255, 255, 255, (int) this.getCurrentTickAlpha(255)};

                    label.getRect().setWidth(this.rect.getWidth());
                    label.getRect().setHeight(label.getOffsetY() + TurokFontManager.getStringHeight(label.getFont(), label.getText()) + label.getOffsetY());

                    wayY += label.getRect().getHeight() + 1;
                }
            }

            this.rectList.setHeight(wayY);

            if (this.isResizing()) {
                this.offsetW = TurokMath.clamp(this.master.getMouse().getX() - this.resizeX, 100, 8098) + SIDJAW;
            }

            this.presetUI.onRender();
        } else {
            this.w = 0;
            this.alpha = 0;
        }
    }

    @Override
    public void onCustomRender() {

    }
}
