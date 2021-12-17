package rina.onepop.club.client.gui.module.preset.widget;

import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokTick;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.api.preset.Preset;
import rina.onepop.club.api.util.file.FileUtil;
import rina.onepop.club.client.gui.imperador.ImperadorButton;
import rina.onepop.club.client.gui.imperador.ImperadorEntryBox;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.module.preset.PresetFrame;
import rina.onepop.club.client.gui.module.preset.ui.PresetUI;

import java.io.IOException;

/**
 * @author SrRina
 * @since 15/08/2021 at 17:45
 **/
public class PresetWidget extends Widget {
    private final ModuleClickGUI master;
    private final PresetFrame frame;

    private final PresetUI ui;
    private final PresetListWidget widget;

    private float offsetX;
    private float offsetY;

    private float offsetW;
    private float offsetH;

    private int alpha;
    private int lastTickAlpha;

    private float alphaHovered;
    private float lastTickAlphaHovered;

    private final Preset currentPreset;
    private final Preset thePreset;

    private ImperadorEntryBox entryBox;
    private final TurokTick pressTick = new TurokTick();

    private String lastText = "";
    protected boolean isConflictingWithAnotherPreset;

    protected boolean isEditing;
    protected boolean isUpdate;

    private boolean isDeleting;
    private ImperadorButton deleteButton;

    public Flag flag = Flag.MOUSE_NOT_OVER;

    public PresetWidget(ModuleClickGUI master, PresetFrame frame, PresetUI ui, PresetListWidget widget, Preset preset) {
        super(preset.getTag());

        this.master = master;
        this.frame = frame;

        this.ui = ui;
        this.widget = widget;

        // I created one copy of preset to manage and update shit.
        this.thePreset = preset;
        this.currentPreset = new Preset(this.thePreset.getTag(), this.thePreset.getData());

        if (this.thePreset.isCurrent()) {
            this.currentPreset.setValidator();
        }

        this.rect.setX(-1080);
    }

    public void init() {
        this.entryBox = new ImperadorEntryBox(this.master.fontWidgetModule, this.thePreset.getTag());
        this.deleteButton = new ImperadorButton(this.master.fontWidgetModule, "Remove");
    }

    public void sync() {
        this.thePreset.setData(this.currentPreset.getData());

        if (this.currentPreset.isCurrent()) {
            this.thePreset.setValidator();
        } else {
            this.thePreset.unsetValidator();
        }
    }

    public void desync() {
        this.currentPreset.setData(this.thePreset.getData());

        if (this.thePreset.isCurrent()) {
            this.currentPreset.setValidator();
        } else {
            this.currentPreset.unsetValidator();
        }
    }

    public Preset getCurrentPreset() {
        return currentPreset;
    }

    public Preset getThePreset() {
        return thePreset;
    }

    public void setLastText(String lastText) {
        this.lastText = lastText;
    }

