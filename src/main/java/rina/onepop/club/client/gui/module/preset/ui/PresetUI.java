package rina.onepop.club.client.gui.module.preset.ui;

import me.rina.turok.render.font.management.TurokFontManager;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.gui.container.Container;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.preset.management.PresetManager;
import rina.onepop.club.api.util.chat.ChatUtil;
import rina.onepop.club.client.gui.imperador.ImperadorButton;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.module.preset.PresetFrame;
import rina.onepop.club.client.gui.module.preset.widget.PresetListWidget;

/**
 * @author SrRina
 * @since 13/08/2021 at 20:22
 **/
public class PresetUI extends Container {
    private final ModuleClickGUI master;
    private final PresetFrame frame;

    private float offsetX;
    private float offsetY;

    private float offsetW;
    private float offsetH;

    private boolean isCancellable;

    protected boolean refreshReleased;
    protected boolean saveReleased;
    protected boolean loadReleased;

    private ImperadorButton buttonRefresh;
    private ImperadorButton buttonSave;

    private ImperadorButton buttonLoad;
    private PresetListWidget presetListWidget;

    public Flag flagMouse = Flag.MOUSE_NOT_OVER;

    public PresetUI(ModuleClickGUI master, PresetFrame frame) {
        super("PresetUI:Container");

        this.master = master;
        this.frame = frame;
    }

    public void init() {
        this.buttonRefresh = new ImperadorButton(this.master.fontWidgetModule, "Refresh");

        this.buttonSave = new ImperadorButton(this.master.fontWidgetModule, "Save");
        this.buttonLoad = new ImperadorButton(this.master.fontWidgetModule, "Load");

        this.presetListWidget = new PresetListWidget(this.master, this.frame, this);
        this.presetListWidget.init();
    }

    public ModuleClickGUI getMaster() {
        return master;
    }

    public PresetFrame getFrame() {
        return frame;
    }

    public void setCancellable(boolean cancellable) {
        isCancellable = cancellable;
    }

