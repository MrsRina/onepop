package me.rina.turok.minecraft;

import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.opengl.TurokGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokClass;
import me.rina.turok.util.TurokDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author SrRina
 * @since 07/01/2021 at 12:41
 **/
@TurokGUI.GUI(name = "HDU editor")
public class TurokGUI extends GuiScreen {
    private final String name = get().name();
    private final String author = get().author();

    protected TurokMouse mouse;
    protected TurokDisplay display;

    protected float partialTicks;

    public TurokGUI() {
        // Init helpers.
        this.mouse = new TurokMouse();
        this.display = new TurokDisplay(Minecraft.getMinecraft());

        /*
         * Init shader utils.
         */
        TurokShaderGL.init(this.display, this.mouse);

        this.init();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface GUI {
        String name() default "Random";
        String author() default "Random";
    }

    public GUI get() {
        if (TurokClass.isAnnotationPreset(getClass(), GUI.class)) {
            return getClass().getAnnotation(GUI.class);
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public void setMouse(TurokMouse mouse) {
        this.mouse = mouse;
    }

    public TurokMouse getMouse() {
        return mouse;
    }

    public void setDisplay(TurokDisplay display) {
        this.display = display;
    }

    public TurokDisplay getDisplay() {
        return display;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    /**
     * Init the GUI.
     */
    public void init() {}

    /**
     * Calls when you want close the current GUI.
     */
    public void closeGUI() {
        this.onClose();

        mc.setIngameFocus();
        mc.displayGuiScreen(null);
    }

    /**
     * Pause game when GUI is enabled.
     *
     * @return return bool state to current ask.
     */
    public boolean pauseGameWhenActive() {
        return false;
    }

    /**
     * Whens GUI is opened this is called.
     */
    public void onOpen() {}

    /**
     * Whens GUI close this is called.
     */
    public void onClose() {}

    /**
     * Use for get current keyboard character or key pressed.
     *
     * @param character character typed from keyboard.
     * @param key key typed from keyboard.
     */
    public void onKeyboard(char character, int key) {}

    /**
     * Mouse up button event.
     *
     * @param button the current button of click.
     */
    public void onMouseClicked(int button) {}

    /**
     * Mouse down button event.
     *
     * @param button the current button of click.
     */
    public void onMouseReleased(int button) {}

    /**
     * Current render to GUI.
     */
    public void onRender() {}

    @Override
    public boolean doesGuiPauseGame() {
        return pauseGameWhenActive();
    }

    @Override
    public void initGui() {
        this.onOpen();
    }

    @Override
    public void keyTyped(char charCode, int keyCode) {
        this.onKeyboard(charCode, keyCode);
    }

    @Override
    public void mouseClicked(int mousePositionX, int mousePositionY, int mouseButtonUp) {
        this.onMouseClicked(mouseButtonUp);
    }

    @Override
    public void mouseReleased(int mousePositionX, int mousePositionY, int mouseButtonDown) {
        this.onMouseReleased(mouseButtonDown);
    }

    @Override
    public void drawScreen(int mousePositionX, int mousePositionY, float partialTicks) {
        this.display = new TurokDisplay(Minecraft.getMinecraft());

        TurokShaderGL.init(this.display, this.mouse);

        // Update the position wi0th mouse field.
        this.mouse.setPos(mousePositionX, mousePositionY);

        // Set the current partial ticks to variable.
        this.partialTicks = partialTicks;

        // We need fix the current matrix view to sync sizes display.
        TurokGL.pushMatrix();

        // Translate to display width, height.
        TurokGL.translate(this.display.getWidth(), this.display.getHeight());
        TurokGL.scale(0.5f, 0.5f, 0.5f);

        TurokGL.popMatrix();
        TurokGL.disable(GL11.GL_TEXTURE_2D);

        this.onRender();

        TurokGL.enable(GL11.GL_TEXTURE_2D);

        // Disable any texture 2d and blend
        TurokGL.disable(GL11.GL_TEXTURE_2D);
        TurokGL.disable(GL11.GL_BLEND);

        // Enable again to fix color.
        TurokGL.enable(GL11.GL_TEXTURE_2D);

        // Color to white.
        TurokGL.color(255, 255, 255);
    }
}