    public String getLastText() {
        return lastText;
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

    public void setDeleting(boolean isDeleting) {
        this.isDeleting = isDeleting;
    }

    public boolean isDeleting() {
        return isDeleting;
    }

    @Override
    public void onScreenClosed() {

    }

    @Override
    public void onCustomScreenClosed() {

    }

    @Override
    public void onScreenOpened() {

    }

    @Override
    public void onCustomScreenOpened() {

    }

    @Override
    public void onKeyboardPressed(char keyChar, int keyCode) {
        this.entryBox.onCustomKeyboardPressed(keyChar, keyCode);
    }

    @Override
    public void onCustomKeyboardPressed(char keyChar, int keyCode) {
        this.entryBox.onKeyboardPressed(keyChar, keyCode);
    }

    @Override
    public void onMouseReleased(int button) {
        this.entryBox.onMouseReleased(button);
        this.deleteButton.onMouseReleased(button);
    }

    @Override
    public void onCustomMouseReleased(int button) {

    }

    @Override
    public void onMouseClicked(int button) {
        if (button == 0 || button == 1) {
            boolean tick = this.entryBox.isFocused();

            if (this.pressTick.isPassedMS(500)) {
                this.pressTick.reset();
            } else {
                tick = true;
            }

            if (tick) {
                this.entryBox.onMouseClicked(button);
                this.entryBox.doSetIndexAB(this.master.getMouse());
            }
        }

        this.deleteButton.onMouseClicked(button);

        if (this.flag == Flag.MOUSE_OVER && !this.entryBox.isFocused() && (button == 0 || button == 1)) {
            this.widget.current(this.currentPreset.getTag());
        }

        this.setDeleting(this.flag == Flag.MOUSE_OVER);
    }

    @Override
    public void onCustomMouseClicked(int button) {

    }

    @Override
    public void onRender() {
        this.deleteButton.getRect().setWidth(TurokFontManager.getStringWidth(this.deleteButton.getFont(), this.deleteButton.getText()) + 8f);
        this.deleteButton.getRect().setHeight(this.deleteButton.getOffsetY() + TurokFontManager.getStringHeight(this.deleteButton.getFont(), this.deleteButton.getText()) + this.deleteButton.getOffsetY());

        this.deleteButton.getRect().setX(this.rect.getX() + this.rect.getWidth() - 3f - this.deleteButton.getRect().getWidth());
        this.deleteButton.getRect().setY(this.rect.getY() + this.rect.getHeight() - 3f - this.deleteButton.getRect().getHeight());

        this.deleteButton.background = new int[] {20, 20, 20, (int) this.frame.getCurrentTickAlpha(255)};
        this.deleteButton.string = new int[] {255, 255, 255, (int) this.frame.getCurrentTickAlpha(255)};
        this.deleteButton.highlight = new int[] {255, 255, 255, (int) this.frame.getCurrentTickAlpha(30)};
        this.deleteButton.pressed = new int[] {Onepop.getWrapper().base[0], Onepop.getWrapper().base[1], Onepop.getWrapper().base[2], (int) this.frame.getCurrentTickAlpha(200)};
        this.deleteButton.outline = this.deleteButton.pressed;

        float heightWithDeleting = this.isDeleting() ? this.deleteButton.getRect().getHeight() + 4f : 0f;
        float height = PresetFrame.OFFSET + TurokFontManager.getStringHeight(this.master.fontWidgetModule, this.currentPreset.getTag()) + PresetFrame.OFFSET;

        this.rect.setWidth(this.frame.getRect().getWidth());
        this.rect.setHeight(height + heightWithDeleting);

        this.entryBox.getRect().setX(this.rect.getX() + 6);
        this.entryBox.getRect().setY(this.rect.getY() + (height / 2f) - (this.entryBox.getRect().getHeight() / 2f));

        this.entryBox.getRect().setWidth(TurokMath.clamp(this.entryBox.size() + 4f, 10, this.rect.getWidth() - (6 * 2)));
        this.entryBox.getRect().setHeight(3 + TurokFontManager.getStringHeight(this.master.fontWidgetModule, this.entryBox.getText()) + 3);

        this.entryBox.setToDrawOutline(false);
        this.entryBox.setRendering(true);
        this.entryBox.setOffsetY(3);
        this.entryBox.setPartialTicks(this.master.getDisplay().getPartialTicks());
        this.entryBox.setIsShadow(false);
        this.entryBox.setFont(this.master.fontWidgetModule);

        this.deleteButton.setRendering(this.isDeleting());
        this.deleteButton.setToDrawOutline(false);
        this.deleteButton.setOffsetY(2);
        this.deleteButton.setPartialTicks(this.master.getDisplay().getPartialTicks());
        this.deleteButton.setText("Remove");
        this.deleteButton.setIsShadow(false);
        this.deleteButton.setFont(this.master.fontWidgetModule);

        this.deleteButton.left(4f);
        this.entryBox.doMouseScroll(this.master.getMouse());

        // Here we add the insane...!
        if (this.widget.getScrollRect().collideWithMouse(this.master.getMouse())) {
            this.entryBox.doMouseOver(this.master.getMouse());
            this.deleteButton.doMouseOver(this.master.getMouse());

            this.flag = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
        } else {
            this.entryBox.setMouseOver(false);
            this.deleteButton.setMouseOver(false);

            this.flag = Flag.MOUSE_NOT_OVER;
        }

        if (this.entryBox.isFocused()) {
            this.isConflictingWithAnotherPreset = this.widget.contains(this.entryBox.getText()) > 1 || this.entryBox.isEmpty();

            if (!this.entryBox.getText().equals(this.entryBox.getSave())) {
                this.ui.setCancellable(true);
            }

            this.isEditing = true;
        }

        int theColorFlag = this.entryBox.isFocused() ? 0 : 255;
        int theConflictFlag = this.isConflictingWithAnotherPreset ? 0 : 255;

        this.entryBox.focused = new int[] {255, theConflictFlag, theConflictFlag, 255};
        this.entryBox.string = new int[] {theColorFlag, theColorFlag, theColorFlag, 255};

        this.alpha = this.currentPreset.isCurrent() ? (int) this.frame.getCurrentTickAlpha(200) : 0;
        this.lastTickAlpha = (int) TurokMath.lerp(this.lastTickAlpha, this.alpha, this.master.getDisplay().getPartialTicks());

        this.alphaHovered = this.flag == Flag.MOUSE_OVER ? (int) this.frame.getCurrentTickAlpha(50) : 0;
        this.lastTickAlphaHovered = TurokMath.lerp(this.lastTickAlphaHovered, this.alphaHovered, this.master.getDisplay().getPartialTicks());

        this.offsetH = (height / 2f);

        if (this.lastTickAlpha != 0) {
            TurokGL.color(Onepop.getWrapper().base[0], Onepop.getWrapper().base[1], Onepop.getWrapper().base[2], this.lastTickAlpha);
            TurokRenderGL.drawSolidRect(this.rect.getX(), this.rect.getY() + ((height / 2f) - (this.offsetH / 2f)), 2, this.offsetH);
        }

        if (this.lastTickAlphaHovered != 0f) {
            if (this.isDeleting()) {
                TurokGL.color(Onepop.getWrapper().base[0], Onepop.getWrapper().base[1], Onepop.getWrapper().base[2], (int) TurokMath.clamp(this.lastTickAlphaHovered, 0, 30));
            } else {
                TurokGL.color(255, 255, 255, (int) this.lastTickAlphaHovered);
            }

            TurokRenderGL.drawSolidRect(this.rect);
        }

        this.entryBox.onRender();
        this.entryBox.getScissor().set(this.entryBox.getRect().getX(), this.widget.getScrollRect().getY(), this.entryBox.getRect().getWidth(), this.widget.getScrollRect().getHeight());
        this.widget.scissor();

        this.deleteButton.onRender();
        this.deleteButton.getScissor().set(this.deleteButton.getRect().getX(), this.widget.getScrollRect().getY(), this.deleteButton.getRect().getWidth(), this.widget.getScrollRect().getHeight());
        this.widget.scissor();

        if (!this.entryBox.isFocused()) {
            if (this.isConflictingWithAnotherPreset) {
                this.entryBox.setText(this.lastText);
                this.isConflictingWithAnotherPreset = false;
            }

            if (this.isEditing) {
                try {
                    FileUtil.renameFolder(Onepop.PATH_PRESET + this.lastText, Onepop.PATH_PRESET + this.entryBox.getText());
                } catch (IOException exc) {}

                this.isEditing = false;
            }

            if (this.isUpdate) {
                this.frame.setCanceledToCloseFrame(false);
                this.isUpdate = false;
            }

            this.lastText = this.entryBox.getText();
        } else {
            this.frame.setCanceledToCloseFrame(true);
            this.isUpdate = true;
        }

        if (this.deleteButton.isReleased()) {
            if (this.deleteButton.isMouseOver()) {
                this.widget.delete(this.getCurrentPreset());
            }

            this.deleteButton.setReleased(false);
        }

        this.thePreset.setTag(this.entryBox.getText());
    }

    @Override
    public void onCustomRender() {

    }

}
