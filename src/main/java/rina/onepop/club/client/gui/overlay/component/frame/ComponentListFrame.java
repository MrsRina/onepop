package rina.onepop.club.client.gui.overlay.component.frame;

import com.google.gson.*;
import rina.onepop.club.Onepop;
import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.gui.flag.Flag;
import rina.onepop.club.api.gui.frame.Frame;
import rina.onepop.club.api.gui.widget.Widget;
import rina.onepop.club.client.gui.overlay.component.widget.ComponentWidget;
import me.rina.turok.render.font.management.TurokFontManager;
import me.rina.turok.render.opengl.TurokRenderGL;
import me.rina.turok.render.opengl.TurokShaderGL;
import me.rina.turok.util.TurokRect;
import rina.onepop.club.client.gui.module.module.widget.ModuleWidget;
import rina.onepop.club.client.gui.overlay.ComponentClickGUI;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author SrRina
 * @since 01/12/20 at 10:41pm
 */
public class ComponentListFrame extends Frame {
    private ComponentClickGUI master;

    private int offsetX;
    private int offsetY;

    private int offsetWidth;
    private int offsetHeight;

    private int dragX;
    private int dragY;

    public TurokRect rectOffset;

    private ArrayList<Widget> loadedWidgetList;

    private boolean isButtonMouseLeftClicked;

    public Flag flagMouse;
    public Flag flagOffsetMouse;

    public ComponentListFrame(ComponentClickGUI master, String tag) {
        super(tag);

        this.master = master;

        this.rectOffset = new TurokRect("Offset", 0, 0);

        this.rect.setWidth(102);

        this.offsetX = 1;
        this.offsetY = 1;

        this.offsetWidth = 112;
        this.offsetHeight = 2 + TurokFontManager.getStringHeight(this.master.fontComponentListFrame, this.rect.getTag()) + 2 + 2;

        this.init();
    }

    public void init() {
        this.loadedWidgetList = new ArrayList<>();

        this.rect.setHeight(this.offsetHeight);

        for (Component components : Onepop.getComponentManager().getComponentList()) {
            ComponentWidget moduleWidget = new ComponentWidget(this.master, this, components);

            moduleWidget.setOffsetY((int) this.rect.getHeight());

            this.loadedWidgetList.add(moduleWidget);

            this.rect.height += moduleWidget.getRect().getHeight() + 1;
        }
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

    public void setButtonMouseClickedLeft(boolean mouseClickedLeft) {
        isButtonMouseLeftClicked = mouseClickedLeft;
    }

    public boolean isButtonMouseClickedLeft() {
        return isButtonMouseLeftClicked;
    }

    public void refresh() {
        this.rect.height = 0;

        this.rect.setHeight(offsetHeight);

        for (Widget widgets : this.loadedWidgetList) {
            if (widgets instanceof ComponentWidget) {
                ComponentWidget moduleWidget = (ComponentWidget) widgets;

                moduleWidget.setOffsetY((int) this.rect.getHeight());

                if (moduleWidget.isWidgetOpened()) {
                    this.rect.height += moduleWidget.getOffsetHeight();
                } else {
                    this.rect.height += moduleWidget.getRect().getHeight() + 1;
                }
            }
        }
    }

    @Override
    public boolean verifyFocus(int mx, int my) {
        boolean verified = false;

        if (this.rect.collideWithMouse(this.master.getMouse())) {
            verified = true;
        }

        return verified;
    }

    @Override
    public void onScreenOpened() {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onScreenOpened();
        }
    }