    public boolean isCancellable() {
        return isCancellable;
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

    @Override
    public void onScreenClosed() {
        this.buttonRefresh.onScreenClosed();
        this.presetListWidget.onScreenClosed();
        this.presetListWidget.desync();
    }

    @Override
    public void onCustomScreenClosed() {

    }

    @Override
    public void onScreenOpened() {
        this.buttonRefresh.onScreenOpened();
        this.presetListWidget.onScreenOpened();
    }

    @Override
    public void onCustomScreenOpened() {

    }

    @Override
    public void onKeyboardPressed(char character, int key) {
        this.presetListWidget.onKeyboardPressed(character, key);
    }

    @Override
    public void onCustomKeyboardPressed(char character, int key) {
        this.presetListWidget.onCustomKeyboardPressed(character, key);
    }

    @Override
    public void onMouseReleased(int button) {
        this.buttonRefresh.onMouseReleased(button);
        this.buttonLoad.onMouseReleased(button);
        this.buttonSave.onMouseReleased(button);
        this.presetListWidget.onMouseReleased(button);
    }

    @Override
    public void onCustomMouseReleased(int button) {

    }

    @Override
    public void onMouseClicked(int button) {
        this.buttonRefresh.onMouseClicked(button);
        this.buttonLoad.onMouseClicked(button);
        this.buttonSave.onMouseClicked(button);
        this.presetListWidget.onMouseClicked(button);
    }

    @Override
    public void onCustomMouseClicked(int button) {

    }

    @Override
    public void onRender() {
        float space = 7 + TurokFontManager.getStringHeight(this.buttonRefresh.getFont(), this.buttonRefresh.getText()) + 7;

        this.rect.setX(0);
        this.rect.setY(this.frame.getRectList().getY() + this.frame.getRectList().getHeight());

        this.rect.setWidth(this.frame.getRect().getWidth());
        this.rect.setHeight(this.frame.getRect().getHeight());

        this.buttonRefresh.left(PresetFrame.SIZE);
        this.buttonRefresh.setOffsetY(2);

        this.buttonSave.getRect().setX(this.buttonRefresh.getRect().getX() + this.buttonRefresh.getRect().getWidth() + 1f);
        this.buttonSave.getRect().setY(this.rect.getY() + (space / 2f) - this.buttonSave.getRect().getHeight() / 2);

        this.buttonSave.left(PresetFrame.SIZE);
        this.buttonSave.setOffsetY(2);

        this.buttonLoad.getRect().setX(this.buttonSave.getRect().getX() + this.buttonSave.getRect().getWidth() + 1f);
        this.buttonLoad.getRect().setY(this.rect.getY() + (space / 2f) - this.buttonLoad.getRect().getHeight() / 2);

        this.buttonLoad.left(PresetFrame.SIZE);
        this.buttonLoad.setOffsetY(2);

        this.buttonRefresh.getRect().setX(this.rect.getX() + 3);
        this.buttonRefresh.getRect().setY(this.rect.getY() + (space / 2f) - this.buttonRefresh.getRect().getHeight() / 2);

        this.buttonRefresh.setRendering(true);
        this.buttonSave.setRendering(true);
        this.buttonLoad.setRendering(true);

        this.buttonRefresh.getRect().setWidth(TurokFontManager.getStringWidth(this.buttonRefresh.getFont(), this.buttonRefresh.getText()) + 8f);
        this.buttonRefresh.getRect().setHeight(this.buttonRefresh.getOffsetY() + TurokFontManager.getStringHeight(this.buttonRefresh.getFont(), this.buttonRefresh.getText()) + this.buttonRefresh.getOffsetY());

        this.buttonSave.getRect().setWidth(TurokFontManager.getStringWidth(this.buttonSave.getFont(), this.buttonSave.getText()) + 8f);
        this.buttonSave.getRect().setHeight(this.buttonSave.getOffsetY() + TurokFontManager.getStringHeight(this.buttonSave.getFont(), this.buttonSave.getText()) + this.buttonSave.getOffsetY());

        this.buttonLoad.getRect().setWidth(TurokFontManager.getStringWidth(this.buttonLoad.getFont(), this.buttonLoad.getText()) + 8f);
        this.buttonLoad.getRect().setHeight(this.buttonLoad.getOffsetY() + TurokFontManager.getStringHeight(this.buttonLoad.getFont(), this.buttonLoad.getText()) + this.buttonLoad.getOffsetY());

        this.buttonRefresh.doMouseOver(this.master.getMouse());

        this.buttonSave.doMouseOver(this.master.getMouse());
        this.buttonLoad.doMouseOver(this.master.getMouse());

        this.buttonRefresh.background = new int[] {20, 20, 20, (int) this.frame.getCurrentTickAlpha(255)};
        this.buttonRefresh.string = new int[] {255, 255, 255, (int) this.frame.getCurrentTickAlpha(255)};
        this.buttonRefresh.highlight = new int[] {255, 255, 255, (int) this.frame.getCurrentTickAlpha(30)};
        this.buttonRefresh.pressed = new int[] {Onepop.getWrapper().base[0], Onepop.getWrapper().base[1], Onepop.getWrapper().base[2], (int) this.frame.getCurrentTickAlpha(200)};
        this.buttonRefresh.outline = this.buttonRefresh.pressed;

        this.buttonSave.background = this.buttonRefresh.background;
        this.buttonSave.string = this.buttonRefresh.string;
        this.buttonSave.highlight = this.buttonRefresh.highlight;
        this.buttonSave.pressed = this.buttonRefresh.pressed;
        this.buttonSave.outline = this.buttonRefresh.pressed;

        this.buttonLoad.background = this.buttonRefresh.background;
        this.buttonLoad.string = this.buttonRefresh.string;
        this.buttonLoad.highlight = this.buttonRefresh.highlight;
        this.buttonLoad.pressed = this.buttonRefresh.pressed;
        this.buttonLoad.outline = this.buttonRefresh.pressed;

        this.buttonRefresh.setToDrawOutline(false);
        this.buttonRefresh.setIsShadow(false);
        this.buttonRefresh.setPartialTicks(this.master.getDisplay().getPartialTicks());
        this.buttonRefresh.setFont(this.master.fontWidgetModule);

        this.buttonSave.setToDrawOutline(false);
        this.buttonSave.setIsShadow(false);
        this.buttonSave.setPartialTicks(this.master.getDisplay().getPartialTicks());
        this.buttonSave.setFont(this.master.fontWidgetModule);

        this.buttonLoad.setToDrawOutline(false);
        this.buttonLoad.setIsShadow(false);
        this.buttonLoad.setPartialTicks(this.master.getDisplay().getPartialTicks());
        this.buttonLoad.setFont(this.master.fontWidgetModule);

        this.presetListWidget.getRect().setX(this.rect.getX());
        this.presetListWidget.getRect().setY(this.buttonRefresh.getRect().getY() + space + 1);

        this.buttonRefresh.onRender();

        this.buttonSave.onRender();
        this.buttonLoad.onRender();

        this.buttonRefresh.scissor();

        this.buttonSave.scissor();
        this.buttonLoad.scissor();

        this.presetListWidget.onRender();

        if (this.buttonRefresh.isReleased()) {
            if (this.buttonRefresh.isMouseOver()) {
                this.refreshReleased = true;
            }

            this.buttonRefresh.setReleased(false);
            this.setCancellable(false);
        }

        if (this.buttonLoad.isReleased()) {
            if (this.buttonLoad.isMouseOver()) {
                this.loadReleased = true;
            }

            this.buttonLoad.setReleased(false);
            this.setCancellable(false);
        }

        if (this.buttonSave.isReleased()) {
            if (this.buttonSave.isMouseOver()) {
                this.saveReleased = true;
            }

            this.buttonSave.setReleased(false);
            this.setCancellable(false);
        }

        if (this.saveReleased && this.buttonSave.lastTickPressedAlpha <= 10) {
            this.presetListWidget.sync();

            ChatUtil.print("Saving preset...");

            PresetManager.process(PresetManager.DATA);

            PresetManager.reload();
            PresetManager.process(PresetManager.SAVE);

            this.presetListWidget.refresh();
            this.saveReleased = false;
        }

        if (this.loadReleased && this.buttonLoad.lastTickPressedAlpha <= 10) {
            this.presetListWidget.sync();

            ChatUtil.print("Loading preset...");

            PresetManager.process(PresetManager.DATA);

            PresetManager.reload();
            PresetManager.process(PresetManager.LOAD);

            this.presetListWidget.refresh();
            this.loadReleased = false;
        }

        if (this.refreshReleased && this.buttonRefresh.lastTickPressedAlpha <= 10) {
            this.presetListWidget.sync();

            ChatUtil.print("Refresh preset list...");

            PresetManager.refresh();

            this.presetListWidget.refresh();
            this.refreshReleased = false;
        }
    }

    @Override
    public void onCustomRender() {

    }
}
