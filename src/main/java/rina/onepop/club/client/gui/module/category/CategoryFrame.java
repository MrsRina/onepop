package rina.onepop.club.client.gui.module.category;

import com.google.gson.*;
import me.rina.turok.hardware.mouse.TurokMouse;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokRect;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.frame.Frame;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.api.module.impl.ModuleCategory;
import rina.onepop.club.client.gui.module.ModuleClickGUI;
import rina.onepop.club.client.gui.module.module.container.ModuleScrollContainer;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author SrRina
 * @since 18/11/20 at 7:00pm
 */
public class CategoryFrame extends Frame {
    private ModuleClickGUI master;
    private ModuleCategory category;

    private int offsetX;
    private int offsetY;

    private int offsetWidth;
    private int offsetHeight;

    private float dragX;
    private float dragY;

    public TurokRect rectOffset;

    /*
     * Maximum count for module.
     */
    private int maximumModule;

    /*
     * Loaded widget list like buttons & stuffs.
     */
    private ArrayList<Widget> loadedWidgetList;

    private boolean isButtonMouseLeftClicked;
    private boolean isAbleToScissor;

    /*
     * Container scroll to modules and widgets.
     */
    public ModuleScrollContainer scrollContainer;

    /* Flags. */
    public Flag flagOffsetMouse;
    public Flag flagMouse;

    public CategoryFrame(ModuleClickGUI master, ModuleCategory category) {
        super(category.getTag());

        this.master = master;
        this.category = category;

        this.offsetX = 2;
        this.offsetY = 1;

        this.offsetWidth = 102;
        this.offsetHeight = 2 + TurokFontManager.getStringHeight(this.master.fontFrameCategory, this.rect.getTag()) + 2 + 2;

        this.flagOffsetMouse = Flag.MOUSE_NOT_OVER;
        this.flagMouse = Flag.MOUSE_NOT_OVER;

        // This isn't needed anymore because rina decided NOT to do scrolling. Setting to funy number because that shouldn't be reached :)
        // Not more bubby, for onepop yes! kiss!
        this.maximumModule = 12;
        this.isAbleToScissor = false;

        this.init();
    }