    @Override
    public void onCustomScreenOpened() {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onCustomScreenOpened();
        }
    }

    @Override
    public void onScreenClosed() {
        this.isButtonMouseLeftClicked = false;

        for (Widget widgets : this.loadedWidgetList) {
            widgets.onScreenClosed();
        }
    }

    @Override
    public void onCustomScreenClosed() {
        for (Widget widgets : this.loadedWidgetList) {
            widgets.onCustomScreenClosed();
        }
    }

    @Override
    public void onKeyboardPressed(char charCode, int keyCode) {

    }

    @Override
    public void onCustomKeyboardPressed(char charCode, int keyCode) {

    }

    @Override
    public void onMouseReleased(int button) {
        if (isButtonMouseLeftClicked) {
            this.isButtonMouseLeftClicked = false;
        }

        for (Widget widgets : this.loadedWidgetList) {
            widgets.onMouseReleased(button);
        }
    }

    @Override
    public void onCustomMouseReleased(int button) {
        this.master.moveFocusedFrameToTopMatrix();

        for (Widget widgets : this.loadedWidgetList) {
            widgets.onCustomMouseReleased(button);
        }
    }

    @Override
    public void onMouseClicked(int button) {
        /*
         * Verify stuff & flags.
         */
        if (this.flagOffsetMouse == Flag.MOUSE_OVER) {
            if (button == 0) {
                this.dragX = (int) (this.master.getMouse().getX() - this.rect.getX());
                this.dragY = (int) (this.master.getMouse().getY() - this.rect.getY());

                this.isButtonMouseLeftClicked = true;
            }
        }

        for (Widget widgets : this.loadedWidgetList) {
            widgets.onMouseClicked(button);
        }
    }

    @Override
    public void onCustomMouseClicked(int button) {
        /*
         * Refresh the matrix of loadedFrameList.
         */
        this.master.moveFocusedFrameToTopMatrix();
    }

    @Override
    public void onRender() {
        this.rectOffset.setX(this.rect.getX());
        this.rectOffset.setY(this.rect.getY());

        this.rectOffset.setWidth(this.rect.getWidth());
        this.rectOffset.setHeight(this.offsetHeight);

        TurokRenderGL.color(this.master.guiColor.background[0], this.master.guiColor.background[1], this.master.guiColor.background[2], this.master.guiColor.background[3]);
        TurokRenderGL.drawSolidRect(this.rect);

        if (this.isButtonMouseLeftClicked) {
            this.rect.setX(this.master.getMouse().getX() - this.dragX);
            this.rect.setY(this.master.getMouse().getY() - this.dragY);
        }

        // Outline effects.
        TurokShaderGL.drawOutlineRectFadingMouse(this.rect, 20, new Color(this.master.guiColor.background[0], this.master.guiColor.background[1], this.master.guiColor.background[2], 255));

        // Title.
        TurokFontManager.render(this.master.fontComponentListFrame, this.rect.getTag(), this.rect.getX() + offsetX, this.rect.getY() + offsetY, true, new Color(255, 255, 255));

        // The category...
        TurokShaderGL.drawSolidRectFadingMouse(this.rect.getX(), this.rectOffset.getY() + this.rectOffset.getHeight() - 2, offsetX + TurokFontManager.getStringWidth(this.master.fontComponentListFrame, this.rect.getTag()) + 2, 1, 50, new Color(this.master.guiColor.base[0], this.master.guiColor.base[1], this.master.guiColor.base[2], 255));

        for (Widget widgets : this.loadedWidgetList) {
            widgets.onRender();

            if (widgets instanceof ModuleWidget) {
                ModuleWidget moduleWidget = (ModuleWidget) widgets;

                moduleWidget.flagMouse = Flag.MOUSE_NOT_OVER;
            }
        }
    }

    @Override
    public void onCustomRender() {
        this.flagOffsetMouse = this.rectOffset.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;
        this.flagMouse = this.rect.collideWithMouse(this.master.getMouse()) ? Flag.MOUSE_OVER : Flag.MOUSE_NOT_OVER;

        for (Widget widgets : this.loadedWidgetList) {
            widgets.onCustomRender();
        }
    }

    public void onSave() {
        try {
            String pathFolder = "ONEPOPCLIENT/GUI/";
            String pathFile = pathFolder + this.rect.getTag() + ".json";

            Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();

            if (!Files.exists(Paths.get(pathFolder.toString()))) {
                Files.createDirectories(Paths.get(pathFolder.toString()));
            }

            if (!Files.exists(Paths.get(pathFile.toString()))) {
                Files.createFile(Paths.get(pathFile.toString()));
            } else {
                java.io.File file = new java.io.File(pathFile.toString());
                file.delete();
            }

            JsonObject mainJson = new JsonObject();

            mainJson.add("x", new JsonPrimitive(this.rect.getX()));
            mainJson.add("y", new JsonPrimitive(this.rect.getY()));

            String stringJson = gsonBuilder.toJson(new JsonParser().parse(mainJson.toString()));

            OutputStreamWriter fileOutputStream = new OutputStreamWriter(new FileOutputStream(pathFile.toString()), "UTF-8");

            fileOutputStream.write(stringJson);
            fileOutputStream.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void onLoad() {
        try {
            String pathFolder = "ONEPOPCLIENT/GUI/";
            String pathFile = pathFolder + this.rect.getTag() + ".json";

            if (!Files.exists(Paths.get(pathFile.toString()))) {
                return;
            }

            InputStream file = Files.newInputStream(Paths.get(pathFile.toString()));

            JsonObject mainJson = new JsonParser().parse(new InputStreamReader(file)).getAsJsonObject();

            if (mainJson.get("x") != null) this.rect.setX(mainJson.get("x").getAsInt());
            if (mainJson.get("y") != null) this.rect.setY(mainJson.get("y").getAsInt());

            file.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}