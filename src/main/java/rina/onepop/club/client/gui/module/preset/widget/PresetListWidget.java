package rina.onepop.club.client.gui.module.preset.widget;

import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokRect;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.api.preset.Preset;
import rina.onepop.club.api.preset.management.PresetManager;
import rina.onepop.club.api.util.client.DateTimerUtil;
import rina.onepop.club.client.gui.imperador.ImperadorButton;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.module.preset.PresetFrame;
import rina.onepop.club.client.gui.module.preset.ui.PresetUI;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SrRina
 * @since 14/08/2021 at 01:19
 **/
public class PresetListWidget extends Widget {
    private final ModuleClickGUI master;
    private final PresetFrame frame;

    private float offsetX;
    private float offsetY;

    private float offsetW;
    private float offsetH;

    private float scroll;
    private float lastTickScroll;

    private final PresetUI ui;
    private ImperadorButton buttonAddPresets;

    private final List<Widget> loadedWidgetList = new ArrayList<>();
    private PresetWidget deleteWidget;

    private boolean hasWheel;
    private final TurokRect scrollRect = new TurokRect(0, 0);

    private boolean isStarted;

    public PresetListWidget(ModuleClickGUI master, PresetFrame frame, PresetUI ui) {
        super("");

        this.master = master;
        this.frame = frame;

        this.ui = ui;
    }

    public void init() {
        this.buttonAddPresets = new ImperadorButton(this.master.fontWidgetModule, "Add");
    }

    public TurokRect getScrollRect() {
        return scrollRect;
    }

    public ModuleClickGUI getMaster() {
        return master;
    }

    public PresetFrame getFrame() {
        return frame;
    }

