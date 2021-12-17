package rina.onepop.club.client.gui.module;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.TurokFont;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokDisplay;
import me.rina.turok.util.TurokMath;
import me.rina.turok.util.TurokTick;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.frame.Frame;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.client.Wrapper;
import rina.onepop.club.client.gui.module.category.CategoryFrame;
import rina.onepop.club.client.gui.module.preset.PresetFrame;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author SrRina
 * @since 17/11/20 at 11:12am
 */
public class ModuleClickGUI extends GuiScreen {
    public TurokDisplay display;
    public TurokMouse mouse;

    public Wrapper guiColor;
    public ArrayList<rina.onepop.club.api.gui.frame.Frame> loadedFrameList;

    private int animationX;
    private int animationY;

    private int closedWidth;
    private boolean isClosingGUI;
    private boolean isEventing;
    private boolean isOverFrame;

    private String descriptionViewer;
    private final TurokTick timerDescriptionViewer = new TurokTick();

    private float animationDescriptionViewerAlpha;
    private float animationDescriptionViewerStringAlpha;
    private float animationDescriptionViewerRoundedAlpha;

    /*
     * We need get the focused frame to render things widgets no glitch.
     */
    private rina.onepop.club.api.gui.frame.Frame focusedFrame;

    private boolean isCanceledCloseGUI;

    private PresetFrame presetFrame;

    /*
     * Fonts used in GUI.
     */
    public TurokFont fontFrameCategory = new TurokFont(new Font("Whitney", 0, 24), true, true);
    public TurokFont fontWidgetModule = new TurokFont(new Font("Whitney", 0, 16), true, true);

    public ModuleClickGUI() {
        this.guiColor = new Wrapper();
        this.mouse = new TurokMouse();
    }

    public void init() {
        this.loadedFrameList = new ArrayList<>();

        int cacheX = 10;

        /*
         * List the categories and create widgets and registry them in loaded frame list.
         */
        for (ModuleCategory categories : ModuleCategory.values()) {
            CategoryFrame categoryFrame = new CategoryFrame(this, categories);

            categoryFrame.getRect().setX(cacheX);
            categoryFrame.getRect().setY(1);

            this.loadedFrameList.add(categoryFrame);

            cacheX += categoryFrame.getOffsetWidth() + 1;

            this.focusedFrame = categoryFrame;
        }

        this.presetFrame = new PresetFrame(this);
        this.presetFrame.init();
    }

    public void matrixMoveFocusedFrameToLast() {
        if (this.focusedFrame == null) {
            return;
        }

        this.loadedFrameList.remove(this.focusedFrame);
        this.loadedFrameList.add(this.focusedFrame);
    }

    public void setCanceledCloseGUI(boolean canceledCloseGUI) {
        isCanceledCloseGUI = canceledCloseGUI;
    }

    public boolean isCanceledCloseGUI() {
        return isCanceledCloseGUI;
    }

    public TurokDisplay getDisplay() {
        return display;
    }

    public TurokMouse getMouse() {
        return mouse;
    }