    public void init() {
        this.rect.setWidth(offsetWidth);

        this.loadedWidgetList = new ArrayList<>();

        this.rect.setHeight(offsetHeight);

        this.scrollContainer = new ModuleScrollContainer(this.master, this);

        /*
         * We create a offset rect to title, so we can drag if mouse over.
         */
        this.rectOffset = new TurokRect("flag", 0, 0);
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public int getMaximumModule() {
        return maximumModule;
    }

    /*
     * Verify the mouse over for focused frame.
     */
    public boolean verify(TurokMouse mouse) {
        boolean verified = false;

        if (this.master.getPresetFrame().flagMouse == Flag.MOUSE_OVER) {
            return false;
        }

        if (this.rect.collideWithMouse(mouse)) {
            verified = true;
        }

        if (this.flagOffsetMouse == Flag.MOUSE_OVER) {
            verified = true;
        }

        return verified;
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

    public void setAbleToScissor(boolean ableToScissor) {
        isAbleToScissor = ableToScissor;
    }

    public boolean isAbleToScissor() {
        return isAbleToScissor;
    }

    @Override
    public void onScreenOpened() {
        this.scrollContainer.onScreenOpened();
    }

    @Override
    public void onCustomScreenOpened() {
        this.scrollContainer.onCustomScreenOpened();
    }

    @Override
    public void onScreenClosed() {
        /*
         * Turn off some events things.
         */
        this.isButtonMouseLeftClicked = false;

        this.scrollContainer.onScreenClosed();
    }

    @Override
    public void onCustomScreenClosed() {
        this.scrollContainer.onCustomScreenClosed();
    }

    @Override
    public void onKeyboardPressed(char charCode, int keyCode) {
        this.scrollContainer.onKeyboardPressed(charCode, keyCode);
    }

    @Override
    public void onCustomKeyboardPressed(char charCode, int keyCode) {
        this.scrollContainer.onCustomKeyboardPressed(charCode, keyCode);
    }

    @Override
    public void onMouseReleased(int button) {
        /*
         * Disable when true for dragging.
         */
        if (isButtonMouseLeftClicked) {
            this.isButtonMouseLeftClicked = false;
        }

        this.scrollContainer.onMouseReleased(button);
    }

    @Override
    public void onCustomMouseReleased(int button) {
        /*
         * Refresh the matrix of loadedFrameList.
         */
        this.master.matrixMoveFocusedFrameToLast();

        this.scrollContainer.onCustomMouseReleased(button);
    }

    @Override
    public void onMouseClicked(int button) {
        /*
         * Verify stuff & flags.
         */
        if (this.flagOffsetMouse == Flag.MOUSE_OVER) {
            if (button == 0) {
                this.dragX = this.master.getMouse().getX() - this.rect.getX();
                this.dragY = this.master.getMouse().getY() - this.rect.getY();

                this.isButtonMouseLeftClicked = true;
            }
        }

        this.scrollContainer.onMouseClicked(button);
    }

    @Override
    public void onCustomMouseClicked(int button) {
        /*
         * Refresh the matrix of loadedFrameList.
         */
        this.master.matrixMoveFocusedFrameToLast();
        this.scrollContainer.onCustomMouseClicked(button);
    }

    @Override
    public void onRender() {
        // Just positions update.
        this.rectOffset.setX(this.rect.getX());
        this.rectOffset.setY(this.rect.getY());

        // And sizes update.
        this.rectOffset.setWidth(this.rect.getWidth());
        this.rectOffset.setHeight(this.offsetHeight);

        // The shadow effect.
        this.doShadowDrop(this.rect.getX(), this.rect.getY(), this.rect.getWidth(), this.rect.getHeight(),15f);

        /*
         * Background frame.
         */
        TurokRenderGL.color(this.master.guiColor.background[0], this.master.guiColor.background[1], this.master.guiColor.background[2], this.master.guiColor.background[3]);
        TurokRenderGL.drawSolidRect(this.rect);

        if (this.isButtonMouseLeftClicked) {
            this.rect.setX(this.master.getMouse().getX() - this.dragX);
            this.rect.setY(this.master.getMouse().getY() - this.dragY);
        }

        /*
         * Render the animation of the rect.
         */
        TurokShaderGL.drawSolidRectFadingMouse(this.rect.getX(), this.rectOffset.getY() + this.rectOffset.getHeight() - 2, offsetX + TurokFontManager.getStringWidth(this.master.fontFrameCategory, this.rect.getTag()) + 2, 1, 50, new Color(this.master.guiColor.base[0], this.master.guiColor.base[1], this.master.guiColor.base[2], 255));

        /*
         * Render the effect fading so  we have the effect.
         */
        TurokShaderGL.drawOutlineRectFadingMouse(this.rect, 20, new Color(this.master.guiColor.background[0], this.master.guiColor.background[1], this.master.guiColor.background[2], 255));

        /*
         * Render title.
         */
        TurokFontManager.render(this.master.fontFrameCategory, this.rect.getTag(), this.rect.getX() + offsetX, this.rect.getY() + offsetY, true, new Color(255, 255, 255));

        /*
         * Render the scroll stuff.
         */
        this.scrollContainer.onRender();
    }

    @Override
    public void onCustomRender() {
        this.flagOffsetMouse = this.rectOffset.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;

        this.scrollContainer.onCustomRender();
    }

    public void onSave() {
        try {
            String pathFolder = Onepop.PATH_CONFIG + "GUI/";
            String pathFile = pathFolder + this.rect.getTag() + ".json";

            Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();

            if (!Files.exists(Paths.get(pathFolder))) {
                Files.createDirectories(Paths.get(pathFolder));
            }

            if (Files.exists(Paths.get(pathFile))) {
                File file = new File(pathFile);
                file.delete();
            }

            Files.createFile(Paths.get(pathFile));

            JsonObject mainJson = new JsonObject();

            mainJson.add("x", new JsonPrimitive(this.rect.getX()));
            mainJson.add("y", new JsonPrimitive(this.rect.getY()));

            String stringJson = gsonBuilder.toJson(new JsonParser().parse(mainJson.toString()));

            OutputStreamWriter fileOutputStream = new OutputStreamWriter(new FileOutputStream(pathFile), "UTF-8");

            fileOutputStream.write(stringJson);
            fileOutputStream.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void onLoad() {
        try {
            String pathFolder = Onepop.PATH_CONFIG + "GUI/";
            String pathFile = pathFolder + this.rect.getTag() + ".json";

            if (!Files.exists(Paths.get(pathFile))) {
                return;
            }

            InputStream file = Files.newInputStream(Paths.get(pathFile));

            JsonObject mainJson = new JsonParser().parse(new InputStreamReader(file)).getAsJsonObject();

            if (mainJson.get("x") != null) this.rect.setX(mainJson.get("x").getAsInt());
            if (mainJson.get("y") != null) this.rect.setY(mainJson.get("y").getAsInt());

            file.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void doShadowDrop(float theX, float theY, float theW, float theH, float l) {

    }
}
