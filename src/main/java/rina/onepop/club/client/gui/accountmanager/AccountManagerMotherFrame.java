package rina.onepop.club.client.gui.accountmanager;

import net.minecraft.client.gui.GuiScreen;
import rina.onepop.club.api.gui.frame.Frame;

/**
 * @author Rina
 * @since 07/10/2021 at 12:44pm
 **/
public class AccountManagerMotherFrame extends Frame {
    private GuiScreen master;

    private float dragX;
    private float dragY;

    private float resizeW;
    private float resizeH;

    public AccountManagerMotherFrame() {
        super("Mother:Frame:AccountManager");
    }

    public void setMaster(GuiScreen master) {
        this.master = master;
    }

    public GuiScreen getMaster() {
        return master;
    }

    public void setDragX(float dragX) {
        this.dragX = dragX;
    }

    public float getDragX() {
        return dragX;
    }

    public void setDragY(float dragY) {
        this.dragY = dragY;
    }

    public float getDragY() {
        return dragY;
    }

    public void setResizeW(float resizeW) {
        this.resizeW = resizeW;
    }

    public float getResizeW() {
        return resizeW;
    }

    public void setResizeH(float resizeH) {
        this.resizeH = resizeH;
    }

    public float getResizeH() {
        return resizeH;
    }

    @Override
    public void onScreenClosed() {}

    @Override
    public void onCustomScreenClosed() {}

    @Override
    public void onScreenOpened() {}

    @Override
    public void onCustomScreenOpened() {}

    @Override
    public void onCustomMouseClicked(int button) {}

    @Override
    public void onMouseClicked(int button) {}

    @Override
    public void onCustomMouseReleased(int button) {}

    @Override
    public void onRender() {

    }

    @Override
    public void onCustomRender() {

    }
}