    public PresetUI getUi() {
        return ui;
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

    public void delete(Preset preset) {
        for (Widget widgets : this.loadedWidgetList) {
            if (!(widgets instanceof PresetWidget)) {
                continue;
            }

            if (((PresetWidget) widgets).getCurrentPreset().getTag().equalsIgnoreCase(preset.getTag())) {
                this.deleteWidget = (PresetWidget) widgets;

                break;
            }
        }
    }

    public void refresh() {
        this.loadedWidgetList.clear();

        for (Preset presets : Onepop.getPresetManager().getPresetSet()) {
            if (this.contains(presets.getTag()) > 0) {
                continue;
            }

            final PresetWidget presetWidget = new PresetWidget(this.master, this.frame, this.ui, this, presets);

            presetWidget.init();

            this.loadedWidgetList.add(presetWidget);
        }
    }

    public int contains(String tag) {
        int j = 0;

        for (Widget widgets : this.loadedWidgetList) {
            if (!(widgets instanceof PresetWidget)) {
                continue;
            }

            if (((PresetWidget) widgets).getThePreset().getTag().equalsIgnoreCase(tag)) {
                j++;
            }
        }

        return j;
    }

    public void current(String tag) {
        for (Widget widgets : this.loadedWidgetList) {
            if (!(widgets instanceof PresetWidget)) {
                continue;
            }

            final PresetWidget widget = (PresetWidget) widgets;
            final Preset preset = widget.getCurrentPreset();

            if (preset.getTag().equalsIgnoreCase(tag)) {
                preset.setValidator();
            } else {
                preset.unsetValidator();
            }
        }
    }

    public void desync() {
        for (Widget widgets : this.loadedWidgetList) {
            if (!(widgets instanceof PresetWidget)) {
                continue;
            }

            ((PresetWidget) widgets).desync();
        }
    }

    public void sync() {
        for (Widget widgets : this.loadedWidgetList) {
            if (!(widgets instanceof PresetWidget)) {
                continue;
            }

            ((PresetWidget) widgets).sync();
        }
    }

    public void scissor() {
        TurokShaderGL.drawScissor(this.rect.getX(),this.rect.getY() + this.buttonAddPresets.getRect().getHeight() + 1, this.rect.getWidth(), (int) this.getOffsetH());
    }

    public void addPreset() {
        this.ui.setCancellable(true);

        int i = 0;

        for (i = 0; i < 99; i++) {
            final String str = "preset" + i;

            if (this.contains(str) > 0) {
                continue;
            }

            final Preset preset = new Preset(str, DateTimerUtil.time(DateTimerUtil.TIME_AND_DATE));
            final PresetWidget widget = new PresetWidget(this.master, this.frame, this.ui, this, preset);

            widget.init();

            PresetManager.implement(preset);
            PresetManager.sync(preset);

            this.loadedWidgetList.add(widget);

            break;
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

    }

    @Override
    public void onScreenOpened() {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onScreenOpened();
        }
    }

    @Override
    public void onCustomScreenOpened() {

    }

    @Override
    public void onKeyboardPressed(char keyChar, int keyCode) {
        final char c = keyChar;
        final int key = keyCode;

        this.loadedWidgetList.parallelStream().forEach(widgets -> {
            widgets.onKeyboardPressed(c, key);
        });
    }

    @Override
    public void onCustomKeyboardPressed(char keyChar, int keyCode) {
        final char c = keyChar;
        final int key = keyCode;

        this.loadedWidgetList.parallelStream().forEach(widgets -> {
            widgets.onCustomKeyboardPressed(c, key);
        });
    }

    @Override
    public void onMouseReleased(int button) {
        this.buttonAddPresets.onMouseReleased(button);

        for (Widget widgets : this.loadedWidgetList) {
            widgets.onMouseReleased(button);
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {

    }

    @Override
    public void onMouseClicked(int button) {
        this.buttonAddPresets.onMouseClicked(button);

        for (Widget widgets : this.loadedWidgetList) {
            widgets.onMouseClicked(button);
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {

    }

    @Override
    public void onRender() {
        if (!this.isStarted) {
            this.refresh();
            this.isStarted = true;
        }

        this.rect.setWidth(this.frame.getRect().getWidth());

        this.buttonAddPresets.background = new int[] {0, 0, 0, 0};
        this.buttonAddPresets.string = new int[] {255, 255, 255, (int) this.frame.getCurrentTickAlpha(255)};
        this.buttonAddPresets.highlight = new int[] {255, 255, 255, (int) this.frame.getCurrentTickAlpha(75)};
        this.buttonAddPresets.pressed = new int[] {Onepop.getWrapper().base[0], Onepop.getWrapper().base[1], Onepop.getWrapper().base[2], (int) this.frame.getCurrentTickAlpha(200)};
        this.buttonAddPresets.outline = this.buttonAddPresets.pressed;

        this.buttonAddPresets.setToDrawOutline(false);
        this.buttonAddPresets.setRendering(true);
        this.buttonAddPresets.setIsShadow(false);
        this.buttonAddPresets.setPartialTicks(this.master.getDisplay().getPartialTicks());
        this.buttonAddPresets.doMouseOver(this.master.getMouse());
        this.buttonAddPresets.setFont(this.master.fontWidgetModule);

        float o = this.buttonAddPresets.getRect().getHeight() / 6f;
        float h = this.buttonAddPresets.getRect().getHeight() - (o * 2);

        this.buttonAddPresets.setOffsetY(7);
        this.buttonAddPresets.left(h + 3f + 3f);

        this.buttonAddPresets.getRect().setWidth(this.rect.getWidth());
        this.buttonAddPresets.getRect().setHeight(7 + TurokFontManager.getStringHeight(this.buttonAddPresets.getFont(), this.buttonAddPresets.getText()) + 7);

        this.buttonAddPresets.getRect().setX(this.rect.getX());
        this.buttonAddPresets.getRect().setY(this.rect.getY());

        this.buttonAddPresets.onRender();
        this.buttonAddPresets.scissor();

        this.setOffsetH(this.frame.getRect().getHeight() - (this.rect.getY() + this.buttonAddPresets.getRect().getHeight() + 1f));

        TurokRenderGL.color(255, 255, 255, 255);
        TurokRenderGL.drawOutlineRect(this.buttonAddPresets.rect.getX() + o, this.buttonAddPresets.getRect().getY() + o, h, h);

        TurokGL.pushMatrix();

        TurokGL.enable(GL11.GL_BLEND);
        TurokGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        TurokGL.lineSize(2f);

        float x = this.buttonAddPresets.getRect().getX() + 3;
        float y = this.buttonAddPresets.getRect().getY() + o;
        float l = 7f;

        TurokRenderGL.color(255, 255, 255, 255);

        TurokGL.prepare(GL11.GL_LINES);

        TurokGL.addVertex(x + (h / 2f) - (l / 2f), y + (h / 2f));
        TurokGL.addVertex(x + (h / 2f) + (l / 2f) - 0.1f, y + (h / 2f));

        TurokGL.addVertex(x + (h / 2f), y + (h / 2f) - (l / 2f));
        TurokGL.addVertex(x + (h / 2f), y + (h / 2f) + (l / 2f));

        TurokGL.release();
        TurokGL.popMatrix();

        this.lastTickScroll = TurokMath.lerp(this.lastTickScroll, this.scroll, this.master.getDisplay().getPartialTicks());

        if (this.deleteWidget != null) {
            PresetManager.exclude(this.deleteWidget.getThePreset());
            PresetManager.reload();

            this.loadedWidgetList.remove(this.deleteWidget);
            this.refresh();

            this.deleteWidget = null;
        }

        if (this.buttonAddPresets.isReleased()) {
            if (this.buttonAddPresets.isMouseOver()) {
                this.addPreset();
            }

            this.buttonAddPresets.setReleased(false);
        }

        float theHeight = this.buttonAddPresets.getRect().getHeight() + 1f;
        float height = (this.frame.getRect().getHeight() - (this.rect.getY() + this.buttonAddPresets.getRect().getHeight() + 1f));

        TurokGL.enable(GL11.GL_SCISSOR_TEST);
        this.scissor();

        for (Widget widgets : this.loadedWidgetList) {
            widgets.onRender();

            if (widgets instanceof PresetWidget) {
                final PresetWidget widget = (PresetWidget) widgets;

                widget.getRect().setX(this.rect.getX());
                widget.getRect().setY(this.rect.getY() + this.lastTickScroll + theHeight);
            }

            theHeight += widgets.getRect().getHeight() + 1;
        }

        TurokGL.disable(GL11.GL_SCISSOR_TEST);

        this.rect.setHeight(theHeight);

        float limit = height - TurokMath.min(theHeight, height);

        int i = -((this.hasWheel ? Mouse.getDWheel() : 0) / 10);

        this.scrollRect.setX(this.rect.getX());
        this.scrollRect.setY(this.rect.getY() + this.buttonAddPresets.getRect().getHeight() + 1);

        this.scrollRect.setWidth(this.rect.getWidth());
        this.scrollRect.setHeight(this.rect.getHeight() - this.buttonAddPresets.getRect().getHeight() - 1f);

        if (this.scrollRect.collideWithMouse(this.master.getMouse())) {
            this.hasWheel = Mouse.hasWheel();
        } else {
            this.hasWheel = false;
        }

        if (this.hasWheel && this.scrollRect.collideWithMouse(this.master.getMouse())) {
            this.scroll -= i;
        }

        if (this.scroll >= 0) {
            this.scroll = 0;
        }

        if (this.scroll <= limit) {
            this.scroll = limit;
        }
    }

    @Override
    public void onCustomRender() {

    }
}