    public void onSaveList() {
        for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
            frames.onSave();
        }
    }

    public void onLoadList() {
        for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
            frames.onLoad();
        }
    }

    public void setClosingGUI(boolean closingGUI) {
        isClosingGUI = closingGUI;
    }

    public boolean isClosingGUI() {
        return isClosingGUI;
    }

    public void setClosedWidth(int closedWidth) {
        this.closedWidth = closedWidth;
    }

    public int getClosedWidth() {
        return closedWidth;
    }

    public void setOverFrame(boolean overFrame) {
        isOverFrame = overFrame;
    }

    public boolean isOverFrame() {
        return isOverFrame;
    }

    public void refreshDescriptionViewer(String description) {
        this.descriptionViewer = description;
    }

    public PresetFrame getPresetFrame() {
        return presetFrame;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        if (rina.onepop.club.client.module.client.ModuleClickGUI.INSTANCE.isEnabled()) {
            rina.onepop.club.client.module.client.ModuleClickGUI.INSTANCE.setDisabled();
        }

        /*
         * List frames in a for to make event on GUI closed.
         */
        for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
            frames.onScreenClosed();
        }

        /*
         * Not remove for prevents null pointer when init again the focused frame keep normal.
         */
        if (this.focusedFrame != null) {
            this.focusedFrame.onCustomScreenClosed();
        }

        this.presetFrame.onScreenClosed();
        this.presetFrame.onCustomScreenClosed();
    }

    @Override
    public void initGui() {
        if (this.isClosingGUI) {
            this.isClosingGUI = false;
        }

        /*
         * All frames when GUI is open.
         */
        for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
            frames.onScreenOpened();
        }

        if (this.focusedFrame != null) {
            this.focusedFrame.onCustomScreenOpened();
        }

        this.presetFrame.onScreenOpened();
        this.presetFrame.onCustomScreenOpened();
    }

    protected void keyTyped(char charCode, int keyCode) throws IOException {
        /*
         * Cancel the escape for close GUI and override the keyboard.
         */
        if (this.isCanceledCloseGUI) {
            this.presetFrame.onCustomKeyboardPressed(charCode, keyCode);

            if (this.focusedFrame != null) {
                this.focusedFrame.onCustomKeyboardPressed(charCode, keyCode);
            }
        } else {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                this.isClosingGUI = true;
            }
        }

        for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
            frames.onKeyboardPressed(charCode, keyCode);
        }

        this.presetFrame.onKeyboardPressed(charCode, keyCode);
    }

    @Override
    public void mouseReleased(int mx, int my, int button) {
        for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
            frames.onMouseReleased(button);

            if (frames instanceof CategoryFrame) {
                CategoryFrame categoryFrame = (CategoryFrame) frames;

                if (categoryFrame.verify(this.mouse)) {
                    this.focusedFrame = categoryFrame;
                }
            }
        }

        if (this.focusedFrame != null) {
            this.focusedFrame.onCustomMouseReleased(button);
        }

        this.presetFrame.onMouseReleased(button);
        this.presetFrame.onCustomMouseReleased(button);
    }

    @Override
    public void mouseClicked(int mx, int my, int button) {
        for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
            frames.onMouseClicked(button);

            if (frames instanceof CategoryFrame) {
                CategoryFrame categoryFrame = (CategoryFrame) frames;

                if (categoryFrame.verify(this.mouse)) {
                    this.focusedFrame = categoryFrame;
                }
            }
        }

        if (this.focusedFrame != null) {
            this.focusedFrame.onCustomMouseClicked(button);
        }

        this.presetFrame.onMouseClicked(button);
        this.presetFrame.onCustomMouseClicked(button);
    }

    public void handleMouseInput() throws IOException {
        if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            if (Mouse.getEventDWheel() > 0) {
                for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
                    frames.getRect().y += 10;
                }
            }

            if (Mouse.getEventDWheel() < 0) {
                for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
                    frames.getRect().y -= 10;
                }
            }
        }

        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int mx, int my, float partialTicks) {
        /*
         * Init display, mouse & stuff.
         */
        this.display = new TurokDisplay(mc);
        this.display.setPartialTicks(partialTicks);

        this.fontFrameCategory = Onepop.getWrapper().fontBigWidget;
        this.fontWidgetModule = Onepop.getWrapper().fontSmallWidget;

        this.mouse.setPos(mx, my);
        this.guiColor.onUpdateColor();

        int closingValueCalculated = 10;

        if (this.isClosingGUI) {
            this.closedWidth = (int) TurokMath.serp(this.closedWidth, 0, partialTicks);

            if (this.closedWidth <= closingValueCalculated) {
                this.onGuiClosed();

                mc.setIngameFocus();
                mc.displayGuiScreen(null);
            }
        } else {
            this.closedWidth = (int) TurokMath.serp(this.closedWidth, this.display.getScaledWidth() + 10, this.display.getPartialTicks());
        }

        TurokShaderGL.init(this.display, this.mouse);

        if (rina.onepop.club.client.module.client.ModuleClickGUI.settingBackGround.getValue()) {
            this.drawDefaultBackground();
        }

        /*
         * Auto scale so, fix the screen.
         */
        TurokGL.pushMatrix();
        TurokGL.translate(this.display.getScaledWidth(), this.display.getScaledHeight());
        TurokGL.scale(0.5f, 0.5f, 0.5f);
        TurokGL.popMatrix();

        /*
         * Disable texture 2D to render rect.
         */
        TurokGL.disable(GL11.GL_TEXTURE_2D);

        this.isOverFrame = false;
        this.descriptionViewer = "";

        this.focusedFrame = null;

        /*
         * We cal all frames to render.
         */
        for (Frame frames : this.loadedFrameList) {
            TurokShaderGL.pushScissor();
            TurokShaderGL.drawScissor(0, 0, this.closedWidth, this.display.getScaledHeight());

            frames.onRender();

            TurokShaderGL.popScissor();

            if (frames instanceof CategoryFrame) {
                CategoryFrame categoryFrame = (CategoryFrame) frames;

                /*
                 * Verify if mouse is over on focus frame.
                 */
                if (categoryFrame.verify(this.mouse)) {
                    this.focusedFrame = categoryFrame;
                    this.isOverFrame = true;
                }

                /*
                 * Disable flag to mouse, so, no glitches.
                 */
                categoryFrame.flagOffsetMouse = Flag.MOUSE_NOT_OVER;
                categoryFrame.flagMouse = Flag.MOUSE_NOT_OVER;
            }
        }

        if (this.focusedFrame != null) {
            this.focusedFrame.onCustomRender();
        }

        if (this.descriptionViewer.isEmpty()) {
            this.timerDescriptionViewer.reset();
        }

        final boolean showDescriptionViewer = this.timerDescriptionViewer.isPassedMS(750);

        this.animationDescriptionViewerAlpha = TurokMath.lerp(this.animationDescriptionViewerAlpha, showDescriptionViewer ? 255 : 0, partialTicks);
        this.animationDescriptionViewerStringAlpha = TurokMath.lerp(this.animationDescriptionViewerStringAlpha, showDescriptionViewer ? 200 : 0, partialTicks);
        this.animationDescriptionViewerRoundedAlpha = TurokMath.lerp(this.animationDescriptionViewerRoundedAlpha, showDescriptionViewer ? 255 : 0, partialTicks);

        final int descriptionWidth = TurokFontManager.getStringWidth(Onepop.getWrapper().fontSmallWidget, this.descriptionViewer);
        final int descriptionHeight = TurokFontManager.getStringHeight(Onepop.getWrapper().fontSmallWidget, this.descriptionViewer);

        this.presetFrame.onRender();
        this.presetFrame.onCustomRender();

        TurokRenderGL.color(190, 190, 190, (int) this.animationDescriptionViewerRoundedAlpha);
        TurokRenderGL.drawOutlineRect((mx + descriptionWidth >= this.display.getScaledWidth() ? mx - descriptionWidth : mx + 6), my + 10, descriptionWidth, descriptionHeight + 1);

        TurokRenderGL.color(255, 255, 255, (int) this.animationDescriptionViewerAlpha);
        TurokRenderGL.drawSolidRect((mx + descriptionWidth >= this.display.getScaledWidth() ? mx - descriptionWidth : mx + 6), my + 10, descriptionWidth, descriptionHeight + 1);

        TurokGL.pushMatrix();
        GlStateManager.enableBlend();
        TurokGL.enable(GL11.GL_TEXTURE_2D);
        TurokRenderGL.color(0, 0, 0, (int) this.animationDescriptionViewerAlpha);
        TurokFontManager.renderNative(Onepop.getWrapper().fontSmallWidget, this.descriptionViewer, (mx + descriptionWidth >= this.display.getScaledWidth() ? mx - descriptionWidth : mx + 6), my + 10, false, new Color(255, 255, 255));
        TurokGL.disable(GL11.GL_TEXTURE_2D);
        TurokGL.popMatrix();

        /*
         * Enable again for screen matrix.
         */
        TurokGL.disable(GL11.GL_TEXTURE_2D);
        TurokGL.disable(GL11.GL_BLEND);

        TurokGL.enable(GL11.GL_TEXTURE_2D);
    }
}
