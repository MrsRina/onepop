package rina.onepop.club.client.gui.overlay;

import rina.onepop.club.Onepop;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.frame.Frame;
import rina.onepop.club.client.gui.module.category.CategoryFrame;
import rina.onepop.club.client.gui.overlay.component.frame.ComponentFrame;
import rina.onepop.club.client.gui.overlay.component.frame.ComponentListFrame;
import rina.onepop.club.client.module.client.ModuleHUD;
import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.TurokFont;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokDisplay;
import me.rina.turok.util.TurokMath;
import rina.onepop.club.client.Wrapper;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import rina.onepop.club.client.Wrapper;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author SrRina
 * @since 1/12/20 at 03:21pm
 */
public class ComponentClickGUI extends GuiScreen {
    private TurokDisplay display;
    private TurokMouse mouse;

    public Wrapper guiColor;

    private ArrayList<rina.onepop.club.api.gui.frame.Frame> loadedFrameList;
    private rina.onepop.club.api.gui.frame.Frame focusedFrame;

    private int closedWidth;
    private boolean isClosingGUI;
    private boolean isEventing;

    public TurokFont fontComponentListFrame = new TurokFont(new Font("Whitney", 0, 24), true, true);
    public TurokFont fontWidgetComponent = new TurokFont(new Font("Whitney", 0, 16), true, true);

    public ComponentClickGUI() {
        this.guiColor = new Wrapper();
        this.mouse = new TurokMouse();

        this.init();
    }

    public void init() {
        this.loadedFrameList = new ArrayList<>();

        ComponentListFrame componentListFrame = new ComponentListFrame(this, "Component HUD");

        this.loadedFrameList.add(componentListFrame);

        for (Component components : Onepop.getComponentManager().getComponentList()) {
            ComponentFrame componentFrame = new ComponentFrame(this, components);

            this.loadedFrameList.add(componentFrame);
        }
    }

    public TurokMouse getMouse() {
        return mouse;
    }

    public TurokDisplay getDisplay() {
        return display;
    }

    public void moveFocusedFrameToTopMatrix() {
        if (this.focusedFrame != null) {
            this.loadedFrameList.remove(this.focusedFrame);
            this.loadedFrameList.add(this.focusedFrame);
        }
    }

    public void setClosingGUI(boolean closingGUI) {
        isClosingGUI = closingGUI;
    }

    public boolean isClosingGUI() {
        return isClosingGUI;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        ModuleHUD.INSTANCE.setDisabled();

        for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
            frames.onScreenClosed();
        }

        if (this.focusedFrame != null) {
            this.focusedFrame.onCustomScreenClosed();
        }
    }

    @Override
    public void initGui() {
        if (this.isClosingGUI) {
            this.isClosingGUI = false;
        }

        for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
            frames.onScreenOpened();
        }

        if (this.focusedFrame != null) {
            this.focusedFrame.onCustomScreenOpened();
        }
    }

    @Override
    public void keyTyped(char charCode, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.isClosingGUI = true;
        }
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
    }

    @Override
    public void drawScreen(int mx, int my, float partialTicks) {
        this.guiColor.onUpdateColor();

        this.display = new TurokDisplay(mc);
        this.display.setPartialTicks(partialTicks);

        this.mouse.setPos(mx, my);

        TurokShaderGL.init(display, this.mouse);

        this.fontComponentListFrame = Onepop.getWrapper().fontBigWidget;
        this.fontWidgetComponent = Onepop.getWrapper().fontSmallWidget;

        /*
         * Auto scale so, fix the screen.
         */
        TurokGL.pushMatrix();
        TurokGL.translate(this.display.getScaledWidth(), this.display.getScaledHeight());
        TurokGL.scale(0.5f, 0.5f, 0.5f);
        TurokGL.popMatrix();

        TurokGL.disable(GL11.GL_TEXTURE_2D);

        for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
            frames.onRender();

            if (frames.verifyFocus(mx, my)) {
                this.focusedFrame = frames;
            }

            if (frames instanceof ComponentListFrame) {
                ((ComponentListFrame) frames).flagMouse = Flag.MOUSE_NOT_OVER;
                ((ComponentListFrame) frames).flagOffsetMouse = Flag.MOUSE_NOT_OVER;
            }

            if (frames instanceof ComponentFrame) {
                ((ComponentFrame) frames).flagMouse = Flag.MOUSE_NOT_OVER;
            }
        }

        if (this.focusedFrame != null) {
            this.focusedFrame.onCustomRender();
        }

        TurokGL.disable(GL11.GL_TEXTURE_2D);
        TurokGL.disable(GL11.GL_BLEND);

        TurokGL.enable(GL11.GL_TEXTURE_2D);
        //TurokGL.color(255, 255, 255);

        int closingValueCalculated = 10;

        if (this.isClosingGUI) {
            this.closedWidth = (int) TurokMath.serp(this.closedWidth, 0, this.display.getPartialTicks());

            if (this.closedWidth <= closingValueCalculated) {
                if (this.isEventing) {
                    this.onGuiClosed();

                    mc.setIngameFocus();
                    mc.displayGuiScreen(null);

                    this.isEventing = false;
                }
            }
        } else {
            this.closedWidth = (int) TurokMath.serp(this.closedWidth, this.display.getScaledWidth() + 10, this.display.getPartialTicks());
            this.isEventing = true;
        }
    }

    public void onSaveAll() {
        for (rina.onepop.club.api.gui.frame.Frame frames : this.loadedFrameList) {
            if (frames instanceof ComponentListFrame) {
                ComponentListFrame componentListFrame = (ComponentListFrame) frames;

                componentListFrame.onSave();
            }
        }
    }

    public void onLoadAll() {
        for (Frame frames : this.loadedFrameList) {
            if (frames instanceof ComponentListFrame) {
                ComponentListFrame componentListFrame = (ComponentListFrame) frames;

                componentListFrame.onLoad();
            }
        }
    }
}
