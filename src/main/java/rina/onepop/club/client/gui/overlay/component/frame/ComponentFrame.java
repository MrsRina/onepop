package rina.onepop.club.client.gui.overlay.component.frame;

import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.frame.Frame;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import rina.onepop.club.client.gui.overlay.ComponentClickGUI;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

/**
 * @author SrRina
 * @since 03/12/20 at 09:04pm
 */
public class ComponentFrame extends Frame {
    private ComponentClickGUI master;

    private rina.onepop.club.api.component.Component component;

    private int dragX;
    private int dragY;

    private boolean isMouseClickedLeft;

    private int alpha;

    public Flag flagMouse;

    public ComponentFrame(ComponentClickGUI master, Component component) {
        super(component.getName());

        this.master = master;
        this.component = component;

        this.flagMouse = Flag.MOUSE_NOT_OVER;
    }

    public boolean isEnabled() {
        return component.isEnabled();
    }

    @Override
    public boolean verifyFocus(int mx, int my) {
        boolean verified = false;

        if (isEnabled()) {
            if (this.component.getRect().collideWithMouse(this.master.getMouse())) {
                verified = true;
            }
        }

        return verified;
    }

    @Override
    public void onMouseReleased(int button) {
        if (this.isMouseClickedLeft) {
            this.isMouseClickedLeft = false;
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {
        this.master.moveFocusedFrameToTopMatrix();
    }

    @Override
    public void onMouseClicked(int button) {
        if (this.component.isEnabled()) {
            if (this.flagMouse == Flag.MOUSE_OVER) {
                if (button == 0) {
                    this.dragX = (int) (this.master.getMouse().getX() - this.component.getRect().getX());
                    this.dragY = (int) (this.master.getMouse().getY() - this.component.getRect().getY());

                    this.isMouseClickedLeft = true;
                }
            }
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {
        this.master.moveFocusedFrameToTopMatrix();
    }

    @Override
    public void onRender() {
        if (this.component.isEnabled()) {
            TurokRenderGL.color(0, 0, 0, 100);
            TurokRenderGL.drawSolidRect(this.component.getRect());

            if (this.flagMouse == Flag.MOUSE_OVER) {
                TurokRenderGL.color(255, 255, 255, 50);
                TurokRenderGL.drawSolidRect(this.component.getRect());
            }

            TurokRenderGL.color(0, 0, 255, this.alpha);
            TurokRenderGL.drawSolidRect(this.component.getRect());

            TurokShaderGL.drawOutlineRectFadingMouse(this.component.getRect(), 50, new Color(0, 0, 0, 255));

            GlStateManager.disableBlend();
            GlStateManager.disableTexture2D();

            this.component.onRender();

            if (this.isMouseClickedLeft) {
                this.component.getRect().setX(this.master.getMouse().getX() - dragX);
                this.component.getRect().setY(this.master.getMouse().getY() - dragY);

                this.alpha = 50;
                this.component.setDragging(true);
            } else {
                this.alpha = 0;
                this.component.setDragging(false);
            }
        }

        this.component.cornerDetector();
    }

    @Override
    public void onCustomRender() {
        if (this.component.isEnabled()) {
            this.flagMouse = this.component.getRect().collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
        }
    }
}
